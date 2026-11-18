package rs.elgor.technician.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.NalogCreateViewModel
import rs.elgor.technician.ui.components.applianceLabel
import rs.elgor.technician.ui.components.formatMoney
import rs.elgor.technician.ui.components.nalogStatusLabel
import rs.elgor.technician.ui.components.paymentLabel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val CREATE_PAYMENT_TYPES = listOf("CASH", "CARD", "INVOICE")

// New radni nalog, same sections and wording as the web form (NalogForm.tsx): Korisnik, Uređaj, Opis kvara,
// Cena (typed by hand), Plaćanje. The phone never calculates money.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NalogCreateScreen(
    viewModel: NalogCreateViewModel,
    onBack: () -> Unit,
    onCreated: (Long) -> Unit
) {
    val form by viewModel.form.collectAsState()
    val applianceTypes by viewModel.applianceTypes.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val customerResults by viewModel.customerResults.collectAsState()
    val technicians by viewModel.technicians.collectAsState()
    val techniciansError by viewModel.techniciansError.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novi radni nalog", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EditCard("KORISNIK") {
                val picked = form.customer
                if (picked != null) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(picked.fullName, fontWeight = FontWeight.SemiBold)
                            val addr = listOfNotNull(picked.address, picked.city).filter { it.isNotBlank() }.joinToString(", ")
                            val line = listOf(addr, picked.phone ?: "").filter { it.isNotBlank() }.joinToString(" · ")
                            if (line.isNotBlank()) Text(line, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        OutlinedButton(onClick = { viewModel.clearCustomer() }) { Text("Promeni") }
                    }
                } else {
                    OutlinedTextField(
                        value = form.customerQuery,
                        onValueChange = { viewModel.onCustomerQuery(it) },
                        label = { Text("Pretraži postojećeg korisnika (ime ili telefon)…") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    customerResults.forEach { c ->
                        TextButton(onClick = { viewModel.pickCustomer(c) }, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(c.fullName, fontWeight = FontWeight.SemiBold)
                                Text(
                                    listOfNotNull(c.phone, c.address).filter { it.isNotBlank() }.joinToString(" · "),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    Text("NOVI KORISNIK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = form.fullName,
                        onValueChange = { v -> if (v.length <= 150) viewModel.edit { it.copy(fullName = v) } },
                        label = { Text("Ime i prezime") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.phone,
                        onValueChange = { v -> if (v.length <= 40) viewModel.edit { it.copy(phone = v) } },
                        label = { Text("Telefon") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.address,
                        onValueChange = { v -> if (v.length <= 255) viewModel.edit { it.copy(address = v) } },
                        label = { Text("Adresa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.city,
                        onValueChange = { v -> if (v.length <= 100) viewModel.edit { it.copy(city = v) } },
                        label = { Text("Mesto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = form.locationNotes,
                        onValueChange = { v -> if (v.length <= 500) viewModel.edit { it.copy(locationNotes = v) } },
                        label = { Text("Detalji lokacije (sprat, ulaz…)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            EditCard("UREĐAJ") {
                PickerField(
                    label = "Vrsta",
                    value = applianceLabel(form.applianceType).ifBlank { form.applianceType },
                    options = applianceTypes.map { it to applianceLabel(it) },
                    onPick = { code -> viewModel.edit { it.copy(applianceType = code) } }
                )
                OutlinedTextField(
                    value = form.brand,
                    onValueChange = { v -> if (v.length <= 100) viewModel.edit { it.copy(brand = v) } },
                    label = { Text("Marka") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.model,
                    onValueChange = { v -> if (v.length <= 100) viewModel.edit { it.copy(model = v) } },
                    label = { Text("Model") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.serial,
                    onValueChange = { v -> if (v.length <= 100) viewModel.edit { it.copy(serial = v) } },
                    label = { Text("Serijski broj") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            EditCard("OPIS KVARA / DIJAGNOZA") {
                OutlinedTextField(
                    value = form.diagnosis,
                    onValueChange = { v -> if (v.length <= 4000) viewModel.edit { it.copy(diagnosis = v) } },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            EditCard("CENA") {
                OutlinedTextField(
                    value = form.price,
                    onValueChange = { v -> if (v.length <= 12) viewModel.edit { it.copy(price = v) } },
                    label = { Text("Cena (RSD, nije obavezno)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            EditCard("PLAĆANJE") {
                PickerField(
                    label = "Plaćanje",
                    value = paymentLabel(form.payment).ifBlank { form.payment },
                    options = CREATE_PAYMENT_TYPES.map { it to paymentLabel(it) },
                    onPick = { code -> viewModel.edit { it.copy(payment = code) } }
                )
                ScheduleField(
                    label = form.scheduledLabel,
                    onPicked = { iso, text -> viewModel.edit { it.copy(scheduledAtIso = iso, scheduledLabel = text) } },
                    onClear = { viewModel.edit { it.copy(scheduledAtIso = null, scheduledLabel = null) } }
                )
                if (viewModel.isAdmin) {
                    PickerField(
                        label = "Dodeli serviseru",
                        value = technicians.firstOrNull { it.id == form.assignedToId }?.name ?: "Nedodeljen",
                        options = listOf("" to "Nedodeljen") + technicians.map { it.id.toString() to it.name },
                        onPick = { code -> viewModel.edit { it.copy(assignedToId = code.toLongOrNull()) } }
                    )
                    PickerField(
                        label = "Početni status",
                        value = nalogStatusLabel(form.initialStatus),
                        options = listOf("DRAFT", "DISPATCHED").map { it to nalogStatusLabel(it) },
                        onPick = { code -> viewModel.edit { it.copy(initialStatus = code) } }
                    )
                    techniciansError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = { viewModel.submit(onCreated) },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isSaving) "Čuvanje…" else "Kreiraj nalog") }
        }
    }
}

// "Planirani dolazak": date only (no hour). The server gets local noon of that day as a UTC instant, so the day never flips.
@Composable
private fun ScheduleField(label: String?, onPicked: (String, String) -> Unit, onClear: () -> Unit) {
    val context = LocalContext.current
    Column {
        Text("Planirani dolazak", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = {
                    val now = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val picked = Calendar.getInstance().apply {
                                set(year, month, day, 12, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val utc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                            val shown = SimpleDateFormat("dd.MM.yyyy.", Locale.US)
                            onPicked(utc.format(picked.time), shown.format(picked.time))
                        },
                        now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.weight(1f)
            ) { Text(label ?: "Nije zakazano") }
            if (label != null) TextButton(onClick = onClear) { Text("Ukloni") }
        }
    }
}
