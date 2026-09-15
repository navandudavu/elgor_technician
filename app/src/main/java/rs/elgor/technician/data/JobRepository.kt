package rs.elgor.technician.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import kotlinx.coroutines.flow.firstOrNull
import rs.elgor.technician.data.local.JobDao
import rs.elgor.technician.data.local.toDomain
import rs.elgor.technician.data.local.toEntity
import rs.elgor.technician.data.remote.ServiceHubApi
import rs.elgor.technician.model.HealthResponse
import rs.elgor.technician.model.HoursUpdateRequest
import rs.elgor.technician.model.Job
import rs.elgor.technician.model.LoginRequest
import rs.elgor.technician.model.LoginResponse
import rs.elgor.technician.model.NoteCreateRequest
import rs.elgor.technician.model.ScheduleUpdateRequest
import rs.elgor.technician.model.StatusUpdateRequest
import rs.elgor.technician.model.User
import java.io.File

// Wraps every raw Retrofit call in Result, extracting the server's actual
// {"error": "..."} message on failure instead of a generic HTTP status -
// mirrors how the web app's api/client.js throws Error(data.error) on a
// non-ok response.
class JobRepository(
    private val api: ServiceHubApi,
    private val dao: JobDao
) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private suspend fun <T> unwrap(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Result.success(body)
                else Result.failure(Exception("Prazan odgovor sa servera"))
            } else {
                val errorBody = response.errorBody()?.string()
                val message = try {
                    moshi.adapter(rs.elgor.technician.model.ApiErrorResponse::class.java)
                        .fromJson(errorBody ?: "")?.error
                } catch (e: Exception) {
                    null
                }
                Result.failure(Exception(message ?: "Zahtev nije uspeo (${response.code()})"))
            }
        } catch (e: java.io.IOException) {
            // Network-level failure (no connection, server unreachable, timeout) -
            // distinct from a server error response, worth a clearer message
            // since "check your connection" is actionable and "Request failed (0)"
            // is not.
            Result.failure(Exception("Nije moguće pristupiti serveru. Proverite vezu ili adresu servera.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> =
        unwrap { api.login(LoginRequest(email, password)) }

    suspend fun checkHealth(): Result<HealthResponse> = unwrap { api.checkHealth() }

    suspend fun getMe(): Result<User> = unwrap { api.getMe() }

    suspend fun updatePushToken(token: String): Result<Unit> =
        unwrap { api.updatePushToken(mapOf("token" to token, "platform" to "android")) }

    suspend fun getJobs(statusFilter: String? = null, showAll: Boolean = false): Result<List<Job>> {
        val networkResult = unwrap { api.getJobs(status = statusFilter, all = if (showAll) 1 else null) }
        
        return if (networkResult.isSuccess) {
            val jobs = networkResult.getOrNull() ?: emptyList()
            // Only cache if it's the default "my assigned jobs" list to avoid 
            // overwriting the local "source of truth" with filtered results.
            if (statusFilter == null && !showAll) {
                dao.insertJobs(jobs.map { it.toEntity() })
            }
            networkResult
        } else {
            // Fallback to local data
            val localJobs = dao.getAllJobs().firstOrNull() ?: emptyList()
            if (localJobs.isNotEmpty()) {
                Result.success(localJobs.map { it.toDomain() })
            } else {
                networkResult
            }
        }
    }

    suspend fun getJob(id: Int): Result<Job> {
        val networkResult = unwrap { api.getJob(id) }
        
        return if (networkResult.isSuccess) {
            val job = networkResult.getOrNull()
            if (job != null) {
                dao.insertJob(job.toEntity())
                dao.deleteNotesForJob(id)
                dao.insertNotes(job.notes.map { it.toEntity() })
                dao.deletePhotosForJob(id)
                dao.insertPhotos(job.photos.map { it.toEntity() })
            }
            networkResult
        } else {
            // Fallback to local
            val entity = dao.getJobById(id).firstOrNull()
            if (entity != null) {
                val notes = dao.getNotesForJob(id).firstOrNull() ?: emptyList()
                val photos = dao.getPhotosForJob(id).firstOrNull() ?: emptyList()
                Result.success(entity.toDomain(notes, photos))
            } else {
                networkResult
            }
        }
    }

    suspend fun updateStatus(id: Int, status: String): Result<Job> =
        unwrap { api.updateStatus(id, StatusUpdateRequest(status)) }.also { result ->
            result.getOrNull()?.let { dao.insertJob(it.toEntity()) }
        }

    suspend fun updateHours(id: Int, hours: Double?): Result<Job> =
        unwrap { api.updateHours(id, HoursUpdateRequest(hours)) }.also { result ->
            result.getOrNull()?.let { dao.insertJob(it.toEntity()) }
        }

    suspend fun updateSchedule(id: Int, scheduledAt: String?): Result<Job> =
        unwrap { api.updateSchedule(id, ScheduleUpdateRequest(scheduledAt)) }.also { result ->
            result.getOrNull()?.let { dao.insertJob(it.toEntity()) }
        }

    suspend fun addNote(id: Int, note: String): Result<Job> =
        unwrap { api.addNote(id, NoteCreateRequest(note)) }.also { result ->
            result.getOrNull()?.let { job ->
                dao.insertNotes(job.notes.map { it.toEntity() })
            }
        }

    suspend fun uploadPhoto(id: Int, file: File, mimeType: String, category: String = "other"): Result<Job> {
        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photo", file.name, requestBody)
        val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
        return unwrap { api.uploadPhoto(id, part, categoryBody) }.also { result ->
            result.getOrNull()?.let { job ->
                dao.insertPhotos(job.photos.map { it.toEntity() })
            }
        }
    }

    suspend fun deletePhoto(jobId: Int, photoId: Int): Result<Job> =
        unwrap { api.deletePhoto(jobId, photoId) }.also { result ->
            result.getOrNull()?.let { job ->
                dao.deletePhotosForJob(jobId)
                dao.insertPhotos(job.photos.map { jobPhoto -> jobPhoto.toEntity() })
            }
        }

    // Returns the raw photo bytes for display - the caller (JobPhoto
    // composable) turns this into a bitmap. Auth-gated on the server side,
    // same reason the web app can't use a plain <img src> for photos: the
    // request needs the Authorization header, which only this client can attach.
    suspend fun getPhotoBytes(filename: String): Result<ByteArray> {
        return try {
            val response = api.getPhoto(filename)
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) Result.success(bytes)
                else Result.failure(Exception("Prazan odgovor za fotografiju"))
            } else {
                Result.failure(Exception("Učitavanje fotografije nije uspelo (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
