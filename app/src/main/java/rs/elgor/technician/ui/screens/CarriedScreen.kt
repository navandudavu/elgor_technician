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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.CarriedViewModel
import rs.elgor.technician.model.CarriedItem
import rs.elgor.technician.ui.components.printCarried

// Zaduzivanje / razduzivanje: what the technician carries. Tap a line to change it, the bin removes it.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarriedScreen(viewModel: CarriedViewModel, isAdmin: Boolean, myName: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val items by viewModel.items.collectAsState()
    val technicians by viewModel.technicians.collectAsState()
    val technicianId by viewModel.technicianId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val magResults by viewModel.magResults.collectAsState()

    // adding = the "new line" dialog is open; dialogFor = the line being changed (null when none)
    var dialogFor by remember { mutableStateOf<CarriedItem?>(null) }
    var adding by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<CarriedItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zaduživanje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Nazad") }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = {
                            val who = if (isAdmin) technicians.firstOrNull { it.id == technicianId }?.name ?: "" else myName
                            printCarried(context, who, items)
                        }) { Icon(Icons.Default.Print, contentDescription = "Štampaj") }
                    }
                }
            )
        },
        floatingActionButton = {
            if (technicianId != null) {
                ExtendedFloatingActionButton(
                    onClick = { adding = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Dodaj") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            if (isAdmin) {
                PickerField(
                    label = "Serviser",
                    value = technicians.firstOrNull { it.id == technicianId }?.name ?: "—",
                    options = technicians.map { it.id.toString() to it.name },
                    onPick = { code -> viewModel.selectTechnician(code.toLong()) }
                )
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }

            when {
                isLoading && items.isEmpty() -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
                items.isEmpty() -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("Još ništa nije zaduženo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { c ->
                        Card(Modifier.fillMaxWidth().clickable { dialogFor = c }) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(c.name, fontWeight = FontWeight.SemiBold)
                                    c.sku?.takeIf { it.isNotBlank() }?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text("× ${c.quantity}", fontWeight = FontWeight.Bold)
                                IconButton(onClick = { deleting = c }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Ukloni")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (adding || dialogFor != null) {
        val editing = dialogFor
        var sku by remember(editing) { mutableStateOf(editing?.sku ?: "") }
        var name by remember(editing) { mutableStateOf(editing?.name ?: "") }
        var qty by remember(editing) { mutableStateOf(editing?.quantity?.toString() ?: "1") }
        var magQuery by remember(editing) { mutableStateOf("") }
        val close = { viewModel.clearMagacin(); adding = false; dialogFor = null }
        AlertDialog(
            onDismissRequest = close,
            title = { Text(if (editing == null) "Dodaj stavku" else "Izmeni stavku") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = magQuery, onValueChange = { magQuery = it; viewModel.searchMagacin(it) },
                        label = { Text("Izaberi iz magacina (nije obavezno)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    magResults.forEach { p ->
                        Column(
                            Modifier.fillMaxWidth().clickable {
                                sku = p.sku ?: ""
                                name = p.name
                                magQuery = ""
                                viewModel.clearMagacin()
                            }.padding(vertical = 4.dp)
                        ) {
                            Text(p.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${p.sku ?: "-"} · na stanju: ${p.stockQty}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedTextField(value = sku, onValueChange = { if (it.length <= 60) sku = it },
                        label = { Text("Šifra (nije obavezna)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = name, onValueChange = { if (it.length <= 200) name = it },
                        label = { Text("Naziv") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = qty, onValueChange = { if (it.length <= 7) qty = it },
                        label = { Text("Količina") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(enabled = !isSaving, onClick = { viewModel.save(editing?.id, sku, name, qty, onDone = close) }) {
                    Text("Sačuvaj")
                }
            },
            dismissButton = { TextButton(onClick = close) { Text("Otkaži") } }
        )
    }

    deleting?.let { c ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Ukloniti stavku?") },
            text = { Text("${c.name} × ${c.quantity}") },
            confirmButton = {
                TextButton(enabled = !isSaving, onClick = { viewModel.delete(c.id); deleting = null }) {
                    Text("Ukloni", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Otkaži") } }
        )
    }
}
