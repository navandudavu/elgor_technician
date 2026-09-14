package rs.elgor.technician.ui.theme

import androidx.compose.ui.graphics.Color

// ELGOR brand palette — same values as the ELGOR customer app and the
// ServiceHub web dashboard, so all three feel like one product.
// Measured directly from the real logo file: black + yellow, not guessed.
val ElgorYellow = Color(0xFFFFF100)
val ElgorYellowDark = Color(0xFFC7BC00) // darker variant for containers/pressed states
val ElgorYellowPale = Color(0xFFFFF9B3) // pale tint for containers on light backgrounds

val ElgorBlack = Color(0xFF000000)
val ElgorBlackSoft = Color(0xFF1A1A1A) // slightly softer than pure black, for large fill areas

val ElgorGrayBackground = Color(0xFFF4F6F8)
val ElgorGraySurface = Color(0xFFFFFFFF)
val ElgorGrayDarkBg = Color(0xFF14181C)
val ElgorGrayDarkSurface = Color(0xFF1E2429)

val ElgorTextPrimary = Color(0xFF1A1F24)
val ElgorTextSecondary = Color(0xFF5C6B75)
val ElgorSuccess = Color(0xFF2E8B57)
val ElgorError = Color(0xFFC0392B)
val ElgorWarning = Color(0xFFC7622D) // used for "high" priority jobs

// Status colors for job pipeline chips - not part of the original ELGOR
// palette, added for this app, chosen to sit comfortably alongside it.
val StatusPending = Color(0xFF8A8578)
val StatusAssigned = Color(0xFF3A6B5C)
val StatusInProgress = Color(0xFFC7622D)
val StatusFinished = Color(0xFF2E8B57)
val StatusCancelled = Color(0xFFA8412B)
