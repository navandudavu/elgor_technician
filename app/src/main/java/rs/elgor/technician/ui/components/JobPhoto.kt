package rs.elgor.technician.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rs.elgor.technician.R
import rs.elgor.technician.data.JobRepository

// Job photos are served behind requireAuth on the backend (see
// backend/src/routes/jobs.js -> GET /jobs/photos/:filename), so a plain
// Coil AsyncImage pointed at the URL would 401 - Coil's own HTTP client
// doesn't know about our JWT. Same root problem the web app solved with
// fetchPhotoUrl() (fetch as blob, not <img src>): here we fetch the raw
// bytes through the same authenticated Retrofit client every other
// request uses, then decode them into a bitmap ourselves.
@Composable
fun JobPhoto(
    filename: String,
    repository: JobRepository,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var bitmap by remember(filename) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var failed by remember(filename) { mutableStateOf(false) }

    LaunchedEffect(filename) {
        bitmap = null
        failed = false
        repository.getPhotoBytes(filename).fold(
            onSuccess = { bytes ->
                val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (decoded != null) bitmap = decoded else failed = true
            },
            onFailure = { failed = true }
        )
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        val currentBitmap = bitmap
        when {
            currentBitmap != null -> Image(
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.job_photo_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            failed -> Text("!", color = MaterialTheme.colorScheme.error)
            else -> CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        }
    }
}
