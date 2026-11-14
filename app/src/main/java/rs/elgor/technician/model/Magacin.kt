package rs.elgor.technician.model

import com.squareup.moshi.JsonClass

// POST /api/parts/{id}/stock. delta is signed: positive adds to the stock, negative takes from it.
@JsonClass(generateAdapter = true)
data class StockAdjustRequest(val delta: Int, val note: String?)

// One row of a part's stock history (GET /api/parts/{id}/movements), newest first.
@JsonClass(generateAdapter = true)
data class StockMovement(
    val id: Long,
    val delta: Int,
    val reason: String,
    val note: String?,
    val nalogId: Long?,
    val actorName: String?,
    val createdAt: String
)
