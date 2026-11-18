package rs.elgor.technician.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import rs.elgor.technician.model.NoteView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.FileProvider
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import rs.elgor.technician.data.NalogEditViewModel
import rs.elgor.technician.data.NalogRepository
import rs.elgor.technician.model.NalogPhotoView
import rs.elgor.technician.util.prepareJpeg
import java.io.File
import java.io.FileOutputStream
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rs.elgor.technician.data.NalogDetailViewModel
import rs.elgor.technician.model.Nalog
import rs.elgor.technician.ui.components.NalogStatusChip
import rs.elgor.technician.ui.components.applianceLabel
import rs.elgor.technician.ui.components.formatInstant
import rs.elgor.technician.ui.components.formatInstantDate
import rs.elgor.technician.ui.components.formatMoney
import rs.elgor.technician.ui.components.printNalog
import rs.elgor.technician.ui.components.nalogStatusLabel
import rs.elgor.technician.ui.components.paymentLabel

// What a technician may do next. Mirrors NalogStatusRules.java; CANCELLED is admin-only, so it is
// never offered here (the server enforces all of this anyway - this only avoids dead buttons).
private val WORKING_STATUSES = listOf("IN_PROGRESS", "AWAITING_PARTS", "RECEIVED", "TESTING", "DRIVING", "NEEDS_ELECTRICIAN")

private fun nextStatuses(status: String): List<String> = when (status) {
    "DRAFT" -> listOf("DISPATCHED")
    "DISPATCHED" -> WORKING_STATUSES
    in WORKING_STATUSES -> WORKING_STATUSES.filter { it != status } + "COMPLETED"
    else -> emptyList()
}

private val EDITABLE_STATUSES = setOf("DRAFT", "DISPATCHED") + WORKING_STATUSES

