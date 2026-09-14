package rs.elgor.technician.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Appliance(
    val id: Int,
    @Json(name = "customer_id") val customerId: Int,
    val type: String,
    val brand: String?,
    val model: String?,
    @Json(name = "serial_number") val serialNumber: String?,
    @Json(name = "purchase_date") val purchaseDate: String?,
    val notes: String?,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class AssetSearchResponse(
    val appliance: Appliance,
    val jobs: List<Job>
)
