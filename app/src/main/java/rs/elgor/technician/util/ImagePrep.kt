package rs.elgor.technician.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Turns whatever the camera/gallery gave us (HEIC, huge 12 MP JPEG, rotated via EXIF...) into a
 * normal upright JPEG of at most [maxSide] pixels on its longest side. The server only accepts
 * JPEG/PNG/WebP up to 10 MB, and a field technician on mobile data should not upload 8 MB per photo.
 * Returns null if the file cannot be decoded as an image.
 */
suspend fun prepareJpeg(src: File, maxSide: Int = 1920): File? = withContext(Dispatchers.IO) {
    try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(src.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        var bmp = BitmapFactory.decodeFile(src.absolutePath, opts) ?: return@withContext null

        // honour the EXIF orientation (decoding drops it, photos would come out sideways)
        val rotation = when (
            ExifInterface(src.absolutePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val longest = maxOf(bmp.width, bmp.height)
        val scale = if (longest > maxSide) maxSide.toFloat() / longest else 1f
        if (rotation != 0f || scale != 1f) {
            val m = Matrix().apply {
                if (scale != 1f) postScale(scale, scale)
                if (rotation != 0f) postRotate(rotation)
            }
            val out = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
            if (out !== bmp) bmp.recycle()
            bmp = out
        }

        val dst = File(src.parentFile, "prep_${System.currentTimeMillis()}.jpg")
        var quality = 85
        while (true) {
            FileOutputStream(dst).use { bmp.compress(Bitmap.CompressFormat.JPEG, quality, it) }
            if (dst.length() <= 9L * 1024 * 1024 || quality <= 50) break
            quality -= 15
        }
        bmp.recycle()
        dst
    } catch (e: Exception) {
        null
    } catch (e: OutOfMemoryError) {
        null
    }
}
