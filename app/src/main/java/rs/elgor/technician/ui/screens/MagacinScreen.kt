package rs.elgor.technician.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.MagacinViewModel
import rs.elgor.technician.model.PartView
import rs.elgor.technician.model.StockMovement
import rs.elgor.technician.ui.components.formatInstant

private fun reasonLabel(reason: String): String = when (reason) {
    "INITIAL" -> "Početno stanje"
    "MANUAL" -> "Ručna korekcija"
    "NALOG_CREATE" -> "Utrošeno na nalogu"
    "NALOG_CANCEL" -> "Nalog otkazan"
    "NALOG_EDIT" -> "Nalog izmenjen"
    "NALOG_DELETE" -> "Nalog obrisan"
    else -> reason
}

// Magacin: every part with its stock. Anyone signed in may correct the stock and read the history; new parts and
// price changes are made on the web by an admin or manager. Prices are shown exactly as the server sends them.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagacinScreen(viewModel: MagacinViewModel, onBack: () -> Unit) {
    val parts by viewModel.parts.collectAsState()
    val query by viewModel.query.collectAsState()
    val showInactive by viewModel.showInactive.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val history by viewModel.history.collectAsState()

    var adjusting by remember { mutableStateOf<PartView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Magacin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Nazad") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setQuery(it) },
                label = { Text("Pretraga po nazivu ili šifri") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Row(
                Modifier.fillMaxWidth().clickable { viewModel.setShowInactive(!showInactive) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = showInactive, onCheckedChange = { viewModel.setShowInactive(it) })
                Text("Prikaži i ukinute delove")
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp)) }

            when {
                isLoading && parts.isEmpty() -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }

                parts.isEmpty() -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { Text("Nema delova.", color = MaterialTheme.colorScheme.onSurfaceVariant) }

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parts, key = { it.id }) { p ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(p.name + if (p.active) "" else " (ukinut)", fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "${p.sku ?: "-"} · cena ${p.unitPrice}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        "${p.stockQty}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = if (p.stockQty < 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row {
                                    TextButton(onClick = { viewModel.clearError(); adjusting = p }) { Text("Korekcija stanja") }
                                    TextButton(onClick = { viewModel.openHistory(p) }) { Text("Istorija") }
                                }
                            }
                        }
                    }
                    if (hasMore) {
                        item {
                            TextButton(onClick = { viewModel.loadMore() }, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                                Text("Učitaj još")
                            }
                        }
                    }
                }
            }
        }
    }

    adjusting?.let { part ->
        var add by remember(part.id) { mutableStateOf(true) }
        var qty by remember(part.id) { mutableStateOf("1") }
        var note by remember(part.id) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { adjusting = null },
            title = { Text(part.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Na stanju: ${part.stockQty}")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = add, onClick = { add = true })
                        Text("Dodaj", modifier = Modifier.clickable { add = true })
                        RadioButton(selected = !add, onClick = { add = false })
                        Text("Skini", modifier = Modifier.clickable { add = false })
                    }
                    OutlinedTextField(
                        value = qty,
                        onValueChange = { if (it.length <= 6) qty = it },
                        label = { Text("Količina") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { if (it.length <= 200) note = it },
                        label = { Text("Napomena (nije obavezna)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isSaving,
                    onClick = { viewModel.adjust(part, qty, add, note) { adjusting = null } }
                ) { Text("Primeni") }
            },
            dismissButton = { TextButton(onClick = { adjusting = null }) { Text("Otkaži") } }
        )
    }

    history?.let { rows ->
        AlertDialog(
            onDismissRequest = { viewModel.closeHistory() },
            title = { Text("Istorija stanja") },
            text = {
                if (rows.isEmpty()) {
                    Text("Nema zapisa.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(rows, key = { it.id }) { m -> MovementRow(m) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.closeHistory() }) { Text("Zatvori") } }
        )
    }
}

@Composable
private fun MovementRow(m: StockMovement) {
    Column {
        Text(
            (if (m.delta > 0) "+" else "") + "${m.delta} · ${reasonLabel(m.reason)}",
            fontWeight = FontWeight.SemiBold
        )
        Text(
            listOfNotNull(m.actorName, formatInstant(m.createdAt), m.note?.takeIf { it.isNotBlank() }).joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
