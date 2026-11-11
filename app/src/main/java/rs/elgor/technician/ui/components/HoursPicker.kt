package rs.elgor.technician.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Work time as "sati" and "minuti" with big − / + buttons, so nobody has to type a decimal.
// The nalog still carries the server's format ("1.50"): the phone only converts between the two,
// with whole numbers (hundredths of an hour), never floating point. Prices are still the server's.
//
// Minutes move in steps of 15 (0, 15, 30, 45 = .00 .25 .50 .75 of an hour), which converts exactly.

private const val MAX_HOURS = 99

/** "1.50" / "1,5" / "" -> hundredths of an hour (150). Anything unreadable counts as 0. */
internal fun hoursToHundredths(raw: String): Int {
    val t = raw.trim().replace(',', '.')
    if (t.isEmpty()) return 0
    val whole = t.substringBefore('.').toIntOrNull() ?: return 0
    val fracText = t.substringAfter('.', "").take(2).padEnd(2, '0')
    val frac = fracText.toIntOrNull() ?: 0
    return whole * 100 + frac
}

/** Hundredths of an hour -> the server's text, e.g. 150 -> "1.50". */
internal fun hundredthsToHours(h: Int): String {
    val clamped = h.coerceIn(0, MAX_HOURS * 100 + 75)
    return "${clamped / 100}." + (clamped % 100).toString().padStart(2, '0')
}

/** Whole minutes (0..59) that a fraction of an hour in hundredths stands for, rounded to the nearest minute. */
internal fun hundredthsToMinutes(frac: Int): Int = ((frac * 60) + 50) / 100

/** One step of 15 minutes up or down inside the hour, snapping odd values (e.g. .33 typed on the web) to the grid. */
internal fun stepQuarter(frac: Int, up: Boolean): Int =
    if (up) minOf(75, (frac / 25 + 1) * 25) else maxOf(0, ((frac + 24) / 25 - 1) * 25)

@Composable
fun HoursPicker(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val total = hoursToHundredths(value)
    val hours = total / 100
    val frac = total % 100
    val minutes = hundredthsToMinutes(frac)

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Vreme rada", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Stepper(
            label = "Sati",
            shown = "$hours",
            canDown = hours > 0,
            canUp = hours < MAX_HOURS,
            onDown = { onChange(hundredthsToHours((hours - 1) * 100 + frac)) },
            onUp = { onChange(hundredthsToHours((hours + 1) * 100 + frac)) }
        )
        Stepper(
            label = "Minuti",
            shown = "$minutes",
            canDown = frac > 0,
            canUp = frac < 75,
            onDown = { onChange(hundredthsToHours(hours * 100 + stepQuarter(frac, up = false))) },
            onUp = { onChange(hundredthsToHours(hours * 100 + stepQuarter(frac, up = true))) }
        )
        Text(
            if (hours == 0 && minutes == 0) "Ukupno: 0 min" else "Ukupno: $hours h $minutes min",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun Stepper(label: String, shown: String, canDown: Boolean, canUp: Boolean, onDown: () -> Unit, onUp: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(72.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onDown, enabled = canDown, modifier = Modifier.width(64.dp).height(48.dp)) { Text("−") }
            Text(shown, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, modifier = Modifier.width(44.dp))
            OutlinedButton(onClick = onUp, enabled = canUp, modifier = Modifier.width(64.dp).height(48.dp)) { Text("+") }
        }
    }
}
