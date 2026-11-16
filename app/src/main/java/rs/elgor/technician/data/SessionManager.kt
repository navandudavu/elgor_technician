package rs.elgor.technician.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "elgor_technician_session")

// Persists the JWT and the server URL across app restarts, same purpose as
// localStorage in the ServiceHub web app. The server URL is configurable
// (not hardcoded) because during development/testing this points at
// whatever machine is running the backend on the local network - it only
// becomes a fixed value once ServiceHub is actually deployed somewhere.
class SessionManager(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val SERVER_URL = stringPreferencesKey("server_url")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val serverUrlFlow: Flow<String?> = context.dataStore.data.map { it[Keys.SERVER_URL] }

    suspend fun getToken(): String? = context.dataStore.data.first()[Keys.TOKEN]
    suspend fun getRefreshToken(): String? = context.dataStore.data.first()[Keys.REFRESH_TOKEN]
    suspend fun getServerUrl(): String? = context.dataStore.data.first()[Keys.SERVER_URL]

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[Keys.TOKEN] = token }
    }

    // Login token and refresh token together; the refresh token (30 days, single use) keeps the phone logged in.
    suspend fun saveTokens(token: String, refreshToken: String?) {
        context.dataStore.edit {
            it[Keys.TOKEN] = token
            if (refreshToken != null) it[Keys.REFRESH_TOKEN] = refreshToken else it.remove(Keys.REFRESH_TOKEN)
        }
    }

    // The server refused the refresh token: it is useless from now on.
    suspend fun clearRefreshToken() {
        context.dataStore.edit { it.remove(Keys.REFRESH_TOKEN) }
    }

    suspend fun saveServerUrl(url: String) {
        // Always ends with a single trailing slash - Retrofit's baseUrl
        // requires this, and being permissive about what the user types
        // (with or without a trailing slash) avoids a confusing setup error.
        val normalized = if (url.endsWith("/")) url else "$url/"
        context.dataStore.edit { it[Keys.SERVER_URL] = normalized }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(Keys.TOKEN)
            it.remove(Keys.REFRESH_TOKEN)
            // Deliberately keep SERVER_URL on logout - it's a deployment
            // setting, not a credential, so re-logging in shouldn't require
            // re-entering the server address every time.
        }
    }

    suspend fun clearServerUrl() {
        context.dataStore.edit {
            it.remove(Keys.SERVER_URL)
        }
    }
}
