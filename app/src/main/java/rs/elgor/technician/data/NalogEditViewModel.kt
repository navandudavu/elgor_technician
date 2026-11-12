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
import rs.elgor.technician.model.ApplianceInput
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.model.NalogPreview
import rs.elgor.technician.model.PartLineInput
import rs.elgor.technician.model.PartView
import rs.elgor.technician.model.PreviewRequest
import rs.elgor.technician.model.UpdateNalogRequest

data class EditLine(val partId: Long, val sku: String?, val name: String, val quantity: Int)

data class EditForm(
    val applianceType: String = "OTHER",
    val brand: String = "",
    val model: String = "",
    val serial: String = "",
    val diagnosis: String = "",
    val workDone: String = "",
    val hours: String = "",
    val payment: String = "CASH",
    val lines: List<EditLine> = emptyList()
)

// Edits an existing nalog. The customer is not editable on the phone (the web app does that).
// Money is never computed here: totals come from the server's preview endpoint, and the save request
// carries only quantities and hours. expectedVersion makes the server refuse (409) if someone else
// changed the nalog in the meantime, instead of silently overwriting their work.
class NalogEditViewModel(private val repository: NalogRepository, private val id: Long) : ViewModel() {

    private val hoursRegex = Regex("^\\d{1,3}([.,]\\d{1,2})?$")

    private val _original = MutableStateFlow<Nalog?>(null)
    val original: StateFlow<Nalog?> = _original.asStateFlow()

    private val _form = MutableStateFlow(EditForm())
    val form: StateFlow<EditForm> = _form.asStateFlow()

    private val _preview = MutableStateFlow<NalogPreview?>(null)
    val preview: StateFlow<NalogPreview?> = _preview.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _partResults = MutableStateFlow<List<PartView>>(emptyList())
    val partResults: StateFlow<List<PartView>> = _partResults.asStateFlow()

    private val _partsLoading = MutableStateFlow(false)
    val partsLoading: StateFlow<Boolean> = _partsLoading.asStateFlow()

    private var previewJob: Job? = null
    private var searchJob: Job? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.get(id).fold(
                onSuccess = { n ->
                    _original.value = n
                    _form.value = EditForm(
                        applianceType = n.appliance.type ?: "OTHER",
                        brand = n.appliance.brand ?: "",
                        model = n.appliance.model ?: "",
                        serial = n.appliance.serialNumber ?: "",
                        diagnosis = n.diagnosis ?: "",
                        workDone = n.workDone ?: "",
                        hours = n.laborHours,
                        payment = n.paymentType ?: "CASH",
                        lines = n.partLines.map { EditLine(it.partId, it.sku, it.name, it.quantity) }
                    )
                    _error.value = null
                    schedulePreview()
                },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun edit(transform: (EditForm) -> EditForm) {
        _form.value = transform(_form.value)
        schedulePreview()
    }

    private fun normalizedHours(raw: String): String? {
        val t = raw.trim()
        return if (hoursRegex.matches(t)) t.replace(',', '.') else null
    }

    fun hoursValid(raw: String): Boolean = normalizedHours(raw) != null

    private fun lineInputs(f: EditForm) = f.lines.map { PartLineInput(it.partId, it.quantity) }

    private fun schedulePreview() {
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            delay(400) // wait for typing to settle
            val f = _form.value
            val hours = normalizedHours(f.hours) ?: return@launch
            repository.preview(PreviewRequest(lineInputs(f), hours, id)).fold(
                onSuccess = { _preview.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun searchParts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _partsLoading.value = true
            repository.searchParts(query.trim().ifBlank { null }).fold(
                onSuccess = { _partResults.value = it.items },
                onFailure = { _error.value = it.message }
            )
            _partsLoading.value = false
        }
    }

    fun addPart(p: PartView) {
        edit { f ->
            val existing = f.lines.firstOrNull { it.partId == p.id }
            if (existing != null) {
                f.copy(lines = f.lines.map { if (it.partId == p.id) it.copy(quantity = minOf(it.quantity + 1, 999)) else it })
            } else {
                f.copy(lines = f.lines + EditLine(p.id, p.sku, p.name, 1))
            }
        }
    }

    fun save(onSaved: () -> Unit) {
        if (_isSaving.value) return
        val n = _original.value ?: return
        val f = _form.value
        val hours = normalizedHours(f.hours)
        val version = n.version
        val problem = when {
            f.brand.isBlank() -> "Unesite marku uređaja."
            f.diagnosis.isBlank() -> "Unesite dijagnozu / opis kvara."
            hours == null -> "Sati rada: broj do 999,99 (najviše 2 decimale)."
            version == null -> "Server nije poslao verziju naloga. Ažurirajte servis."
            else -> null
        }
        if (problem != null) {
            _error.value = problem
            return
        }
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            val request = UpdateNalogRequest(
                customerId = n.customer.id,
                appliance = ApplianceInput(
                    type = f.applianceType,
                    brand = f.brand.trim(),
                    model = f.model.trim().ifBlank { null },
                    serialNumber = f.serial.trim().ifBlank { null }
                ),
                diagnosis = f.diagnosis.trim(),
                workDone = f.workDone.trim().ifBlank { null },
                partLines = lineInputs(f),
                laborHours = hours!!,
                paymentType = f.payment,
                expectedVersion = version!!
            )
            repository.update(id, request).fold(
                onSuccess = { onSaved() },
                onFailure = { _error.value = it.message }
            )
            _isSaving.value = false
        }
    }
}

class NalogEditViewModelFactory(
    private val repository: NalogRepository,
    private val id: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NalogEditViewModel(repository, id) as T
}
