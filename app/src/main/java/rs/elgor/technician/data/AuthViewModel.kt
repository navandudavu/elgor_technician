package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    // Called once when the app starts - if a token is already saved, verify
    // it's still valid (server might have restarted with a new JWT_SECRET,
    // or the token could have simply expired) by hitting /auth/me rather
    // than trusting the saved token blindly.
    fun checkExistingSession() {
        viewModelScope.launch {
            val token = sessionManager.getToken()
            if (token.isNullOrBlank()) {
                _authState.value = AuthState.LoggedOut
                return@launch
            }
            repository.getMe().fold(
                onSuccess = { user ->
                    _authState.value = AuthState.LoggedIn(user)
                    syncPushToken()
                },
                onFailure = {
                    // Saved token is no longer valid - clear it so the user
                    // isn't stuck bouncing between a broken "logged in" state.
                    sessionManager.clearSession()
                    _authState.value = AuthState.LoggedOut
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
                    sessionManager.saveToken(response.token)
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
            sessionManager.clearSession()
            _authState.value = AuthState.LoggedOut
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
            }
        }
    }
}
