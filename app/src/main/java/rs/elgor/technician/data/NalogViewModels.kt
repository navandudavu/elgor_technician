package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.model.NalogHistoryEntry
import rs.elgor.technician.model.NalogPhotoView
import rs.elgor.technician.model.NoteView
import rs.elgor.technician.model.HeldPart
import rs.elgor.technician.model.MaterialLine
import rs.elgor.technician.model.NalogEvent
import java.io.File
import rs.elgor.technician.model.NalogSummary

class NalogListViewModel(private val repository: NalogRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<NalogSummary>>(emptyList())
    val items: StateFlow<List<NalogSummary>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasMore = MutableStateFlow(false)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private var nextPage = 0

    // Search (number, customer, address, device type...) and the archive (nalozi taken away from this technician, read only).
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    private val _archive = MutableStateFlow(false)
    val archive: StateFlow<Boolean> = _archive.asStateFlow()
    private var job: kotlinx.coroutines.Job? = null

    fun setQuery(q: String) { if (q != _query.value) { _query.value = q; load() } }
    fun setArchive(on: Boolean) { if (on != _archive.value) { _archive.value = on; load() } }

    fun load() {
        nextPage = 0
        fetch(reset = true)
    }

    fun loadMore() {
        if (_isLoading.value) return
        fetch(reset = false)
    }

    private fun fetch(reset: Boolean) {
        job?.cancel()          // a newer search replaces the one still running
        job = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.list(q = _query.value, archive = _archive.value, page = nextPage).fold(
                onSuccess = { p ->
                    _items.value = if (reset) p.items else _items.value + p.items
                    nextPage = p.page + 1
                    _hasMore.value = nextPage < p.totalPages
                },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
}

class NalogDetailViewModel(private val repository: NalogRepository, private val id: Long) : ViewModel() {

    private val _nalog = MutableStateFlow<Nalog?>(null)
    val nalog: StateFlow<Nalog?> = _nalog.asStateFlow()

    private val _history = MutableStateFlow<List<NalogHistoryEntry>>(emptyList())
    val history: StateFlow<List<NalogHistoryEntry>> = _history.asStateFlow()

    private val _photos = MutableStateFlow<List<NalogPhotoView>>(emptyList())
    val photos: StateFlow<List<NalogPhotoView>> = _photos.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteView>>(emptyList())
    val notes: StateFlow<List<NoteView>> = _notes.asStateFlow()

    private val _material = MutableStateFlow<List<MaterialLine>>(emptyList())
    val material: StateFlow<List<MaterialLine>> = _material.asStateFlow()

    private val _timeline = MutableStateFlow<List<NalogEvent>>(emptyList())
    val timeline: StateFlow<List<NalogEvent>> = _timeline.asStateFlow()

    private val _held = MutableStateFlow<List<HeldPart>>(emptyList())
    val held: StateFlow<List<HeldPart>> = _held.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.get(id).fold(
                onSuccess = { _nalog.value = it; _error.value = null },
                onFailure = { _error.value = it.message }
            )
            repository.history(id).onSuccess { _history.value = it }
            repository.photos(id).onSuccess { _photos.value = it }
            repository.notes(id).onSuccess { _notes.value = it }
            repository.material(id).onSuccess { _material.value = it }
            repository.timeline(id).onSuccess { _timeline.value = it }
            _isLoading.value = false
        }
    }

    // reason is only for CANCELLED (mandatory there, the server refuses a blank one).
    fun changeStatus(status: String, reason: String? = null) {
        if (_isBusy.value) return          // no double taps -> no double requests
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.changeStatus(id, status, reason).fold(
                onSuccess = { _nalog.value = it },
                onFailure = { _error.value = it.message }
            )
            repository.history(id).onSuccess { _history.value = it }
            repository.timeline(id).onSuccess { _timeline.value = it }
            _isBusy.value = false
        }
    }

    /** Search among the parts the signed-in technician holds. Nothing is listed until something is typed. */
    fun searchHeld(q: String) {
        if (q.isBlank()) { _held.value = emptyList(); return }
        viewModelScope.launch { repository.heldParts(q).onSuccess { _held.value = it.take(15) } }
    }

    // The server refuses more than he holds and parts he does not hold; its message is shown as is.
    fun addMaterial(partId: Long, quantity: String, onDone: () -> Unit) {
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.addMaterial(id, partId, quantity).fold(
                onSuccess = { _material.value = it; onDone() },
                onFailure = { _error.value = it.message }
            )
            repository.timeline(id).onSuccess { _timeline.value = it }
            _isBusy.value = false
        }
    }

    fun addNote(text: String, onDone: () -> Unit) {
        val body = text.trim()
        if (body.isEmpty() || _isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.addNote(id, body).fold(
                onSuccess = { _notes.value = _notes.value + it; onDone() },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun deleteNote(noteId: Long) {
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.deleteNote(id, noteId).fold(
                onSuccess = { _notes.value = _notes.value.filter { it.id != noteId } },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    // Admin only. Server restores stock and refuses COMPLETED.
    fun deleteNalog(onDeleted: () -> Unit) {
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.deleteNalog(id).fold(
                onSuccess = { onDeleted() },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }

    fun uploadPhoto(file: File, category: String) {
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.uploadPhoto(id, file, category).fold(
                onSuccess = { _photos.value = _photos.value + it },
                onFailure = { _error.value = it.message }
            )
            runCatching { file.delete() }
            _isBusy.value = false
        }
    }

    fun deletePhoto(photoId: Long) {
        if (_isBusy.value) return
        viewModelScope.launch {
            _isBusy.value = true
            _error.value = null
            repository.deletePhoto(id, photoId).fold(
                onSuccess = { _photos.value = _photos.value.filter { it.id != photoId } },
                onFailure = { _error.value = it.message }
            )
            _isBusy.value = false
        }
    }
}

class NalogListViewModelFactory(private val repository: NalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NalogListViewModel(repository) as T
}

class NalogDetailViewModelFactory(
    private val repository: NalogRepository,
    private val id: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NalogDetailViewModel(repository, id) as T
}
