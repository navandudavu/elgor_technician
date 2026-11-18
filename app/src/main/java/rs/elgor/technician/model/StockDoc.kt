package rs.elgor.technician.model

import com.squareup.moshi.JsonClass

// Zaduzenje (ZAD) / Razduzenje (RAZ) documents. Mirrors StockDocDtos.java on the server. The phone only reads them:
// the server scopes the list to the signed-in technician and only admin-level users create or change documents.
// Quantities are Strings ("2.000"), never parsed to numbers on the phone.

@JsonClass(generateAdapter = true)
data class StockDocPage(
    val items: List<StockDocSummary>,
    val page: Int,
    val size: Int,
    val totalItems: Long,
    val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class StockDocSummary(
    val id: Long,
    val type: String,
    val docNo: Long,
    val technicianId: Long?,
    val technicianName: String?,
    val docAt: String?,
    val createdByName: String?,
    val lineCount: Int
)

@JsonClass(generateAdapter = true)
data class StockDocLine(
    val id: Long?,
    val partId: Long?,
    val sku: String?,
    val name: String,
    val unit: String?,
    val quantity: String
)

@JsonClass(generateAdapter = true)
data class StockDoc(
    val id: Long,
    val type: String,
    val docNo: Long,
    val technicianId: Long?,
    val technicianName: String?,
    val docAt: String?,
    val createdByName: String?,
    val updatedByName: String?,
    val lines: List<StockDocLine> = emptyList()
)
