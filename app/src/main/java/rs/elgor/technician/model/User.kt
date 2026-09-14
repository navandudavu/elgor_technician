package rs.elgor.technician.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResponse(val token: String, val user: User)

@JsonClass(generateAdapter = true)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String, // "admin" | "technician" - this app is built for technicians,
                       // but doesn't hard-block an admin from logging in too
    val phone: String? = null
)

// The API returns { "error": "message" } on failure responses - used to
// surface a real server message instead of a generic "something went wrong".
@JsonClass(generateAdapter = true)
data class ApiErrorResponse(val error: String?)

@JsonClass(generateAdapter = true)
data class HealthResponse(val ok: Boolean, val time: String? = null)
