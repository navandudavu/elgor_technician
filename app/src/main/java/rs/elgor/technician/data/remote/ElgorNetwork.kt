package rs.elgor.technician.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
        return chain.proceed(request)
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
}
