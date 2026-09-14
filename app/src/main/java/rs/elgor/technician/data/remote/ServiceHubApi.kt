package rs.elgor.technician.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import rs.elgor.technician.model.HealthResponse
import rs.elgor.technician.model.HoursUpdateRequest
import rs.elgor.technician.model.Job
import rs.elgor.technician.model.LoginRequest
import rs.elgor.technician.model.LoginResponse
import rs.elgor.technician.model.NoteCreateRequest
import rs.elgor.technician.model.ScheduleUpdateRequest
import rs.elgor.technician.model.StatusUpdateRequest
import rs.elgor.technician.model.User

// Mirrors backend/src/routes/{auth,jobs}.js exactly. Only the endpoints a
// technician actually uses are included here - admin-only routes (assign,
// schedule, CSV export, team management) are deliberately left out, since
// this app is scoped to technicians only (see the ServiceHub web app for
// the admin-facing equivalents). A previous edit added an updateSchedule()
// call here, but PATCH /jobs/:id/schedule is admin-only on the backend
// (requireRole('admin')) - a technician account would just get a 403, so
// it's been removed rather than left half-wired to an action this app
// was never meant to expose.
interface ServiceHubApi {

    @GET("api/health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<User>

    @POST("api/auth/token")
    suspend fun updatePushToken(@Body body: Map<String, String>): Response<Unit>

    // all=1 lets a technician see the full board if they ever need to,
    // but the default (no param) is "only my assigned jobs" - matches the
    // web app's default for technician accounts.
    @GET("api/jobs")
    suspend fun getJobs(
        @Query("status") status: String? = null,
        @Query("all") all: Int? = null
    ): Response<List<Job>>

    @GET("api/jobs/{id}")
    suspend fun getJob(@Path("id") id: Int): Response<Job>

    @PATCH("api/jobs/{id}/status")
    suspend fun updateStatus(@Path("id") id: Int, @Body body: StatusUpdateRequest): Response<Job>

    @PATCH("api/jobs/{id}/hours")
    suspend fun updateHours(@Path("id") id: Int, @Body body: HoursUpdateRequest): Response<Job>

    @PATCH("api/jobs/{id}/schedule")
    suspend fun updateSchedule(@Path("id") id: Int, @Body body: ScheduleUpdateRequest): Response<Job>

    @POST("api/jobs/{id}/notes")
    suspend fun addNote(@Path("id") id: Int, @Body body: NoteCreateRequest): Response<Job>

    // Matches the backend exactly: POST /jobs/:id/photos takes a "photo"
    // multipart field and an optional "caption" - there's no "category"
    // field on the server, so a part for it doesn't belong here (a
    // previous edit added one, which is also why this stopped matching
    // JobRepository's two-argument call and failed to compile).
    // Matches the backend: POST /jobs/:id/photos accepts a "photo" multipart
    // field, an optional "caption", and an optional "category" (defaults to
    // "other" server-side if omitted - see backend/src/routes/jobs.js and
    // the category migration in backend/src/db/index.js).
    @Multipart
    @POST("api/jobs/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: Int,
        @Part photo: MultipartBody.Part,
        @Part("category") category: RequestBody
    ): Response<Job>

    @GET("api/jobs/photos/{filename}")
    suspend fun getPhoto(@Path("filename") filename: String): Response<ResponseBody>

    @DELETE("api/jobs/{jobId}/photos/{photoId}")
    suspend fun deletePhoto(@Path("jobId") jobId: Int, @Path("photoId") photoId: Int): Response<Job>
}