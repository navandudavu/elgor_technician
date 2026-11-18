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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.StockDocViewModel
import rs.elgor.technician.model.StockDocSummary
import rs.elgor.technician.ui.components.formatInstant

// Magacin tab: the technician's own zaduzenja / razduzenja. Read only: only admin and manager create or change them.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagacinScreen(viewModel: StockDocViewModel, onBack: () -> Unit) {
    val items by viewModel.items.collectAsState()
    val details by viewModel.details.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val error by viewModel.error.collectAsState()
    var openId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Magacin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Nazad") }
                },
                actions = {
                    IconButton(onClick = { viewModel.load() }, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Osveži")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
            when {
                isLoading && items.isEmpty() -> Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
                items.isEmpty() -> Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) { Text("Još nema zaduženja ni razduženja.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { d ->
                        val open = openId == d.id
                        Card(Modifier.fillMaxWidth().clickable {
                            openId = if (open) null else d.id
                            if (!open) viewModel.loadDetail(d.id)
                        }) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    StockDocTypeChip(d.type)
                                    Text(
                                        "  ${stockDocTitle(d)}",
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("${d.lineCount} st.", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    formatInstant(d.docAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (open) {
                                    val doc = details[d.id]
                                    if (doc == null) {
                                        CircularProgressIndicator(Modifier.padding(4.dp))
                                    } else {
                                        doc.lines.forEach { l ->
                                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(l.name)
                                                    l.sku?.takeIf { it.isNotBlank() }?.let {
                                                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                                Text("${trimQty(l.quantity)} ${l.unit ?: ""}".trim(), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
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
}

private fun stockDocTitle(d: StockDocSummary) =
    (if (d.type == "RAZ") "Razduženje" else "Zaduženje") + " br. ${d.docNo}"

/** "2.000" -> "2", "1.500" -> "1.5": display only, the value itself stays a String. */
private fun trimQty(q: String): String =
    if (q.contains('.')) q.trimEnd('0').trimEnd('.') else q

@Composable
private fun StockDocTypeChip(type: String) {
    val raz = type == "RAZ"
    Surface(
        color = if (raz) Color(0xFF5A3E85) else Color(0xFF1F6B3A),
        contentColor = Color.White,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = if (raz) "Razduženje" else "Zaduženje",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
