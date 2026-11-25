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
import rs.elgor.technician.ui.components.applianceLabel
import rs.elgor.technician.ui.components.formatMoney
import rs.elgor.technician.ui.components.paymentLabel

private val PAYMENT_TYPES = listOf("CASH", "CARD", "INVOICE")

// The editable part of an unfinished nalog, shown straight on the nalog screen (there is no separate edit screen).
// The customer is not editable on the phone (the web app does that). Every amount shown comes from the
// server's preview: the phone never calculates money.
@Composable
fun NalogEditFields(viewModel: NalogEditViewModel, afterDiagnosis: @Composable () -> Unit) {
    val form by viewModel.form.collectAsState()
    val applianceTypes by viewModel.applianceTypes.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

        EditCard("DIJAGNOZA / OPIS") {
            OutlinedTextField(
                value = form.diagnosis,
                onValueChange = { v -> if (v.length <= 4000) viewModel.edit { it.copy(diagnosis = v) } },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }

        afterDiagnosis()

        EditCard("PLAĆANJE") {
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
                label = { Text("Cena (RSD) - obavezna za završetak, može biti 0") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// The save button sits at the bottom, right above the status buttons.
@Composable
fun NalogSaveButton(viewModel: NalogEditViewModel, onSaved: () -> Unit) {
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { viewModel.save(onSaved) },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (isSaving) "Čuvanje…" else "Sačuvaj izmene") }
    }
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
