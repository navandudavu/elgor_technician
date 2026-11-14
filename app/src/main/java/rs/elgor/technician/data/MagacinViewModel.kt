package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.elgor.technician.model.PartView
import rs.elgor.technician.model.StockAdjustRequest
import rs.elgor.technician.model.StockMovement

// Magacin: everybody signed in sees all parts and may correct the stock (the server writes who did it into the
// history). Creating or editing parts is done on the web by an admin or manager.
class MagacinViewModel(private val repository: NalogRepository) : ViewModel() {

    private val pageSize = 50
    private val deltaRegex = Regex("^\\d{1,6}$")

    private val _parts = MutableStateFlow<List<PartView>>(emptyList())
    val parts: StateFlow<List<PartView>> = _parts.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _showInactive = MutableStateFlow(false)
    val showInactive: StateFlow<Boolean> = _showInactive.asStateFlow()

    private val _hasMore = MutableStateFlow(false)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // null = history dialog closed
    private val _history = MutableStateFlow<List<StockMovement>?>(null)
    val history: StateFlow<List<StockMovement>?> = _history.asStateFlow()

    private var page = 0
    private var job: Job? = null

    init { reload(false) }

    fun setQuery(q: String) {
        _query.value = q
        reload(true)
    }

    fun setShowInactive(v: Boolean) {
        _showInactive.value = v
        reload(false)
    }

    fun reload(debounce: Boolean) {
        job?.cancel()
        job = viewModelScope.launch {
            if (debounce) delay(300)
            _isLoading.value = true
            repository.searchParts(_query.value.trim().ifBlank { null }, !_showInactive.value, 0, pageSize).fold(
                onSuccess = {
                    page = 0
                    _parts.value = it.items
                    _hasMore.value = it.page + 1 < it.totalPages
                    _error.value = null
                },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) return
        job = viewModelScope.launch {
            _isLoading.value = true
            repository.searchParts(_query.value.trim().ifBlank { null }, !_showInactive.value, page + 1, pageSize).fold(
                onSuccess = {
                    page = it.page
                    _parts.value = _parts.value + it.items
                    _hasMore.value = it.page + 1 < it.totalPages
                },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    /** quantity is a positive whole number; add = true puts it into the stock, false takes it out. */
    fun adjust(part: PartView, quantity: String, add: Boolean, note: String, onDone: () -> Unit) {
        if (_isSaving.value) return
        val q = quantity.trim()
        if (!deltaRegex.matches(q) || q.toInt() == 0) {
            _error.value = "Količina mora biti ceo broj veći od nule."
            return
        }
        val delta = if (add) q.toInt() else -q.toInt()
        viewModelScope.launch {
            _isSaving.value = true
            repository.adjustStock(part.id, StockAdjustRequest(delta, note.trim().ifBlank { null })).fold(
                onSuccess = { updated ->
                    _parts.value = _parts.value.map { if (it.id == updated.id) updated else it }
                    _error.value = null
                    onDone()
                },
                onFailure = { _error.value = it.message }
            )
            _isSaving.value = false
        }
    }

    fun openHistory(part: PartView) {
        viewModelScope.launch {
            repository.movements(part.id).fold(
                onSuccess = { _history.value = it; _error.value = null },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun closeHistory() { _history.value = null }

    fun clearError() { _error.value = null }
}

class MagacinViewModelFactory(private val repository: NalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = MagacinViewModel(repository) as T
}
