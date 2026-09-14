package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Plain factories rather than a DI framework (Hilt/Koin) - this app is
// small enough that a DI framework would be more ceremony than the problem
// needs. If the app grows a lot more screens/dependencies later, revisit this.

class AuthViewModelFactory(
    private val repository: JobRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class JobsViewModelFactory(private val repository: JobRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobsViewModel::class.java)) {
            return JobsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}

class JobDetailViewModelFactory(
    private val repository: JobRepository,
    private val jobId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobDetailViewModel::class.java)) {
            return JobDetailViewModel(repository, jobId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
