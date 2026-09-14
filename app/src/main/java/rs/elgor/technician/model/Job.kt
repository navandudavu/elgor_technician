package rs.elgor.technician.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Field names match ServiceHub's actual API response exactly (see
// backend/src/routes/jobs.js -> jobWithDetails()) - Moshi maps JSON keys
// to these properties automatically since they're already snake_case-free
// where the API itself returns camelCase-free snake_case JSON keys.

@JsonClass(generateAdapter = true)
data class Job(
    val id: Int,
    @Json(name = "customer_id") val customerId: Int,
    @Json(name = "appliance_id") val applianceId: Int?,
    @Json(name = "assigned_to") val assignedTo: Int?,
    @Json(name = "created_by") val createdBy: Int?,
    val title: String,
    val description: String?,
    val status: String, // pending | assigned | in_progress | finished | cancelled
    val priority: String, // low | normal | high | urgent
    @Json(name = "scheduled_at") val scheduledAt: String?,
    @Json(name = "finished_at") val finishedAt: String?,
    val address: String?,
    @Json(name = "hours_logged") val hoursLogged: Double?,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "customer_name") val customerName: String?,
    @Json(name = "customer_phone") val customerPhone: String?,
    @Json(name = "appliance_type") val applianceType: String?,
    @Json(name = "appliance_brand") val applianceBrand: String?,
    @Json(name = "appliance_model") val applianceModel: String?,
    @Json(name = "appliance_serial") val applianceSerial: String?,
    @Json(name = "assigned_to_name") val assignedToName: String?,
    val notes: List<JobNote> = emptyList(),
    val history: List<JobStatusHistoryEntry> = emptyList(),
    val photos: List<JobPhoto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class JobNote(
    val id: Int,
    @Json(name = "job_id") val jobId: Int,
    @Json(name = "author_id") val authorId: Int?,
    val note: String,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "author_name") val authorName: String?
)

@JsonClass(generateAdapter = true)
data class JobStatusHistoryEntry(
    val id: Int,
    @Json(name = "job_id") val jobId: Int,
    @Json(name = "old_status") val oldStatus: String?,
    @Json(name = "new_status") val newStatus: String,
    @Json(name = "changed_by") val changedBy: Int?,
    @Json(name = "changed_at") val changedAt: String,
    @Json(name = "changed_by_name") val changedByName: String?
)

@JsonClass(generateAdapter = true)
data class JobPhoto(
    val id: Int,
    @Json(name = "job_id") val jobId: Int,
    @Json(name = "uploaded_by") val uploadedBy: Int?,
    val filename: String,
    @Json(name = "original_name") val originalName: String?,
    val caption: String?,
    val category: String = "other",
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "uploaded_by_name") val uploadedByName: String?
)

@JsonClass(generateAdapter = true)
data class StatusUpdateRequest(val status: String)

@JsonClass(generateAdapter = true)
data class HoursUpdateRequest(@Json(name = "hours_logged") val hoursLogged: Double?)

@JsonClass(generateAdapter = true)
data class NoteCreateRequest(val note: String)

@JsonClass(generateAdapter = true)
data class ScheduleUpdateRequest(@Json(name = "scheduled_at") val scheduledAt: String?)
