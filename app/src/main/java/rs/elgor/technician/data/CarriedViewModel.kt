package rs.elgor.technician.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rs.elgor.technician.model.CarriedItem
import rs.elgor.technician.model.CarriedRequest
import rs.elgor.technician.model.TechnicianView

// Zaduzivanje / razduzivanje. A technician manages his own list (at most 50 items, enforced by the server);
// an admin or manager picks whose list to manage. Not connected to the warehouse.
class CarriedViewModel(
    private val repository: NalogRepository,
    private val isAdmin: Boolean,
    private val myId: Long
) : ViewModel() {

    private val qtyRegex = Regex("^\\d{1,7}$")

    private val _items = MutableStateFlow<List<CarriedItem>>(emptyList())
    val items: StateFlow<List<CarriedItem>> = _items.asStateFlow()

    private val _technicians = MutableStateFlow<List<TechnicianView>>(emptyList())
    val technicians: StateFlow<List<TechnicianView>> = _technicians.asStateFlow()

    private val _technicianId = MutableStateFlow<Long?>(if (isAdmin) null else myId)
    val technicianId: StateFlow<Long?> = _technicianId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        if (isAdmin) {
            viewModelScope.launch {
                repository.technicians().fold(
                    onSuccess = { list ->
                        _technicians.value = list
                        _technicianId.value = list.firstOrNull()?.id
                        load()
                    },
                    onFailure = { _error.value = it.message }
                )
            }
        } else {
            load()
        }
    }

    fun selectTechnician(id: Long) {
        _technicianId.value = id
        load()
    }

    fun load() {
        val tech = _technicianId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.carried(if (isAdmin) tech else null).fold(
                onSuccess = { _items.value = it; _error.value = null },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    /** editingId == null adds a new line, otherwise changes that one. onDone runs only after the server accepted it. */
    fun save(editingId: Long?, sku: String, name: String, qty: String, onDone: () -> Unit) {
        if (_isSaving.value) return
        val tech = _technicianId.value
        val q = qty.trim()
        val problem = when {
            tech == null -> "Izaberite servisera."
            name.isBlank() -> "Unesite naziv."
            !qtyRegex.matches(q) || q.toLong() > 1_000_000 -> "Količina mora biti ceo broj."
            else -> null
        }
        if (problem != null) {
            _error.value = problem
            return
        }
        val request = CarriedRequest(
            sku = sku.trim().ifBlank { null },
            name = name.trim(),
            quantity = q.toInt(),
            technicianId = if (isAdmin) tech else null
        )
        viewModelScope.launch {
            _isSaving.value = true
            val result = if (editingId == null) repository.addCarried(request).map { Unit }
            else repository.updateCarried(editingId, request).map { Unit }
            result.fold(
                onSuccess = { _error.value = null; onDone(); load() },
                onFailure = { _error.value = it.message }
            )
            _isSaving.value = false
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.deleteCarried(id).fold(
                onSuccess = { _error.value = null; load() },
                onFailure = { _error.value = it.message }
            )
            _isSaving.value = false
        }
    }
}

class CarriedViewModelFactory(
    private val repository: NalogRepository,
    private val isAdmin: Boolean,
    private val myId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CarriedViewModel(repository, isAdmin, myId) as T
}
