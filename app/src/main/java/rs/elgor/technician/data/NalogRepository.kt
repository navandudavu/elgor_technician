package rs.elgor.technician.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import rs.elgor.technician.data.remote.NalogApi
import rs.elgor.technician.model.CreateNalogRequest
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.model.NalogCustomer
import rs.elgor.technician.model.TechnicianView
import rs.elgor.technician.model.NalogHistoryEntry
import rs.elgor.technician.model.NalogPage
import rs.elgor.technician.model.NalogPhotoView
import rs.elgor.technician.model.NalogStatusRequest
import rs.elgor.technician.model.ProblemDetail
import rs.elgor.technician.model.CreateNoteRequest
import rs.elgor.technician.model.NoteView
import rs.elgor.technician.model.NalogPreview
import rs.elgor.technician.model.PartPage
import rs.elgor.technician.model.PreviewRequest
import rs.elgor.technician.model.UpdateNalogRequest

class NalogRepository(private val api: NalogApi) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private suspend fun <T> unwrap(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body) else Result.failure(Exception("Prazan odgovor sa servera"))
            } else {
                val raw = response.errorBody()?.string()
                val problem = try {
                    moshi.adapter(ProblemDetail::class.java).fromJson(raw ?: "")
                } catch (e: Exception) {
                    null
                }
                val fieldMsg = problem?.errors?.entries?.joinToString("; ") { "${it.key}: ${it.value}" }
                val msg = when (response.code()) {
                    401 -> "Sesija je istekla. Prijavite se ponovo."
                    403 -> "Nemate dozvolu za ovu radnju."
                    404 -> "Radni nalog nije pronađen."
                    409 -> problem?.detail ?: "Nalog je u međuvremenu izmenjen. Otvorite ga ponovo i probajte opet."
                    else -> fieldMsg ?: problem?.detail ?: "Zahtev nije uspeo (${response.code()})"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: java.io.IOException) {
            Result.failure(Exception("Nije moguće pristupiti servisu za radne naloge. Proverite vezu i da li je servis pokrenut.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun list(status: String? = null, page: Int = 0): Result<NalogPage> =
        unwrap { api.list(status = status, page = page) }

    suspend fun get(id: Long): Result<Nalog> = unwrap { api.get(id) }

    suspend fun create(request: CreateNalogRequest, idempotencyKey: String): Result<Nalog> =
        unwrap { api.create(idempotencyKey, request) }

    suspend fun searchCustomers(q: String): Result<List<NalogCustomer>> = unwrap { api.customers(q) }

    suspend fun technicians(): Result<List<TechnicianView>> = unwrap { api.technicians() }

    suspend fun history(id: Long): Result<List<NalogHistoryEntry>> = unwrap { api.history(id) }

    suspend fun changeStatus(id: Long, status: String): Result<Nalog> =
        unwrap { api.changeStatus(id, NalogStatusRequest(status)) }

    suspend fun update(id: Long, request: UpdateNalogRequest): Result<Nalog> = unwrap { api.update(id, request) }

    suspend fun preview(request: PreviewRequest): Result<NalogPreview> = unwrap { api.preview(request) }

    suspend fun searchParts(q: String?): Result<PartPage> = unwrap { api.parts(q = q) }

    suspend fun deleteNalog(id: Long): Result<Unit> = unwrap { api.delete(id) }

    suspend fun notes(id: Long): Result<List<NoteView>> = unwrap { api.notes(id) }

    suspend fun addNote(id: Long, body: String): Result<NoteView> =
        unwrap { api.addNote(id, CreateNoteRequest(body)) }

    suspend fun deleteNote(id: Long, noteId: Long): Result<Unit> = unwrap { api.deleteNote(id, noteId) }

    suspend fun photos(id: Long): Result<List<NalogPhotoView>> = unwrap { api.photos(id) }

    // Always sent as JPEG (see util/ImagePrep.kt).
    suspend fun uploadPhoto(id: Long, file: File, category: String): Result<NalogPhotoView> {
        val part = MultipartBody.Part.createFormData(
            "photo", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        val cat = category.toRequestBody("text/plain".toMediaTypeOrNull())
        return unwrap { api.uploadPhoto(id, part, cat) }
    }

    suspend fun deletePhoto(id: Long, photoId: Long): Result<Unit> = unwrap { api.deletePhoto(id, photoId) }

    // Raw bytes: the endpoint needs the Authorization header, so Coil/plain URLs would get a 401.
    suspend fun photoBytes(id: Long, photoId: Long): Result<ByteArray> {
        return try {
            val r = api.photoFile(id, photoId)
            val bytes = if (r.isSuccessful) r.body()?.bytes() else null
            if (bytes != null) Result.success(bytes)
            else Result.failure(Exception("Učitavanje fotografije nije uspelo (${r.code()})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
