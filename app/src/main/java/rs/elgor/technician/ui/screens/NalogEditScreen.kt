package rs.elgor.technician.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.NalogEditViewModel
import rs.elgor.technician.ui.components.HoursPicker
import rs.elgor.technician.ui.components.applianceLabel
import rs.elgor.technician.ui.components.formatMoney
import rs.elgor.technician.ui.components.paymentLabel

private val APPLIANCE_TYPES = listOf(
    "AIR_CONDITIONER", "REFRIGERATOR", "FREEZER", "WASHING_MACHINE", "DRYER", "DISHWASHER",
    "OVEN", "STOVE", "WATER_HEATER", "BOILER", "OTHER"
)
private val PAYMENT_TYPES = listOf("CASH", "CARD", "INVOICE")

// Edit an unfinished nalog from the phone. Customer data is read-only here (web app edits it).
// Every amount shown comes from the server's preview: the phone never calculates money.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NalogEditScreen(
    viewModel: NalogEditViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val form by viewModel.form.collectAsState()
    val original by viewModel.original.collectAsState()
    val preview by viewModel.preview.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    var pickingPart by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Izmena naloga", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { padding ->
        val n = original
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                n == null && isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                n == null -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
                    Text(error ?: "Nalog nije učitan.", color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Pokušaj ponovo")
                    }
                }
                else -> Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditCard("KLIJENT (menja se na veb aplikaciji)") {
                        Text(n.customer.fullName, fontWeight = FontWeight.SemiBold)
                        val addr = listOfNotNull(n.customer.address, n.customer.city).filter { it.isNotBlank() }.joinToString(", ")
                        if (addr.isNotBlank()) Text(addr)
                    }

                    EditCard("UREĐAJ") {
                        PickerField(
                            label = "Vrsta",
                            value = applianceLabel(form.applianceType).ifBlank { form.applianceType },
                            options = APPLIANCE_TYPES.map { it to applianceLabel(it) },
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

                    EditCard("DIJAGNOZA / OPIS") {
                        OutlinedTextField(
                            value = form.diagnosis,
                            onValueChange = { v -> if (v.length <= 4000) viewModel.edit { it.copy(diagnosis = v) } },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    EditCard("IZVRŠENI RADOVI") {
                        Text(form.workDone.ifBlank { "—" })
                        Text(
                            "Menja se na ekranu naloga (otvorite nalog).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    EditCard("DELOVI") {
                        if (form.lines.isEmpty()) {
                            Text("Nema ugrađenih delova.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        form.lines.forEach { line ->
                            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(line.name, fontWeight = FontWeight.SemiBold)
                                line.sku?.takeIf { it.isNotBlank() }?.let {
                                    Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.edit { f ->
                                                    f.copy(lines = f.lines.map {
                                                        if (it.partId == line.partId) it.copy(quantity = maxOf(1, it.quantity - 1)) else it
                                                    })
                                                }
                                            },
                                            enabled = line.quantity > 1
                                        ) { Text("−") }
                                        Text("${line.quantity}", fontWeight = FontWeight.Bold)
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.edit { f ->
                                                    f.copy(lines = f.lines.map {
                                                        if (it.partId == line.partId) it.copy(quantity = minOf(999, it.quantity + 1)) else it
                                                    })
                                                }
                                            },
                                            enabled = line.quantity < 999
                                        ) { Text("+") }
                                    }
                                    preview?.partLines?.firstOrNull { it.partId == line.partId }?.let {
                                        Text(formatMoney(it.lineTotal))
                                    }
                                    TextButton(onClick = {
                                        viewModel.edit { f -> f.copy(lines = f.lines.filter { it.partId != line.partId }) }
                                    }) { Text("Ukloni") }
                                }
                            }
                        }
                        OutlinedButton(
                            onClick = { pickingPart = true },
                            enabled = form.lines.size < 50,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("+ Dodaj deo") }
                    }

                    EditCard("RAD I PLAĆANJE") {
                        HoursPicker(value = form.hours, onChange = { v -> viewModel.edit { it.copy(hours = v) } })
                        PickerField(
                            label = "Plaćanje",
                            value = paymentLabel(form.payment).ifBlank { form.payment },
                            options = PAYMENT_TYPES.map { it to paymentLabel(it) },
                            onPick = { code -> viewModel.edit { it.copy(payment = code) } }
                        )
                    }

                    EditCard("CENA") {
                        OutlinedTextField(
                            value = form.price,
                            onValueChange = { v -> if (v.length <= 12) viewModel.edit { it.copy(price = v) } },
                            label = { Text("Cena (RSD)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Button(
                        onClick = { viewModel.save(onSaved) },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (isSaving) "Čuvanje…" else "Sačuvaj izmene") }
                }
            }
        }
    }

    if (pickingPart) {
        PartPickerDialog(viewModel = viewModel, onClose = { pickingPart = false })
    }
}

@Composable
private fun PartPickerDialog(viewModel: NalogEditViewModel, onClose: () -> Unit) {
    val results by viewModel.partResults.collectAsState()
    val loading by viewModel.partsLoading.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(query) { viewModel.searchParts(query) }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Dodaj deo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Naziv ili šifra") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (loading) Text("Pretraga…", style = MaterialTheme.typography.bodySmall)
                Column(Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState())) {
                    if (!loading && results.isEmpty()) {
                        Text("Nema rezultata.", style = MaterialTheme.typography.bodySmall)
                    }
                    results.forEach { p ->
                        val inStock = p.stockQty > 0
                        TextButton(
                            onClick = { viewModel.addPart(p); onClose() },
                            enabled = inStock,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(p.name)
                                Text(
                                    (p.sku?.let { "$it · " } ?: "") + formatMoney(p.unitPrice) +
                                        " · " + (if (inStock) "na stanju: ${p.stockQty}" else "nema na stanju"),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onClose) { Text("Zatvori") } }
    )
}

@Composable
internal fun PickerField(label: String, value: String, options: List<Pair<String, String>>, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) { Text(value) }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { (code, text) ->
                    DropdownMenuItem(text = { Text(text) }, onClick = { open = false; onPick(code) })
                }
            }
        }
    }
}

@Composable
internal fun EditCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
internal fun EditMoneyRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (bold) FontWeight.Bold else null, modifier = Modifier.weight(1f).padding(end = 8.dp))
        Text(value, fontWeight = if (bold) FontWeight.Bold else null)
    }
}
