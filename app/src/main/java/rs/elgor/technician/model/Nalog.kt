package rs.elgor.technician.model

import com.squareup.moshi.JsonClass

// Mirrors the Java DTOs in nalog-service (NalogResponse / NalogSummary / PageResponse /
// StatusHistoryView). Enums are kept as plain Strings on purpose so a new server-side value
// can never crash parsing. Money is a String ("1250.00"): never parsed to Double on the phone.

@JsonClass(generateAdapter = true)
data class NalogPage(
    val items: List<NalogSummary>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class NalogSummary(
    val id: Long,
    val nalogNumber: String,
    val status: String,
    val customerName: String?,
    val customerPhone: String?,
    val applianceType: String?,
    val applianceBrand: String?,
    val total: String?,
    val paymentType: String?,
    val assignedToName: String?,
    val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class NalogCustomer(
    val id: Long,
    val fullName: String,
    val address: String?,
    val city: String?,
    val phone: String?,
    val email: String?,
    val locationNotes: String?
)

@JsonClass(generateAdapter = true)
data class NalogAppliance(
    val type: String?,
    val brand: String?,
    val model: String?,
    val serialNumber: String?
)

@JsonClass(generateAdapter = true)
data class NalogPartLine(
    val partId: Long,
    val sku: String?,
    val name: String,
    val unitPrice: String,
    val quantity: Int,
    val lineTotal: String
)

@JsonClass(generateAdapter = true)
data class Nalog(
    val id: Long,
    val nalogNumber: String,
    val status: String,
    val customer: NalogCustomer,
    val appliance: NalogAppliance,
    val diagnosis: String?,
    val laborHours: String,
    val laborRate: String,
    val laborTotal: String,
    val partLines: List<NalogPartLine> = emptyList(),
    val partsTotal: String,
    val subtotal: String,
    val vatRate: String,
    val vatAmount: String,
    val total: String,
    val paymentType: String?,
    val createdByName: String?,
    val assignedToId: Long?,
    val assignedToName: String?,
    val scheduledAt: String?,
    val completedAt: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val version: Long? = null
)

@JsonClass(generateAdapter = true)
data class NalogHistoryEntry(
    val fromStatus: String?,
    val toStatus: String,
    val changedByName: String?,
    val changedAt: String?
)

@JsonClass(generateAdapter = true)
data class NalogStatusRequest(val status: String)

// Spring's RFC 7807 error body: {"title":..., "detail":..., "errors": {"field": "msg"}}
@JsonClass(generateAdapter = true)
data class ProblemDetail(
    val title: String? = null,
    val detail: String? = null,
    val errors: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class NalogPhotoView(
    val id: Long,
    val nalogId: Long,
    val category: String,
    val caption: String?,
    val originalName: String?,
    val contentType: String?,
    val sizeBytes: Long,
    val uploadedById: Long?,
    val uploadedByName: String?,
    val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class NoteView(
    val id: Long,
    val nalogId: Long,
    val authorId: Long?,
    val authorName: String?,
    val body: String,
    val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class CreateNoteRequest(val body: String)

// --- Edit (PUT /api/nalozi/{id}) and the server-side price preview. No money is ever sent: only
// quantities and hours. Hours travel as a String ("2.50"), never through a Double.

@JsonClass(generateAdapter = true)
data class PartView(
    val id: Long,
    val sku: String?,
    val name: String,
    val unitPrice: String,
    val stockQty: Int,
    val active: Boolean
)

@JsonClass(generateAdapter = true)
data class PartPage(
    val items: List<PartView>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class PartLineInput(val partId: Long, val quantity: Int)

@JsonClass(generateAdapter = true)
data class ApplianceInput(
    val type: String,
    val brand: String,
    val model: String? = null,
    val serialNumber: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateNalogRequest(
    val customerId: Long,
    val appliance: ApplianceInput,
    val diagnosis: String,
    val partLines: List<PartLineInput>,
    val laborHours: String,
    val paymentType: String,
    val expectedVersion: Long
)

@JsonClass(generateAdapter = true)
data class PreviewRequest(
    val partLines: List<PartLineInput>,
    val laborHours: String,
    val nalogId: Long? = null
)

@JsonClass(generateAdapter = true)
data class NalogPreview(
    val laborHours: String,
    val laborRate: String,
    val laborTotal: String,
    val partLines: List<NalogPartLine> = emptyList(),
    val partsTotal: String,
    val subtotal: String,
    val vatRate: String,
    val vatAmount: String,
    val total: String
)
