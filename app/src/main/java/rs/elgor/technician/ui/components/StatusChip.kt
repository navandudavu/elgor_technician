package rs.elgor.technician.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import rs.elgor.technician.R
import rs.elgor.technician.ui.theme.ElgorError
import rs.elgor.technician.ui.theme.ElgorWarning
import rs.elgor.technician.ui.theme.StatusAssigned
import rs.elgor.technician.ui.theme.StatusCancelled
import rs.elgor.technician.ui.theme.StatusFinished
import rs.elgor.technician.ui.theme.StatusInProgress
import rs.elgor.technician.ui.theme.StatusPending

fun statusColor(status: String): Color = when (status) {
    "pending" -> StatusPending
    "assigned" -> StatusAssigned
    "in_progress" -> StatusInProgress
    "finished" -> StatusFinished
    "cancelled" -> StatusCancelled
    else -> StatusPending
}

@Composable
fun statusLabel(status: String): String = when (status) {
    "pending" -> stringResource(R.string.status_pending)
    "assigned" -> stringResource(R.string.status_assigned)
    "in_progress" -> stringResource(R.string.status_in_progress)
    "finished" -> stringResource(R.string.status_finished)
    "cancelled" -> stringResource(R.string.status_cancelled)
    else -> status
}

fun priorityColor(priority: String): Color = when (priority) {
    "urgent" -> ElgorError
    "high" -> ElgorWarning
    else -> Color.Gray
}

@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    Surface(
        color = statusColor(status).copy(alpha = 0.15f),
        contentColor = statusColor(status),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = statusLabel(status),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityChip(priority: String, modifier: Modifier = Modifier) {
    if (priority == "low" || priority == "normal") return // only worth calling out when it matters
    val label = when (priority) {
        "urgent" -> stringResource(R.string.priority_urgent)
        "high" -> stringResource(R.string.priority_high)
        "normal" -> stringResource(R.string.priority_normal)
        "low" -> stringResource(R.string.priority_low)
        else -> priority.replaceFirstChar { it.uppercase() }
    }
    Surface(
        color = priorityColor(priority).copy(alpha = 0.15f),
        contentColor = priorityColor(priority),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
