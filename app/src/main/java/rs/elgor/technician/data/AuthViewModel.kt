package rs.elgor.technician.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import rs.elgor.technician.data.remote.ElgorNetwork
import rs.elgor.technician.model.User

sealed class AuthState {
    data object CheckingSession : AuthState() // brief startup state while we check for a saved token
    data object LoggedOut : AuthState()
    data class LoggedIn(val user: User) : AuthState()
}

class AuthViewModel(
    private val repository: JobRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.CheckingSession)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

    init {
        // The network layer clears the token on any 401 (see AuthInterceptor). Seeing it vanish
        // while logged in means the session died: back to the login screen.
        viewModelScope.launch {
            sessionManager.tokenFlow.collect { token ->
                if (token.isNullOrBlank() && _authState.value is AuthState.LoggedIn) {
                    _authState.value = AuthState.LoggedOut
                    _loginError.value = "Sesija je istekla. Prijavite se ponovo."
                }
            }
        }
    }

    // Called once when the app starts - if a token is already saved, verify
    // it's still valid (server might have restarted with a new JWT_SECRET,
    // or the token could have simply expired) by hitting /auth/me rather
    // than trusting the saved token blindly.
    fun checkExistingSession() {
        viewModelScope.launch {
            val token = sessionManager.getToken()
            // An expired login token with a refresh token still saved is a live session: the network layer renews it.
            if (token.isNullOrBlank() && sessionManager.getRefreshToken().isNullOrBlank()) {
                _authState.value = AuthState.LoggedOut
                return@launch
            }
            repository.getMe().fold(
                onSuccess = { user ->
                    _authState.value = AuthState.LoggedIn(user)
                    syncPushToken()
                },
                onFailure = { error ->
                    if (error.cause is java.io.IOException) {
                        // No connection: the session may be perfectly good, so keep it and let the person retry.
                        _loginError.value = error.message
                        _authState.value = AuthState.LoggedOut
                    } else {
                        // Saved token is no longer valid - clear it so the user
                        // isn't stuck bouncing between a broken "logged in" state.
                        sessionManager.clearSession()
                        _authState.value = AuthState.LoggedOut
                    }
                }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            _loginError.value = null
            repository.login(email, password).fold(
                onSuccess = { response ->
                    sessionManager.saveTokens(response.token, response.refreshToken)
                    _authState.value = AuthState.LoggedIn(response.user)
                    syncPushToken()
                },
                onFailure = { error ->
                    _loginError.value = error.message ?: "Prijava nije uspela"
                }
            )
            _isLoggingIn.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            val refresh = sessionManager.getRefreshToken()
            val url = sessionManager.getServerUrl()
            sessionManager.clearSession()
            _authState.value = AuthState.LoggedOut
            if (refresh != null && url != null) {
                withContext(Dispatchers.IO) { ElgorNetwork.revokeRefreshToken(url, refresh) }
            }
        }
    }

    fun resetServerUrl() {
        viewModelScope.launch {
            sessionManager.clearServerUrl()
            _authState.value = AuthState.CheckingSession // Trigger re-check in app
        }
    }

    private fun syncPushToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                repository.updatePushToken(token)
            } catch (e: Exception) {
                // Not critical if fails, will retry on next login/app start
                Log.e("AuthViewModel", "Failed to sync push token", e)
            }
        }
    }
}
