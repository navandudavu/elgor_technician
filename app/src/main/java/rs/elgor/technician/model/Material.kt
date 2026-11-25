package rs.elgor.technician.model

import com.squareup.moshi.JsonClass

// Mirrors HeldPart / MaterialLine / NalogEventView in StockDocDtos.java and NalogEventView.java.
// A part the technician holds, with the quantity he holds. Never a price. Quantities are Strings, never parsed on the phone.

@JsonClass(generateAdapter = true)
data class HeldPart(val partId: Long, val sku: String?, val name: String, val unit: String?, val quantity: String? = null)

@JsonClass(generateAdapter = true)
data class MaterialLine(
    val lineId: Long?,
    val docId: Long?,
    val docNo: Long,
    val partId: Long?,
    val sku: String?,
    val name: String,
    val unit: String?,
    val quantity: String,
    val byName: String?,
    val at: String?
)

@JsonClass(generateAdapter = true)
data class MaterialLineRequest(val partId: Long, val quantity: String)

@JsonClass(generateAdapter = true)
data class MaterialRequest(val lines: List<MaterialLineRequest>)

@JsonClass(generateAdapter = true)
data class NalogEvent(val at: String?, val by: String?, val kind: String, val detail: String?)
