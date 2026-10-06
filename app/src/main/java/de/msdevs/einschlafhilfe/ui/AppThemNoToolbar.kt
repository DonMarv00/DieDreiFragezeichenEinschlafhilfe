package de.msdevs.einschlafhilfe.ui


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val Background = Color(0xFF000000)
private val Surface    = Color(0xFF000000)

private val Primary    = Color(0xFFD50000)
private val OnDark     = Color(0xFFFFFFFF)

private val AppColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnDark,
    primaryContainer = Primary,
    onPrimaryContainer = OnDark,
    inversePrimary = Primary,

    secondary = Primary,
    onSecondary = OnDark,
    secondaryContainer = Primary,
    onSecondaryContainer = OnDark,

    tertiary = Primary,
    onTertiary = OnDark,
    tertiaryContainer = Primary,
    onTertiaryContainer = OnDark,

    background = Background,
    onBackground = OnDark,

    surface = Surface,
    onSurface = OnDark,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFCCCCCC),
    surfaceTint = Primary,

    surfaceBright = Color(0xFF1E1E1E),
    surfaceDim = Color(0xFF000000),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0A0A0A),
    surfaceContainer = Color(0xFF0D0D0D),
    surfaceContainerHigh = Color(0xFF141414),
    surfaceContainerHighest = Color(0xFF1A1A1A),

    inverseSurface = OnDark,
    inverseOnSurface = Color(0xFF121212),

    error = Color(0xFFFF5252),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFFB00020),
    onErrorContainer = OnDark,

    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A),
    scrim = Color(0xFF000000)

)

@Composable
fun AppThemeNoToolbar(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}