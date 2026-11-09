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
import rs.elgor.technician.model.CreateNalogRequest
import rs.elgor.technician.model.NalogCustomer
import rs.elgor.technician.model.NalogPreview
import rs.elgor.technician.model.NewCustomerInput
import rs.elgor.technician.model.PartLineInput
import rs.elgor.technician.model.PartView
import rs.elgor.technician.model.PreviewRequest
import rs.elgor.technician.model.TechnicianView
import java.util.UUID

data class CreateForm(
    val customer: NalogCustomer? = null,        // an existing customer that was picked
    val customerQuery: String = "",
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val locationNotes: String = "",
    val applianceType: String = "AIR_CONDITIONER",
    val brand: String = "",
    val model: String = "",
    val serial: String = "",
    val diagnosis: String = "",
    val hours: String = "",
    val payment: String = "CASH",
    val initialStatus: String = "DRAFT",        // admin only
    val scheduledAtIso: String? = null,         // UTC instant sent to the server
    val scheduledLabel: String? = null,         // the same moment, as the user picked it
    val assignedToId: Long? = null,             // admin only
    val lines: List<EditLine> = emptyList()
)

// Creates a nalog, like the web form. Money is never computed here: the totals shown come from the server's
// preview endpoint and the request carries only hours and part quantities. One Idempotency-Key per form, so a
// retry after a timeout can never create two nalozi. A technician's nalog is always DRAFT and assigned to them
// (the server enforces it, the phone just does not offer the choice).
class NalogCreateViewModel(
    private val repository: NalogRepository,
    val isAdmin: Boolean
) : ViewModel() {

    private val hoursRegex = Regex("^\\d{1,3}([.,]\\d{1,2})?$")
    private var idempotencyKey = UUID.randomUUID().toString()

    private val _form = MutableStateFlow(CreateForm())
    val form: StateFlow<CreateForm> = _form.asStateFlow()

    private val _preview = MutableStateFlow<NalogPreview?>(null)
    val preview: StateFlow<NalogPreview?> = _preview.asStateFlow()

    private val _previewError = MutableStateFlow<String?>(null)
    val previewError: StateFlow<String?> = _previewError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _customerResults = MutableStateFlow<List<NalogCustomer>>(emptyList())
    val customerResults: StateFlow<List<NalogCustomer>> = _customerResults.asStateFlow()

    private val _technicians = MutableStateFlow<List<TechnicianView>>(emptyList())
    val technicians: StateFlow<List<TechnicianView>> = _technicians.asStateFlow()

    private val _techniciansError = MutableStateFlow<String?>(null)
    val techniciansError: StateFlow<String?> = _techniciansError.asStateFlow()

    private val _partResults = MutableStateFlow<List<PartView>>(emptyList())
    val partResults: StateFlow<List<PartView>> = _partResults.asStateFlow()

    private val _partsLoading = MutableStateFlow(false)
    val partsLoading: StateFlow<Boolean> = _partsLoading.asStateFlow()

    private var previewJob: Job? = null
    private var customerJob: Job? = null
    private var partsJob: Job? = null

    init {
        if (isAdmin) loadTechnicians()
        schedulePreview()
    }

    fun loadTechnicians() {
        viewModelScope.launch {
            repository.technicians().fold(
                onSuccess = { _technicians.value = it; _techniciansError.value = null },
                onFailure = { _techniciansError.value = "Lista servisera nije učitana: ${it.message}" }
            )
        }
    }

    fun edit(transform: (CreateForm) -> CreateForm) {
        _form.value = transform(_form.value)
        schedulePreview()
    }

    // An empty field means 0 hours (the web form starts at 0.00).
    private fun normalizedHours(raw: String): String? {
        val t = raw.trim()
        if (t.isEmpty()) return "0.00"
        return if (hoursRegex.matches(t)) t.replace(',', '.') else null
    }

    fun hoursValid(raw: String): Boolean = normalizedHours(raw) != null

    private fun lineInputs(f: CreateForm) = f.lines.map { PartLineInput(it.partId, it.quantity) }

    private fun schedulePreview() {
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            delay(400) // wait for typing to settle
            val f = _form.value
            val hours = normalizedHours(f.hours)
            if (hours == null) {
                _preview.value = null
                return@launch
            }
            repository.preview(PreviewRequest(lineInputs(f), hours, null)).fold(
                onSuccess = { _preview.value = it; _previewError.value = null },
                onFailure = { _preview.value = null; _previewError.value = it.message }
            )
        }
    }

    fun onCustomerQuery(q: String) {
        _form.value = _form.value.copy(customerQuery = q)
        customerJob?.cancel()
        if (q.trim().length < 2) {
            _customerResults.value = emptyList()
            return
        }
        customerJob = viewModelScope.launch {
            delay(300)
            repository.searchCustomers(q.trim()).fold(
                onSuccess = { _customerResults.value = it },
                onFailure = { /* the next keystroke retries */ }
            )
        }
    }

    fun pickCustomer(c: NalogCustomer) {
        _customerResults.value = emptyList()
        _form.value = _form.value.copy(customer = c, customerQuery = "")
    }

    fun clearCustomer() {
        _form.value = _form.value.copy(customer = null)
    }

    fun searchParts(query: String) {
        partsJob?.cancel()
        partsJob = viewModelScope.launch {
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

    fun submit(onCreated: (Long) -> Unit) {
        if (_isSaving.value) return
        val f = _form.value
        val hours = normalizedHours(f.hours)
        val problem = when {
            f.customer == null && f.fullName.isBlank() -> "Unesite ime i prezime korisnika."
            f.customer == null && f.phone.isBlank() -> "Unesite telefon korisnika."
            f.customer == null && f.address.isBlank() -> "Unesite adresu korisnika."
            f.brand.isBlank() -> "Unesite marku uređaja."
            f.diagnosis.isBlank() -> "Unesite opis kvara / dijagnozu."
            hours == null -> "Sati rada: broj do 999,99 (najviše 2 decimale)."
            else -> null
        }
        if (problem != null) {
            _error.value = problem
            return
        }
        val validHours = hours ?: return
        val request = CreateNalogRequest(
            customerId = f.customer?.id,
            newCustomer = if (f.customer == null) NewCustomerInput(
                fullName = f.fullName.trim(),
                address = f.address.trim(),
                city = f.city.trim().ifBlank { null },
                phone = f.phone.trim(),
                locationNotes = f.locationNotes.trim().ifBlank { null }
            ) else null,
            appliance = ApplianceInput(
                type = f.applianceType,
                brand = f.brand.trim(),
                model = f.model.trim().ifBlank { null },
                serialNumber = f.serial.trim().ifBlank { null }
            ),
            diagnosis = f.diagnosis.trim(),
            partLines = lineInputs(f),
            laborHours = validHours,
            paymentType = f.payment,
            status = if (isAdmin) f.initialStatus else "DRAFT",
            scheduledAt = f.scheduledAtIso,
            assignedToId = if (isAdmin) f.assignedToId else null
        )
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            repository.create(request, idempotencyKey).fold(
                onSuccess = { n ->
                    idempotencyKey = UUID.randomUUID().toString()   // the next form gets its own key
                    onCreated(n.id)
                },
                onFailure = { _error.value = it.message }
            )
            _isSaving.value = false
        }
    }
}

class NalogCreateViewModelFactory(
    private val repository: NalogRepository,
    private val isAdmin: Boolean
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = NalogCreateViewModel(repository, isAdmin) as T
}
