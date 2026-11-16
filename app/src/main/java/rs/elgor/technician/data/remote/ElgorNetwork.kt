package rs.elgor.technician.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import rs.elgor.technician.data.SessionManager
import java.util.concurrent.TimeUnit

// Attaches "Authorization: Bearer <token>" to every request automatically,
// the same way the web app's api/client.js reads the token and sets the
// header on every fetch call. runBlocking here is intentional and safe:
// OkHttp interceptors are already running on a background thread (never
// the UI thread), so a short synchronous DataStore read doesn't block
// anything the user would notice.
private class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionManager.getToken() }
        val request = chain.request().newBuilder().apply {
            if (token != null) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        val response = chain.proceed(request)
        // Token rejected by the server (expired, password changed, account deactivated): drop it.
        // AuthViewModel watches the token and returns to the login screen. Only if the token we
        // sent is still the saved one, so a late 401 can't log out a fresh login.
        // With a refresh token still saved the session is not over: RefreshAuthenticator already tried, and a refresh
        // that failed only because the server could not be reached must not log anyone out.
        if (response.code == 401 && token != null && !request.url.encodedPath.endsWith("/auth/login")) {
            runBlocking {
                if (sessionManager.getToken() == token && sessionManager.getRefreshToken() == null) sessionManager.clearSession()
            }
        }
        return response
    }
}

// One refresh at a time for the whole app (both API clients share it): the refresh token is single use,
// so two parallel refreshes would make the second one fail and log the user out.
private val refreshLock = Any()
private val refreshHttp = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .writeTimeout(15, TimeUnit.SECONDS)
    .build()
private val JSON_TYPE = "application/json".toMediaType()

private sealed class RefreshOutcome {
    data class Ok(val token: String, val refreshToken: String) : RefreshOutcome()
    data object Rejected : RefreshOutcome()      // the server said no: session over
    data object Unavailable : RefreshOutcome()   // network or server trouble: keep the session, try again later
}

private fun callRefresh(baseUrl: String, refreshToken: String): RefreshOutcome {
    val request = Request.Builder()
        .url(baseUrl + "api/auth/refresh")
        .post(JSONObject().put("refreshToken", refreshToken).toString().toRequestBody(JSON_TYPE))
        .build()
    return try {
        refreshHttp.newCall(request).execute().use { r ->
            when {
                r.code == 401 -> RefreshOutcome.Rejected
                !r.isSuccessful -> RefreshOutcome.Unavailable
                else -> {
                    val json = JSONObject(r.body?.string() ?: "")
                    val t = json.optString("token")
                    val rt = json.optString("refreshToken")
                    if (t.isBlank() || rt.isBlank()) RefreshOutcome.Unavailable else RefreshOutcome.Ok(t, rt)
                }
            }
        }
    } catch (e: Exception) {
        RefreshOutcome.Unavailable
    }
}

// On a 401 the saved refresh token is traded for a new login token and the request is repeated once. Runs on
// OkHttp's background threads, same as AuthInterceptor, so the short blocking DataStore reads are fine.
private class RefreshAuthenticator(
    private val sessionManager: SessionManager,
    private val baseUrl: String
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (path.endsWith("/auth/login") || path.endsWith("/auth/refresh") || path.endsWith("/auth/logout")) return null
        if (response.priorResponse != null) return null                  // already retried once: give up
        val sent = response.request.header("Authorization")?.removePrefix("Bearer ")

        synchronized(refreshLock) {
            val current = runBlocking { sessionManager.getToken() }
            if (current != null && current != sent) {
                // another request refreshed the session while this one was waiting
                return response.request.newBuilder().header("Authorization", "Bearer $current").build()
            }
            val refresh = runBlocking { sessionManager.getRefreshToken() } ?: return null
            return when (val outcome = callRefresh(baseUrl, refresh)) {
                is RefreshOutcome.Ok -> {
                    runBlocking { sessionManager.saveTokens(outcome.token, outcome.refreshToken) }
                    response.request.newBuilder().header("Authorization", "Bearer ${outcome.token}").build()
                }
                RefreshOutcome.Rejected -> {
                    runBlocking { sessionManager.clearRefreshToken() }   // AuthInterceptor then ends the session
                    null
                }
                RefreshOutcome.Unavailable -> null
            }
        }
    }
}

object ElgorNetwork {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Rebuilt whenever the server URL changes (e.g. first-run setup, or an
    // admin changing which server this phone points at) - there's no
    // single fixed baseUrl to bake in at compile time, see SessionManager.
    fun create(baseUrl: String, sessionManager: SessionManager): ServiceHubApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(RefreshAuthenticator(sessionManager, baseUrl))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS) // photo uploads can be slower on mobile data
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(ServiceHubApi::class.java)
    }

    // Same client/auth as create(); only the target (Java nalog-service) differs.
    fun createNalogApi(serverUrl: String, sessionManager: SessionManager): NalogApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(RefreshAuthenticator(sessionManager, serverUrl))
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NalogApi::class.java)
    }

    // Logout: tell the server to kill the refresh token. Best effort, runs on the caller's IO thread.
    fun revokeRefreshToken(baseUrl: String, refreshToken: String) {
        try {
            val request = Request.Builder()
                .url(baseUrl + "api/auth/logout")
                .post(JSONObject().put("refreshToken", refreshToken).toString().toRequestBody(JSON_TYPE))
                .build()
            refreshHttp.newCall(request).execute().close()
        } catch (e: Exception) {
            // offline: the token simply expires
        }
    }
}
