package rs.elgor.technician.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Same primary=yellow / onPrimary=black rule as the ELGOR customer app and
// the ServiceHub web dashboard - text/icons on yellow are always black,
// never white, since white-on-yellow is close to unreadable. Keep this
// rule if you add new yellow-background UI elements later.
private val LightColors = lightColorScheme(
    primary = ElgorYellow,
    onPrimary = ElgorBlack,
    primaryContainer = ElgorYellowPale,
    onPrimaryContainer = ElgorBlack,
    secondary = ElgorBlack,
    onSecondary = ElgorYellow,
    secondaryContainer = ElgorBlackSoft,
    onSecondaryContainer = ElgorYellow,
    background = ElgorGrayBackground,
    onBackground = ElgorTextPrimary,
    surface = ElgorGraySurface,
    onSurface = ElgorTextPrimary,
    surfaceVariant = ElgorGrayBackground,
    onSurfaceVariant = ElgorTextSecondary,
    error = ElgorError
)

private val DarkColors = darkColorScheme(
    primary = ElgorYellow,
    onPrimary = ElgorBlack,
    primaryContainer = ElgorYellowDark,
    onPrimaryContainer = ElgorBlack,
    secondary = ElgorYellow,
    onSecondary = ElgorBlack,
    secondaryContainer = ElgorBlackSoft,
    onSecondaryContainer = ElgorYellow,
    background = ElgorGrayDarkBg,
    onBackground = Color.White,
    surface = ElgorGrayDarkSurface,
    onSurface = Color.White,
    error = ElgorError
)

@Composable
fun ElgorTechnicianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
