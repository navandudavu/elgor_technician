package rs.elgor.technician.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import rs.elgor.technician.R
import rs.elgor.technician.data.JobDetailViewModel
import rs.elgor.technician.data.JobRepository
import rs.elgor.technician.model.Job
import rs.elgor.technician.ui.components.JobPhoto
import rs.elgor.technician.ui.components.PriorityChip
import rs.elgor.technician.ui.components.StatusChip
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val STATUS_FLOW = listOf(
    "pending",
    "assigned",
    "in_progress",
    "finished"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    viewModel: JobDetailViewModel,
    repository: JobRepository,
    onBack: () -> Unit
) {
    val job by viewModel.job.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        job?.title
                            ?: stringResource(R.string.job_title_placeholder),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading && job == null -> {
                    Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                job == null -> {
                    Box(
                        Modifier.fillMaxSize(),
                        Alignment.Center
                    ) {
                        Text(
                            error
                                ?: stringResource(R.string.error_load_job),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    JobDetailContent(
                        job = job!!,
                        isBusy = isBusy,
                        error = error,
                        repository = repository,
                        onStatusChange = {
                            viewModel.updateStatus(it)
                        },
                        onHoursChange = {
                            viewModel.updateHours(it)
                        },
                        onScheduledAtChange = {
                            viewModel.updateScheduledAt(it)
                        },
                        onAddNote = {
                            viewModel.addNote(it)
                        },
                        onUploadPhoto = { file, mime, category ->
                            viewModel.uploadPhoto(
                                file,
                                mime,
                                category
                            )
                        },
                        onDeletePhoto = {
                            viewModel.deletePhoto(it)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JobDetailContent(
    job: Job,
    isBusy: Boolean,
    error: String?,
    repository: JobRepository,
    onStatusChange: (String) -> Unit,
    onHoursChange: (Double?) -> Unit,
    onScheduledAtChange: (String?) -> Unit,
    onAddNote: (String) -> Unit,
    onUploadPhoto: (File, String, String) -> Unit,
    onDeletePhoto: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var noteText by remember {
        mutableStateOf("")
    }

    var showCategoryPicker by remember {
        mutableStateOf(false)
    }

    var pendingPhotoFile by remember {
        mutableStateOf<File?>(null)
    }

    var pendingPhotoMime by remember {
        mutableStateOf<String?>(null)
    }

    var showDurationPicker by remember {
        mutableStateOf(false)
    }

    var showPhotoOptions by remember {
        mutableStateOf(false)
    }

    var cameraTempPath by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val (file, mime) = copyUriToTempFile(
                    context,
                    uri
                )

                if (file != null) {
                    pendingPhotoFile = file
                    pendingPhotoMime = mime ?: "image/jpeg"
                    showCategoryPicker = true
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_copy_photo),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraTempPath?.let { path ->
                val file = File(path)

                if (file.exists()) {
                    pendingPhotoFile = file
                    pendingPhotoMime = "image/jpeg"
                    showCategoryPicker = true
                } else {
                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.error_camera_file_missing
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            cameraTempPath?.let { path ->
                runCatching {
                    File(path).delete()
                }
            }
        }
    }

    val currentIndex = STATUS_FLOW.indexOf(job.status)

    val nextStatus =
        if (
            job.status != "finished" &&
            job.status != "cancelled" &&
            currentIndex >= 0 &&
            currentIndex < STATUS_FLOW.size - 1
        ) {
            STATUS_FLOW[currentIndex + 1]
        } else {
            null
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(job.status)
                PriorityChip(job.priority)
            }

            if (!job.address.isNullOrBlank()) {
                IconButton(
                    onClick = {
                        val gmmIntentUri = Uri.parse(
                            "geo:0,0?q=${Uri.encode(job.address)}"
                        )

                        val mapIntent = Intent(
                            Intent.ACTION_VIEW,
                            gmmIntentUri
                        )

                        mapIntent.setPackage(
                            "com.google.android.apps.maps"
                        )

                        context.startActivity(mapIntent)
                    }
                ) {
                    Icon(
                        Icons.Default.Navigation,
                        contentDescription = stringResource(
                            R.string.navigate
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Surface(
            onClick = {
                val calendar = Calendar.getInstance()
                job.scheduledAt?.let { isoDate ->
                    runCatching {
                        val sdf = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.US
                        )
                        sdf.parse(isoDate)?.let {
                            calendar.time = it
                        }
                    }
                }

                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val selected = Calendar.getInstance()
                                selected.set(year, month, day, hour, minute, 0)
                                val sdf = SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss",
                                    Locale.US
                                )
                                onScheduledAtChange(
                                    sdf.format(selected.time)
                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Column {
                    Text(
                        stringResource(R.string.scheduled_at),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = job.scheduledAt ?: stringResource(
                            R.string.not_scheduled
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            job.customerName?.let {
                Text(
                    stringResource(
                        R.string.customer_label,
                        it
                    ) + (
                            job.customerPhone?.let { phone ->
                                " · $phone"
                            } ?: ""
                            ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            job.address?.let {
                Text(
                    stringResource(
                        R.string.address_label,
                        it
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (job.applianceType != null) {
                Text(
                    text = stringResource(R.string.appliance_details),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = job.applianceType,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = job.applianceBrand ?: "-",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = job.applianceModel ?: "-",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            if (!job.applianceSerial.isNullOrBlank()) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.serial_number),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = job.applianceSerial,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            job.description?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (nextStatus != null) {
            val statusLabel = when (nextStatus) {
                "pending" ->
                    stringResource(R.string.status_pending)

                "assigned" ->
                    stringResource(R.string.status_assigned)

                "in_progress" ->
                    stringResource(R.string.status_in_progress)

                "finished" ->
                    stringResource(R.string.status_finished)

                else ->
                    nextStatus
            }

            Button(
                onClick = {
                    onStatusChange(nextStatus)
                },
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(
                        R.string.move_to_status,
                        statusLabel
                    )
                )
            }
        }

        Column {
            Text(
                stringResource(R.string.hours_logged),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                onClick = {
                    showDurationPicker = true
                },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth()
            ) {
                val totalMinutes =
                    ((job.hoursLogged ?: 0.0) * 60).toInt()

                val displayHours = totalMinutes / 60
                val displayMinutes = totalMinutes % 60

                Text(
                    text = if (totalMinutes > 0) {
                        stringResource(
                            R.string.hours_minutes_format,
                            displayHours,
                            displayMinutes
                        )
                    } else {
                        stringResource(
                            R.string.zero_hours_zero_minutes
                        )
                    },
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.photos),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Box {
                    OutlinedButton(
                        onClick = {
                            showPhotoOptions = true
                        },
                        enabled = !isBusy
                    ) {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            Modifier.padding(start = 4.dp)
                        )

                        Text(
                            stringResource(R.string.add)
                        )
                    }

                    DropdownMenu(
                        expanded = showPhotoOptions,
                        onDismissRequest = {
                            showPhotoOptions = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(
                                        R.string.take_photo
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AddAPhoto,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showPhotoOptions = false

                                try {
                                    val tempFile = File(
                                        context.cacheDir,
                                        "camera_" +
                                                "${System.currentTimeMillis()}.jpg"
                                    )

                                    if (tempFile.exists()) {
                                        tempFile.delete()
                                    }

                                    tempFile.createNewFile()

                                    val authority =
                                        "${context.packageName}.fileprovider"

                                    val uri =
                                        FileProvider.getUriForFile(
                                            context,
                                            authority,
                                            tempFile
                                        )

                                    cameraTempPath =
                                        tempFile.absolutePath

                                    cameraLauncher.launch(uri)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        context.getString(
                                            R.string.error_start_camera
                                        ),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(
                                        R.string.choose_photo
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showPhotoOptions = false

                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts
                                            .PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        )
                    }
                }
            }

            if (job.photos.isEmpty()) {
                Text(
                    stringResource(R.string.no_photos),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(
                            if (job.photos.size > 3) {
                                220.dp
                            } else {
                                110.dp
                            }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(
                        job.photos,
                        key = { it.id }
                    ) { photo ->
                        Box {
                            JobPhoto(
                                filename = photo.filename,
                                repository = repository,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(
                                        RoundedCornerShape(6.dp)
                                    )
                            )

                            val categoryLabel = when (photo.category) {
                                "before" ->
                                    stringResource(
                                        R.string.photo_category_before
                                    )

                                "after" ->
                                    stringResource(
                                        R.string.photo_category_after
                                    )

                                "label" ->
                                    stringResource(
                                        R.string.photo_category_label
                                    )

                                else ->
                                    stringResource(
                                        R.string.photo_category_other
                                    )
                            }

                            Surface(
                                color = MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                                    .copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    categoryLabel,
                                    style = MaterialTheme
                                        .typography
                                        .labelSmall,
                                    modifier = Modifier.padding(
                                        horizontal = 4.dp,
                                        vertical = 2.dp
                                    )
                                )
                            }

                            IconButton(
                                onClick = {
                                    onDeletePhoto(photo.id)
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(
                                        R.string.delete_photo
                                    ),
                                    tint = MaterialTheme
                                        .colorScheme
                                        .onError,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Column {
            Text(
                stringResource(R.string.notes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(
                    top = 6.dp,
                    bottom = 8.dp
                )
            ) {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        noteText = it
                    },
                    placeholder = {
                        Text(
                            stringResource(
                                R.string.add_note_placeholder
                            )
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        onAddNote(noteText)
                        noteText = ""
                    },
                    enabled = !isBusy && noteText.isNotBlank()
                ) {
                    Text(
                        stringResource(R.string.add)
                    )
                }
            }

            if (job.notes.isEmpty()) {
                Text(
                    stringResource(R.string.no_notes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    job.notes.forEach { note ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text(
                                    note.note,
                                    style = MaterialTheme
                                        .typography
                                        .bodyMedium
                                )

                                Text(
                                    stringResource(
                                        R.string.note_author_date,
                                        note.authorName ?: "",
                                        note.createdAt
                                    ),
                                    style = MaterialTheme
                                        .typography
                                        .labelMedium,
                                    color = MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,
                                    modifier = Modifier.padding(
                                        top = 4.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }

    if (showDurationPicker) {
        val currentMinutes =
            ((job.hoursLogged ?: 0.0) * 60).toInt()

        DurationPickerDialog(
            initialHours = currentMinutes / 60,
            initialMinutes = currentMinutes % 60,
            onDismiss = {
                showDurationPicker = false
            },
            onConfirm = { h, m ->
                showDurationPicker = false

                val totalHours =
                    h + (m / 60.0)

                onHoursChange(totalHours)
            }
        )
    }

    if (showCategoryPicker && pendingPhotoFile != null) {
        CategoryPickerDialog(
            onDismiss = {
                showCategoryPicker = false

                pendingPhotoFile?.let {
                    runCatching {
                        it.delete()
                    }
                }

                pendingPhotoFile = null
                pendingPhotoMime = null
            },
            onConfirm = { category ->
                val file = pendingPhotoFile
                val mime =
                    pendingPhotoMime ?: "image/jpeg"

                if (file != null) {
                    showCategoryPicker = false
                    pendingPhotoFile = null
                    pendingPhotoMime = null

                    onUploadPhoto(
                        file,
                        mime,
                        category
                    )
                }
            }
        )
    }
}

@Composable
private fun DurationPickerDialog(
    initialHours: Int,
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var h by remember {
        mutableIntStateOf(initialHours)
    }

    var m by remember {
        mutableIntStateOf(initialMinutes)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(
                        R.string.duration_picker_title
                    ),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    Modifier.height(24.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NumberPickerField(
                        value = h,
                        label = stringResource(R.string.hours),
                        onValueChange = {
                            h = it
                        },
                        range = 0..99
                    )

                    Text(
                        ":",
                        style = MaterialTheme
                            .typography
                            .headlineLarge
                    )

                    NumberPickerField(
                        value = m,
                        label = stringResource(R.string.minutes),
                        onValueChange = {
                            m = it
                        },
                        range = 0..59
                    )
                }

                Spacer(
                    Modifier.height(24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            stringResource(R.string.cancel)
                        )
                    }

                    TextButton(
                        onClick = {
                            onConfirm(h, m)
                        }
                    ) {
                        Text(
                            stringResource(R.string.save)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.photo_category_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CategoryItem(
                            label = stringResource(R.string.photo_category_before),
                            icon = Icons.Default.History,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onConfirm("before") }
                        )
                        CategoryItem(
                            label = stringResource(R.string.photo_category_after),
                            icon = Icons.Default.Check,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onConfirm("after") }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CategoryItem(
                            label = stringResource(R.string.photo_category_label),
                            icon = Icons.Default.Label,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { onConfirm("label") }
                        )
                        CategoryItem(
                            label = stringResource(R.string.photo_category_other),
                            icon = Icons.Default.Image,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f),
                            onClick = { onConfirm("other") }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = color,
        modifier = modifier.aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NumberPickerField(
    value: Int,
    label: String,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                if (value < range.last) {
                    onValueChange(value + 1)
                }
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.displayMedium
        )

        IconButton(
            onClick = {
                if (value > range.first) {
                    onValueChange(value - 1)
                }
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

private suspend fun copyUriToTempFile(
    context: Context,
    uri: Uri
): Pair<File?, String?> {
    return try {
        val mimeType =
            context.contentResolver.getType(uri)

        val extension = when (mimeType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/heic",
            "image/heif" -> "heic"
            else -> "jpg"
        }

        val tempFile = File(
            context.cacheDir,
            "upload_${System.currentTimeMillis()}.$extension"
        )

        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            ?: return Pair(null, null)

        Pair(
            tempFile,
            mimeType ?: "image/jpeg"
        )
    } catch (_: Exception) {
        Pair(null, null)
    }
}
