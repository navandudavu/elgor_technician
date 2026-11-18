package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.elgor.technician.model.StockDoc
import rs.elgor.technician.model.StockDocSummary

// "Magacin" tab: the technician's own zaduzenja and razduzenja, read only. Nothing here changes data.
class StockDocViewModel(private val repository: NalogRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<StockDocSummary>>(emptyList())
    val items: StateFlow<List<StockDocSummary>> = _items.asStateFlow()

    private val _details = MutableStateFlow<Map<Long, StockDoc>>(emptyMap())
    val details: StateFlow<Map<Long, StockDoc>> = _details.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(false)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var page = 0

    fun load() {
        page = 0
        fetch(reset = true)
    }

    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) return
        fetch(reset = false)
    }

    private fun fetch(reset: Boolean) {
        if (_isLoading.value) return
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            repository.stockDocs(page).fold(
                onSuccess = { p ->
                    _items.value = if (reset) p.items else _items.value + p.items
                    _hasMore.value = p.page + 1 < p.totalPages
                    page = p.page + 1
                },
                onFailure = { _error.value = it.message ?: "Učitavanje nije uspelo" }
            )
            _isLoading.value = false
        }
    }

    /** Loads the lines of one document the first time it is opened. */
    fun loadDetail(id: Long) {
        if (_details.value.containsKey(id)) return
        viewModelScope.launch {
            repository.stockDoc(id).fold(
                onSuccess = { _details.value = _details.value + (id to it) },
                onFailure = { _error.value = it.message ?: "Učitavanje nije uspelo" }
            )
        }
    }
}

class StockDocViewModelFactory(private val repository: NalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = StockDocViewModel(repository) as T
}
