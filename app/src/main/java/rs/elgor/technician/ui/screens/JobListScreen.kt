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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import rs.elgor.technician.R
import rs.elgor.technician.data.AuthState
import rs.elgor.technician.data.AuthViewModel
import rs.elgor.technician.data.JobsViewModel
import rs.elgor.technician.model.Job
import rs.elgor.technician.ui.components.PriorityChip
import rs.elgor.technician.ui.components.StatusChip

// Uses a manual refresh button rather than Material3's PullToRefreshBox -
// that API only became available in Material3 1.3.0, and this project is
// pinned to compose-bom 2024.06.00 (Material3 1.2.x) to match the existing
// ELGOR customer app's dependency versions. Bumping the BOM just for this
// would cascade into a compileSdk bump too - not worth it for a nice-to-have.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(
    jobsViewModel: JobsViewModel,
    authViewModel: AuthViewModel,
    onJobClick: (Int) -> Unit
) {
    val jobs by jobsViewModel.jobs.collectAsState()
    val isLoading by jobsViewModel.isLoading.collectAsState()
    val error by jobsViewModel.error.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val userName = (authState as? AuthState.LoggedIn)?.user?.name ?: ""

    LaunchedEffect(Unit) { jobsViewModel.loadJobs() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.my_jobs), fontWeight = FontWeight.Bold)
                        if (userName.isNotBlank()) {
                            Text(userName, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { jobsViewModel.loadJobs() }, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh))
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = stringResource(R.string.sign_out))
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading && jobs.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                error != null && jobs.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)
                }
                jobs.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_jobs_assigned),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(jobs, key = { it.id }) { job ->
                        JobListItem(job = job, onClick = { onJobClick(job.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun JobListItem(job: Job, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            job.customerName?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(job.status)
                PriorityChip(job.priority)
                job.hoursLogged?.let { hours ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            stringResource(R.string.hours_suffix, hours.toString()),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
