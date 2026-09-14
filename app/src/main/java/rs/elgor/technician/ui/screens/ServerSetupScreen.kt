package rs.elgor.technician.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import rs.elgor.technician.R
import rs.elgor.technician.data.JobRepository
import rs.elgor.technician.data.SessionManager
import rs.elgor.technician.data.remote.ElgorNetwork
import rs.elgor.technician.data.remote.NetworkConfig

// First screen shown on a fresh install, or if the app can't find a saved
// server address. Necessary because, unlike a web app served from a fixed
// origin, this phone app has no address to talk to until someone tells it
// one - either a local network IP during testing, or the real deployed
// URL once ServiceHub is hosted somewhere. Saved so this only happens once.
@Composable
fun ServerSetupScreen(
    sessionManager: SessionManager,
    onServerSaved: () -> Unit
) {
    var serverUrl by remember { mutableStateOf(NetworkConfig.getDefaultBaseUrl()) }
    var isSaving by remember { mutableStateOf(false) }
    var isTesting by remember { mutableStateOf(false) }
    val invalidUrlError = stringResource(R.string.error_invalid_url)
    var error by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_round),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(72.dp).clip(CircleShape)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(stringResource(R.string.server_setup_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.server_setup_instructions),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it; error = null },
                label = { Text(stringResource(R.string.server_address_label)) },
                placeholder = { Text(stringResource(R.string.server_address_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            
            successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val trimmed = serverUrl.trim()
                        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                            error = invalidUrlError
                            return@OutlinedButton
                        }
                        isTesting = true
                        error = null
                        successMessage = null
                        scope.launch {
                            val tempRepo = JobRepository(ElgorNetwork.create(trimmed, sessionManager))
                            tempRepo.checkHealth().fold(
                                onSuccess = { successMessage = "Veza potvrđena!" },
                                onFailure = { error = "Greška: ${it.message}" }
                            )
                            isTesting = false
                        }
                    },
                    enabled = !isTesting && !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Testiraj vezu")
                    }
                }

                Button(
                    onClick = {
                        val trimmed = serverUrl.trim()
                        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                            error = invalidUrlError
                            return@Button
                        }
                        isSaving = true
                        scope.launch {
                            sessionManager.saveServerUrl(trimmed)
                            isSaving = false
                            onServerSaved()
                        }
                    },
                    enabled = !isSaving && !isTesting && serverUrl.trim().length > 10,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.btn_continue))
                    }
                }
            }

            Text(
                stringResource(R.string.server_setup_footer),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
