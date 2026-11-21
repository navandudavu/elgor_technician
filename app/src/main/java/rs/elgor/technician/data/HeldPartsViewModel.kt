package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.elgor.technician.model.HeldPart

// "Magacin" tab: the parts the technician holds (code, name, unit). No quantity, no documents, nothing to change.
class HeldPartsViewModel(private val repository: NalogRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<HeldPart>>(emptyList())
    val items: StateFlow<List<HeldPart>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(q: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.heldParts(q).fold(
                onSuccess = { _items.value = it },
                onFailure = { _error.value = it.message ?: "Učitavanje nije uspelo" }
            )
            _isLoading.value = false
        }
    }
}

class HeldPartsViewModelFactory(private val repository: NalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HeldPartsViewModel(repository) as T
}
