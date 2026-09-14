package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.elgor.technician.model.Job
import java.io.File

class JobsViewModel(private val repository: JobRepository) : ViewModel() {

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // No status/all params - defaults to "my assigned jobs only",
            // matching the technician view on the web app's job board.
            repository.getJobs().fold(
                onSuccess = { _jobs.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
}

class JobDetailViewModel(
    private val repository: JobRepository,
    private val jobId: Int
) : ViewModel() {

    private val _job = MutableStateFlow<Job?>(null)
    val job: StateFlow<Job?> = _job.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isBusy = MutableStateFlow(false) // true during any mutation (status/hours/note/photo)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    init {
        loadJob()
    }

    fun loadJob() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getJob(jobId).fold(
                onSuccess = { _job.value = it; _error.value = null },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun updateStatus(status: String) {
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.updateStatus(jobId, status).fold(
                onSuccess = { _job.value = it },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun updateHours(hours: Double?) {
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.updateHours(jobId, hours).fold(
                onSuccess = { _job.value = it },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun updateScheduledAt(scheduledAt: String?) {
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.updateSchedule(jobId, scheduledAt).fold(
                onSuccess = { _job.value = it },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun addNote(note: String) {
        if (note.isBlank()) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.addNote(jobId, note).fold(
                onSuccess = { _job.value = it },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun uploadPhoto(file: File, mimeType: String, category: String = "other") {
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.uploadPhoto(jobId, file, mimeType, category).fold(
                onSuccess = { _job.value = it },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun deletePhoto(photoId: Int) {
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.deletePhoto(jobId, photoId).fold(
                onSuccess = { _job.value = it },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }
}
