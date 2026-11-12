package rs.elgor.technician.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.AuthState
import rs.elgor.technician.data.AuthViewModel
import rs.elgor.technician.data.NalogListViewModel
import rs.elgor.technician.model.NalogSummary
import rs.elgor.technician.ui.components.NalogStatusChip
import rs.elgor.technician.ui.components.applianceLabel
import rs.elgor.technician.ui.components.formatMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NalogListScreen(
    viewModel: NalogListViewModel,
    authViewModel: AuthViewModel,
    onNalogClick: (Long) -> Unit,
    onCreateNalog: () -> Unit,
    onOpenOldJobs: (() -> Unit)? = null
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val userName = (authState as? AuthState.LoggedIn)?.user?.name ?: ""
    val isAdmin = (authState as? AuthState.LoggedIn)?.user?.role == "admin"

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        floatingActionButton = {
            // Only admins make nalozi; technicians work the ones they are given.
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = onCreateNalog,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Novi nalog") }
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Radni nalozi", fontWeight = FontWeight.Bold)
                        if (userName.isNotBlank()) Text(userName, style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    onOpenOldJobs?.let { open ->
                        androidx.compose.material3.TextButton(onClick = open) { Text("Stari poslovi") }
                    }
                    IconButton(onClick = { viewModel.load() }, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Osveži")
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Odjava")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading && items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null && items.isEmpty() -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)
                }
                items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Trenutno nemate radnih naloga.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items, key = { it.id }) { n -> NalogListItem(n) { onNalogClick(n.id) } }
                    if (error != null) {
                        item { Text(error ?: "", color = MaterialTheme.colorScheme.error) }
                    }
                    if (hasMore) {
                        item {
                            OutlinedButton(
                                onClick = { viewModel.loadMore() },
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(if (isLoading) "Učitavanje…" else "Učitaj još") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NalogListItem(n: NalogSummary, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(n.nalogNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                NalogStatusChip(n.status)
            }
            n.customerName?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            val device = listOfNotNull(applianceLabel(n.applianceType).ifBlank { null }, n.applianceBrand).joinToString(" · ")
            if (device.isNotBlank()) {
                Text(device, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.padding(top = 6.dp))
            Text(formatMoney(n.total), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}
