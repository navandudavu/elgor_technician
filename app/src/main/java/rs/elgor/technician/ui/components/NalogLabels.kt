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
import rs.elgor.technician.ui.theme.StatusAssigned
import rs.elgor.technician.ui.theme.StatusCancelled
import rs.elgor.technician.ui.theme.StatusFinished
import rs.elgor.technician.ui.theme.StatusInProgress
import rs.elgor.technician.ui.theme.StatusPending

// Same Serbian wording as the web app (translations.js: nalogStatus_*, appliance_*, payment_*).

fun nalogStatusLabel(s: String) = when (s) {
    "DRAFT" -> "Nacrt"
    "DISPATCHED" -> "Dodeljen"
    "IN_PROGRESS" -> "U toku"
    "AWAITING_PARTS" -> "Čeka delove"
    "RECEIVED" -> "Preuzet u servis"
    "TESTING" -> "Testira se"
    "DRIVING" -> "Vozi se"
    "NEEDS_ELECTRICIAN" -> "Potreban elektroničar"
    "COMPLETED" -> "Završen"
    "CANCELLED" -> "Otkazan"
    else -> s
}

fun nalogStatusColor(s: String): Color = when (s) {
    "DRAFT" -> StatusPending
    "DISPATCHED" -> StatusAssigned
    "IN_PROGRESS" -> StatusInProgress
    "AWAITING_PARTS" -> Color(0xFF8A3F12)
    "RECEIVED" -> Color(0xFF1F3F66)
    "TESTING" -> Color(0xFF5A3E85)
    "DRIVING" -> Color(0xFF0F6B6B)
    "NEEDS_ELECTRICIAN" -> Color(0xFF9A2B2B)
    "COMPLETED" -> StatusFinished
    "CANCELLED" -> StatusCancelled
    else -> StatusPending
}

fun applianceLabel(t: String?) = when (t) {
    "AIR_CONDITIONER" -> "Klima uređaj"
    "REFRIGERATOR" -> "Frižider"
    "FREEZER" -> "Zamrzivač"
    "WASHING_MACHINE" -> "Veš mašina"
    "DRYER" -> "Mašina za sušenje"
    "DISHWASHER" -> "Mašina za sudove"
    "OVEN" -> "Rerna"
    "STOVE" -> "Šporet"
    "WATER_HEATER" -> "Grejač vode"
    "BOILER" -> "Bojler"
    "OTHER" -> "Ostalo"
    null -> ""
    else -> t
}

fun paymentLabel(p: String?) = when (p) {
    "CASH" -> "Gotovina"
    "CARD" -> "Kartica"
    "INVOICE" -> "Virman / faktura"
    null -> ""
    else -> p
}

/** "1250.5" / "1250.50" -> "1.250,50 RSD". Pure string work: money never goes through a Double. */
fun formatMoney(amount: String?): String {
    if (amount.isNullOrBlank()) return "—"
    val neg = amount.startsWith("-")
    val clean = amount.removePrefix("-")
    val parts = clean.split('.')
    val whole = parts[0].ifEmpty { "0" }.reversed().chunked(3).joinToString(".").reversed()
    val frac = (parts.getOrElse(1) { "" } + "00").take(2)
    return (if (neg) "-" else "") + "$whole,$frac RSD"
}

/** "2026-10-25T12:30:00.123Z" (UTC instant) -> "25.10.2026. 14:30" in the phone's timezone. minSdk 24: no java.time. */
fun formatInstant(iso: String?): String {
    if (iso.isNullOrBlank() || iso.length < 19) return iso ?: ""
    return try {
        val inFmt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
        inFmt.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val d = inFmt.parse(iso.substring(0, 19)) ?: return iso
        java.text.SimpleDateFormat("dd.MM.yyyy. HH:mm", java.util.Locale.US).format(d)
    } catch (e: Exception) {
        iso
    }
}

@Composable
fun NalogStatusChip(status: String, modifier: Modifier = Modifier) {
    Surface(
        color = nalogStatusColor(status).copy(alpha = 0.15f),
        contentColor = nalogStatusColor(status),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = nalogStatusLabel(status),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