private fun startLabel(to: String) = when (to) {
    "DISPATCHED" -> "Dodeli"
    "IN_PROGRESS" -> "Počni / nastavi rad"
    "AWAITING_PARTS" -> "Čeka delove"
    "RECEIVED" -> "Preuzet u servis"
    "TESTING" -> "Testira se"
    "DRIVING" -> "Vozi se"
    "NEEDS_ELECTRICIAN" -> "Potreban elektroničar"
    "COMPLETED" -> "Završi nalog"
    else -> nalogStatusLabel(to)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NalogDetailScreen(
    viewModel: NalogDetailViewModel,
    repository: NalogRepository,
    currentUserId: Long,
    isAdmin: Boolean,
    editViewModel: NalogEditViewModel,
    onBack: () -> Unit
) {
    val nalog by viewModel.nalog.collectAsState()
    val history by viewModel.history.collectAsState()
    val photos by viewModel.photos.collectAsState()
    val notes by viewModel.notes.collectAsState()
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var confirmCancel by rememberSaveable { mutableStateOf(false) }
    var cancelReason by rememberSaveable { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val editDirty by editViewModel.dirty.collectAsState()
    var confirmDiscard by rememberSaveable { mutableStateOf(false) }
    val requestBack = { if (editDirty) confirmDiscard = true else onBack() }
    BackHandler(enabled = editDirty) { confirmDiscard = true }

    // Whenever the server-side nalog changes (saved, status changed), the form is reloaded so its version matches.
    val versionKey = nalog?.version
    LaunchedEffect(versionKey) {
        if (versionKey != null && editViewModel.original.value?.version != versionKey) editViewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nalog?.nalogNumber ?: "Radni nalog", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = requestBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { padding ->
        val n = nalog
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                n == null && isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                n == null -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(error ?: "Nalog nije učitan.", color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = { viewModel.load() }, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Pokušaj ponovo")
                    }
                }
                else -> Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Unfinished nalog the person may change: everything is editable right here.
                    // Otherwise (finished, cancelled, someone else's) it is the read-only view.
                    val editable = n.status in EDITABLE_STATUSES && (isAdmin || n.assignedToId == currentUserId)
                    NalogBody(
                        n = n,
                        context = context,
                        showDetails = !editable,
                        afterDiagnosis = {
                            NalogNotesSection(
                                notes = notes,
                                isBusy = isBusy,
                                isAdmin = isAdmin,
                                locked = n.status == "COMPLETED",
                                onAdd = { text, done -> viewModel.addNote(text, done) },
                                onDelete = { viewModel.deleteNote(it) }
                            )
                        }
                    )

                    if (editable) {
                        NalogEditFields(viewModel = editViewModel, afterDiagnosis = {
                            NalogNotesSection(
                                notes = notes,
                                isBusy = isBusy,
                                isAdmin = isAdmin,
                                locked = n.status == "COMPLETED",
                                onAdd = { text, done -> viewModel.addNote(text, done) },
                                onDelete = { viewModel.deleteNote(it) }
                            )
                        })
                    }

                    NalogPhotoSection(
                        nalogId = n.id,
                        photos = photos,
                        repository = repository,
                        canUpload = n.status != "CANCELLED",
                        isBusy = isBusy,
                        currentUserId = currentUserId,
                        isAdmin = isAdmin,
                        onUpload = { file, cat -> viewModel.uploadPhoto(file, cat) },
                        onDelete = { id -> viewModel.deletePhoto(id) }
                    )

                    if (editable) {
                        NalogSaveButton(viewModel = editViewModel, onSaved = { viewModel.load() })
                    }

                    // Shown ONLY on a cancelled nalog: the reason the technician (or admin) typed in.
                    if (n.status == "CANCELLED") {
                        SectionCard("RAZLOG OTKAZIVANJA") {
                            Text(if (n.cancelReason.isNullOrBlank()) "—" else n.cancelReason)
                        }
                    }

                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    // Unsaved edits are saved first, so e.g. "Završi nalog" sees the changes just typed.
                    fun changeStatusKeepingEdits(to: String) {
                        if (editable) editViewModel.saveIfDirty { viewModel.changeStatus(to) } else viewModel.changeStatus(to)
                    }

                    val next = nextStatuses(n.status)
                    val otherSteps = next.filter { it != "COMPLETED" }
                    if (otherSteps.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            otherSteps.forEach { s ->
                                OutlinedButton(
                                    onClick = { changeStatusKeepingEdits(s) },
                                    enabled = !isBusy,
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text(startLabel(s)) }
                            }
                        }
                    }

                    // Printing uses the saved nalog, so unsaved edits must be saved first.
                    OutlinedButton(
                        onClick = { printNalog(context, n, notes, revers = false) },
                        enabled = !editDirty,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Štampaj nalog") }
                    if (n.status == "RECEIVED") {
                        OutlinedButton(
                            onClick = { printNalog(context, n, notes, revers = true) },
                            enabled = !editDirty,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Štampaj revers") }
                    }
                    if (editDirty) {
                        Text("Sačuvajte izmene pre štampe.", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // "Završi nalog" comes last, below the print buttons.
                    if ("COMPLETED" in next) {
                        Button(
                            onClick = { changeStatusKeepingEdits("COMPLETED") },
                            enabled = !isBusy,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(startLabel("COMPLETED")) }
                    }

                    // Final: a cancelled nalog is never reopened. The reason is mandatory.
                    if (editable) {
                        OutlinedButton(
                            onClick = { cancelReason = ""; confirmCancel = true },
                            enabled = !isBusy,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Otkaži nalog", color = MaterialTheme.colorScheme.error) }
                    }

                    if (isAdmin && n.status != "COMPLETED") {
                        OutlinedButton(
                            onClick = { confirmDelete = true },
                            enabled = !isBusy,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Obriši nalog", color = MaterialTheme.colorScheme.error) }
                    }

                    if (history.isNotEmpty()) {
                        SectionCard("ISTORIJA") {
                            history.forEach { h ->
                                Text(
                                    "${formatInstant(h.changedAt)} · " +
                                        (h.fromStatus?.let { nalogStatusLabel(it) + " → " } ?: "") +
                                        nalogStatusLabel(h.toStatus) +
                                        (h.changedByName?.let { " · $it" } ?: ""),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (confirmDiscard) {
        AlertDialog(
            onDismissRequest = { confirmDiscard = false },
            title = { Text("Odbaciti izmene?") },
            text = { Text("Imate nesačuvane izmene na ovom nalogu.") },
            confirmButton = { TextButton(onClick = { confirmDiscard = false; onBack() }) { Text("Odbaci") } },
            dismissButton = { TextButton(onClick = { confirmDiscard = false }) { Text("Ostani") } }
        )
    }

    if (confirmCancel) {
        AlertDialog(
            onDismissRequest = { if (!isBusy) confirmCancel = false },
            title = { Text("Otkazati nalog?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Otkazan nalog se ne može ponovo otvoriti. Razlog otkazivanja je obavezan.")
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { v -> if (v.length <= 1000) cancelReason = v },
                        label = { Text("Razlog otkazivanja") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !isBusy && cancelReason.isNotBlank(),
                    onClick = { confirmCancel = false; viewModel.changeStatus("CANCELLED", cancelReason.trim()) }
                ) { Text("Otkaži nalog", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmCancel = false }) { Text("Nazad") } }
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Obrisati radni nalog?") },
            text = { Text("Nalog se trajno briše. Ovo se ne može poništiti.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; viewModel.deleteNalog(onBack) }) { Text("Obriši") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Otkaži") } }
        )
    }
}

@Composable
private fun NalogNotesSection(
    notes: List<NoteView>,
    isBusy: Boolean,
    isAdmin: Boolean,
    locked: Boolean,
    onAdd: (String, () -> Unit) -> Unit,
    onDelete: (Long) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var deleting by remember { mutableStateOf<NoteView?>(null) }

    SectionCard("KONSTATACIJA/IZVRŠENI RADOVI") {
        if (notes.isEmpty()) {
            Text("Još nema konstatacija.", style = MaterialTheme.typography.bodySmall)
        }
        notes.forEach { note ->
            Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(note.body, style = MaterialTheme.typography.bodyMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        (note.authorName?.let { "$it · " } ?: "") + formatInstant(note.createdAt),
                        style = MaterialTheme.typography.labelSmall
                    )
                    if (isAdmin && !locked) {
                        TextButton(onClick = { deleting = note }, enabled = !isBusy) { Text("Obriši") }
                    }
                }
            }
        }
        if (locked) {
            Text(
                "Nalog je završen, konstatacije se ne mogu menjati.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 2000) text = it },
                label = { Text("Nova konstatacija") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onAdd(text) { text = "" } },
                enabled = !isBusy && text.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) { Text("Dodaj konstataciju") }
        }
    }

    deleting?.let { note ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Obrisati konstataciju?") },
            text = { Text(note.body) },
            confirmButton = { TextButton(onClick = { deleting = null; onDelete(note.id) }) { Text("Obriši") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Otkaži") } }
        )
    }
}

@Composable
private fun NalogBody(
    n: Nalog,
    context: Context,
    showDetails: Boolean,
    afterDiagnosis: @Composable () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        NalogStatusChip(n.status)
        n.assignedToName?.let { Text("Serviser: $it", style = MaterialTheme.typography.labelMedium) }
    }

    SectionCard("KLIJENT") {
        Text(n.customer.fullName, fontWeight = FontWeight.SemiBold)
        val addr = listOfNotNull(n.customer.address, n.customer.city).filter { it.isNotBlank() }.joinToString(", ")
        if (addr.isNotBlank()) Text(addr)
        n.customer.phone?.takeIf { it.isNotBlank() }?.let { Text(it) }
        n.customer.locationNotes?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
            if (!n.customer.phone.isNullOrBlank()) {
                OutlinedButton(onClick = { dial(context, n.customer.phone) }) { Text("Pozovi") }
            }
            if (addr.isNotBlank()) {
                OutlinedButton(onClick = { navigate(context, addr) }) { Text("Navigacija") }
            }
        }
    }

    n.scheduledAt?.let { SectionCard("ZAKAZANO") { Text(formatInstantDate(it)) } }

    // Read-only view (finished, cancelled, someone else's nalog), in the same order as the editable one:
    // uređaj, dijagnoza, konstatacija/izvršeni radovi, cena; photos follow below.
    if (showDetails) {
        SectionCard("UREĐAJ") {
            Text(listOfNotNull(applianceLabel(n.appliance.type).ifBlank { null }, n.appliance.brand, n.appliance.model).joinToString(" · "))
            n.appliance.serialNumber?.takeIf { it.isNotBlank() }?.let { Text("S/N: $it") }
        }

        if (!n.diagnosis.isNullOrBlank()) {
            SectionCard("DIJAGNOZA / OPIS") { Text(n.diagnosis) }
        }

        afterDiagnosis()

        SectionCard("CENA") {
            MoneyRow("CENA", formatMoney(n.total), bold = true)
            Text("Plaćanje: ${paymentLabel(n.paymentType)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun MoneyRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (bold) FontWeight.Bold else null, modifier = Modifier.weight(1f).padding(end = 8.dp))
        Text(value, fontWeight = if (bold) FontWeight.Bold else null)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

private fun dial(context: Context, phone: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.filter { it.isDigit() || it == '+' })))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Nema aplikacije za pozive", Toast.LENGTH_LONG).show()
    }
}

// Prefer Google Maps, fall back to any geo: app, never crash.
private fun navigate(context: Context, address: String) {
    val geo = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(address)}"))
    try {
        context.startActivity(Intent(geo).setPackage("com.google.android.apps.maps"))
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(geo)
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(context, "Nema aplikacije za mape", Toast.LENGTH_LONG).show()
        }
    }
}


private fun categoryLabel(c: String) = when (c) {
    "before" -> "Pre"
    "after" -> "Posle"
    "label" -> "Nalepnica / SN"
    "workorder" -> "Radni nalog"
    else -> "Ostalo"
}

@Composable
private fun NalogPhotoSection(
    nalogId: Long,
    photos: List<NalogPhotoView>,
    repository: NalogRepository,
    canUpload: Boolean,
    isBusy: Boolean,
    currentUserId: Long,
    isAdmin: Boolean,
    onUpload: (File, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var menuOpen by remember { mutableStateOf(false) }
    var cameraTempPath by rememberSaveable { mutableStateOf<String?>(null) }
    var pending by remember { mutableStateOf<File?>(null) }       // prepared JPEG waiting for a category
    var viewing by remember { mutableStateOf<NalogPhotoView?>(null) }
    var deleting by remember { mutableStateOf<NalogPhotoView?>(null) }
    var preparing by remember { mutableStateOf(false) }

    fun handleRaw(raw: File) {
        scope.launch {
            preparing = true
            val jpeg = prepareJpeg(raw)
            runCatching { raw.delete() }
            preparing = false
            if (jpeg != null) pending = jpeg
            else Toast.makeText(context, "Fotografija se ne može obraditi.", Toast.LENGTH_LONG).show()
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val tmp = File(context.cacheDir, "pick_${System.currentTimeMillis()}")
                val ok = try {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(tmp).use { input.copyTo(it) }
                    } != null
                } catch (e: Exception) { false }
                if (ok) handleRaw(tmp)
                else Toast.makeText(context, "Fotografija se ne može učitati.", Toast.LENGTH_LONG).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = cameraTempPath
        if (success && path != null && File(path).exists()) handleRaw(File(path))
        else if (path != null) runCatching { File(path).delete() }
    }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("FOTOGRAFIJE (${photos.size})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (canUpload) {
                    Box {
                        OutlinedButton(onClick = { menuOpen = true }, enabled = !isBusy && !preparing) {
                            Text(if (preparing) "Obrada…" else "+ Dodaj")
                        }
                        androidx.compose.material3.DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text("Slikaj") },
                                onClick = {
                                    menuOpen = false
                                    try {
                                        val f = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                                        f.createNewFile()
                                        cameraTempPath = f.absolutePath
                                        cameraLauncher.launch(
                                            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f)
                                        )
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Kamera se ne može pokrenuti.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text("Izaberi iz galerije") },
                                onClick = {
                                    menuOpen = false
                                    pickLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            )
                        }
                    }
                }
            }

            if (photos.isEmpty()) {
                Text("Nema fotografija.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                photos.chunked(3).forEach { rowPhotos ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowPhotos.forEach { p ->
                            Box(Modifier.weight(1f)) {
                                NalogPhotoImage(
                                    repository, nalogId, p.id,
                                    Modifier.fillMaxWidth().aspectRatio(1f),
                                    ContentScale.Crop
                                ) { viewing = p }
                                if (p.category != "other") {
                                    Text(
                                        categoryLabel(p.category),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.align(Alignment.BottomStart).padding(4.dp)
                                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                                            .padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                        repeat(3 - rowPhotos.size) { Box(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }

    // choose the category, then upload
    pending?.let { file ->
        AlertDialog(
            onDismissRequest = { runCatching { file.delete() }; pending = null },
            title = { Text("Vrsta fotografije") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("before", "after", "label", "workorder", "other").forEach { c ->
                        OutlinedButton(
                            onClick = { pending = null; onUpload(file, c) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(categoryLabel(c)) }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { runCatching { file.delete() }; pending = null }) { Text("Otkaži") } }
        )
    }

    viewing?.let { p ->
        Dialog(onDismissRequest = { viewing = null }) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    NalogPhotoImage(repository, nalogId, p.id, Modifier.fillMaxWidth().aspectRatio(0.8f), ContentScale.Fit, null)
                    Text(
                        listOfNotNull(categoryLabel(p.category), p.uploadedByName, formatInstant(p.createdAt).ifBlank { null }).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewing = null }) { Text("Zatvori") }
                        if (isAdmin || p.uploadedById == currentUserId) {
                            OutlinedButton(onClick = { deleting = p; viewing = null }, enabled = !isBusy) { Text("Obriši") }
                        }
                    }
                }
            }
        }
    }

    deleting?.let { p ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Obrisati fotografiju?") },
            text = { Text("Ovo se ne može poništiti.") },
            confirmButton = { TextButton(onClick = { deleting = null; onDelete(p.id) }) { Text("Obriši") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Otkaži") } }
        )
    }
}

// Photos are served behind the Bearer token, so they are fetched through our own client and decoded here.
@Composable
private fun NalogPhotoImage(
    repository: NalogRepository,
    nalogId: Long,
    photoId: Long,
    modifier: Modifier,
    scale: ContentScale,
    onClick: (() -> Unit)?
) {
    var bitmap by remember(photoId) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var failed by remember(photoId) { mutableStateOf(false) }

    LaunchedEffect(photoId) {
        repository.photoBytes(nalogId, photoId).fold(
            onSuccess = { bytes ->
                // decode at reduced size: a 1920px JPEG as a full bitmap is ~15 MB of RAM, times 20 thumbnails
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                var sample = 1
                while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= 1000) sample *= 2
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
                if (bmp != null) bitmap = bmp else failed = true
            },
            onFailure = { failed = true }
        )
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        val b = bitmap
        when {
            b != null -> Image(b.asImageBitmap(), contentDescription = "Fotografija", contentScale = scale, modifier = Modifier.fillMaxSize())
            failed -> Text("!", color = MaterialTheme.colorScheme.error)
            else -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        }
    }
}
