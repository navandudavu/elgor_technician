package rs.elgor.technician.data.remote

import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import rs.elgor.technician.model.CreateNalogRequest
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.model.NalogCustomer
import rs.elgor.technician.model.TechnicianView
import rs.elgor.technician.model.NalogHistoryEntry
import rs.elgor.technician.model.NalogPage
import rs.elgor.technician.model.NalogPhotoView
import rs.elgor.technician.model.NalogStatusRequest
import rs.elgor.technician.model.HeldPart
import rs.elgor.technician.model.MaterialLine
import rs.elgor.technician.model.MaterialRequest
import rs.elgor.technician.model.NalogEvent
import rs.elgor.technician.model.NalogPreview
import rs.elgor.technician.model.PreviewRequest
import rs.elgor.technician.model.UpdateNalogRequest
import retrofit2.http.PUT
import rs.elgor.technician.model.CreateNoteRequest
import rs.elgor.technician.model.NoteView

// The Java nalog-service (/api/nalozi). Same JWT as the Node backend. Technicians only ever
// see nalozi assigned to or created by them: the server enforces that, not this app.
interface NalogApi {

    @GET("api/nalozi")
    suspend fun list(
        @Query("status") status: String? = null,
        @Query("q") q: String? = null,
        @Query("archive") archive: Boolean? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30
    ): Response<NalogPage>

    // One Idempotency-Key per form: a retry after a timeout can never create a second nalog.
    // A technician always becomes the assignee; status and assignee are honoured for admins only.
    @POST("api/nalozi")
    suspend fun create(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body body: CreateNalogRequest
    ): Response<Nalog>

    // Customer autocomplete (2+ characters), same endpoint the web form uses.
    @GET("api/nalozi/customers")
    suspend fun customers(@Query("q") q: String): Response<List<NalogCustomer>>

    // Admin only (server enforces it): who a new nalog can be assigned to.
    @GET("api/nalozi/technicians")
    suspend fun technicians(): Response<List<TechnicianView>>

    @GET("api/nalozi/{id}")
    suspend fun get(@Path("id") id: Long): Response<Nalog>

    // Admin: any nalog. Technician: only their own. Never COMPLETED / CANCELLED (server enforces all of it).
    @PUT("api/nalozi/{id}")
    suspend fun update(@Path("id") id: Long, @Body body: UpdateNalogRequest): Response<Nalog>

    // "What would this cost?" - the server does the money maths, the phone only displays it.
    @POST("api/nalozi/preview")
    suspend fun preview(@Body body: PreviewRequest): Response<NalogPreview>

    // Device types for the dropdown: built-in codes, then the names the admin added on the web.
    @GET("api/appliance-types")
    suspend fun applianceTypes(): Response<List<String>>

    // Admin only (server enforces it); not allowed for COMPLETED.
    @DELETE("api/nalozi/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>

    // Magacin tab: the parts the signed-in technician holds (name, code, unit - no quantity). Search only.
    @GET("api/stock-docs/held")
    suspend fun heldParts(@Query("q") q: String? = null): Response<List<HeldPart>>

    // Material of a nalog = automatic razduzenje. A technician can only add parts he holds.
    @GET("api/nalozi/{id}/material")
    suspend fun material(@Path("id") id: Long): Response<List<MaterialLine>>

    @POST("api/nalozi/{id}/material")
    suspend fun addMaterial(@Path("id") id: Long, @Body body: MaterialRequest): Response<List<MaterialLine>>

    // Complete history (status, edits, notes, photos, material, assignment...), also for a nalog taken away from the technician.
    @GET("api/nalozi/{id}/timeline")
    suspend fun timeline(@Path("id") id: Long): Response<List<NalogEvent>>

    @GET("api/nalozi/{id}/notes")
    suspend fun notes(@Path("id") id: Long): Response<List<NoteView>>

    @POST("api/nalozi/{id}/notes")
    suspend fun addNote(@Path("id") id: Long, @Body body: CreateNoteRequest): Response<NoteView>

    // Admin only.
    @DELETE("api/nalozi/{id}/notes/{noteId}")
    suspend fun deleteNote(@Path("id") id: Long, @Path("noteId") noteId: Long): Response<Unit>

    @GET("api/nalozi/{id}/history")
    suspend fun history(@Path("id") id: Long): Response<List<NalogHistoryEntry>>

    @PATCH("api/nalozi/{id}/status")
    suspend fun changeStatus(@Path("id") id: Long, @Body body: NalogStatusRequest): Response<Nalog>

    @GET("api/nalozi/{id}/photos")
    suspend fun photos(@Path("id") id: Long): Response<List<NalogPhotoView>>

    @Multipart
    @POST("api/nalozi/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: Long,
        @Part photo: MultipartBody.Part,
        @Part("category") category: RequestBody
    ): Response<NalogPhotoView>

    @GET("api/nalozi/{id}/photos/{photoId}/file")
    suspend fun photoFile(@Path("id") id: Long, @Path("photoId") photoId: Long): Response<ResponseBody>

    @DELETE("api/nalozi/{id}/photos/{photoId}")
    suspend fun deletePhoto(@Path("id") id: Long, @Path("photoId") photoId: Long): Response<Unit>
}
