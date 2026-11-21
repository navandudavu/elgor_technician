package rs.elgor.technician.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import rs.elgor.technician.data.HeldPartsViewModel

// Magacin tab: only the parts the technician holds (šifra, naziv, jed. mere). No quantities, no documents.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagacinScreen(viewModel: HeldPartsViewModel, onBack: () -> Unit) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) delay(250)      // typing debounce
        viewModel.load(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Magacin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Nazad") }
                },
                actions = {
                    IconButton(onClick = { viewModel.load(query) }, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Osveži")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Pretraga po šifri ili nazivu") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
            when {
                isLoading && items.isEmpty() -> Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
                items.isEmpty() -> Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) { Text("Nema zaduženih delova.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.partId }) { p ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(p.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    listOfNotNull(p.sku?.takeIf { it.isNotBlank() }, p.unit?.takeIf { it.isNotBlank() }).joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
