package rs.elgor.technician.model

import com.squareup.moshi.JsonClass

// Zaduzivanje / razduzivanje: the material a technician carries. Mirrors CarriedDtos.java on the server.

@JsonClass(generateAdapter = true)
data class CarriedItem(
    val id: Long,
    val technicianId: Long,
    val sku: String?,
    val name: String,
    val quantity: Int
)

@JsonClass(generateAdapter = true)
data class CarriedRequest(
    val sku: String? = null,
    val name: String,
    val quantity: Int,
    val technicianId: Long? = null   // admins only: whose list
)
