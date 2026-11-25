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
import rs.elgor.technician.ui.components.DEFAULT_APPLIANCE_TYPES
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.model.NalogPreview
import rs.elgor.technician.model.PreviewRequest
import rs.elgor.technician.model.UpdateNalogRequest

data class EditForm(
    val applianceType: String = "OTHER",
    val brand: String = "",
    val model: String = "",
    val serial: String = "",
    val diagnosis: String = "",
    val hours: String = "",
    val price: String = "",
    val payment: String = "CASH"
)

// Edits an existing nalog. The customer is not editable on the phone (the web app does that).
// Money is never computed here: totals come from the server's preview endpoint, and the save request
// carries only quantities and hours. expectedVersion makes the server refuse (409) if someone else
// changed the nalog in the meantime, instead of silently overwriting their work.
class NalogEditViewModel(private val repository: NalogRepository, private val id: Long) : ViewModel() {

    private val hoursRegex = Regex("^\\d{1,3}([.,]\\d{1,2})?$")
    private val priceRegex = Regex("^\\d{1,8}([.,]\\d{1,2})?$")

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

    private val _applianceTypes = MutableStateFlow(DEFAULT_APPLIANCE_TYPES)
    val applianceTypes: StateFlow<List<String>> = _applianceTypes.asStateFlow()

    // The form as last loaded or saved, so the screen knows whether there is anything unsaved.
    private var baseline = EditForm()

    private val _dirty = MutableStateFlow(false)
    val dirty: StateFlow<Boolean> = _dirty.asStateFlow()

    private var previewJob: Job? = null

    init {
        load()
        viewModelScope.launch { repository.applianceTypes().onSuccess { if (it.isNotEmpty()) _applianceTypes.value = it } }
    }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.get(id).fold(
                onSuccess = { n ->
                    _original.value = n
                    val loaded = EditForm(
                        applianceType = n.appliance.type ?: "OTHER",
                        brand = n.appliance.brand ?: "",
                        model = n.appliance.model ?: "",
                        serial = n.appliance.serialNumber ?: "",
                        diagnosis = n.diagnosis ?: "",
                        hours = n.laborHours,
                        price = if (n.priceEntered) n.total else "",
                        payment = n.paymentType ?: "CASH"
                    )
                    baseline = loaded
                    _form.value = loaded
                    _dirty.value = false
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
        _dirty.value = _form.value != baseline
        schedulePreview()
    }

    private fun normalizedHours(raw: String): String? {
        val t = raw.trim()
        return if (hoursRegex.matches(t)) t.replace(',', '.') else null
    }

    fun hoursValid(raw: String): Boolean = normalizedHours(raw) != null

    // The price is typed by hand and travels as a String, never through a Double. Blank = leave the price as it is.
    private fun priceOk(raw: String): Boolean = raw.isBlank() || priceRegex.matches(raw.trim())
    private fun normalizedPrice(raw: String): String? = raw.trim().replace(',', '.').ifBlank { null }

    private fun schedulePreview() {
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            delay(400) // wait for typing to settle
            val f = _form.value
            val hours = normalizedHours(f.hours) ?: return@launch
            repository.preview(PreviewRequest(laborHours = hours, nalogId = id, price = if (priceOk(f.price)) normalizedPrice(f.price) else null)).fold(
                onSuccess = { _preview.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    // Used before a status change: nothing to save means go straight on.
    fun saveIfDirty(onDone: () -> Unit) {
        if (_dirty.value) save(onDone) else onDone()
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
            !priceOk(f.price) -> "Cena: broj do 99.999.999,99 (najviše 2 decimale)."
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
                laborHours = hours!!,
                price = normalizedPrice(f.price),
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
