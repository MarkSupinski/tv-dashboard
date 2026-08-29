package com.microserve.batterytv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark, TV-friendly palette.
val BatteryGreen = Color(0xFF3DDC84)
val BatteryTeal = Color(0xFF00C2FF)
val BatteryAmber = Color(0xFFFFB74D)
val BatteryRed = Color(0xFFFF5252)
val BatteryPurple = Color(0xFFB388FF)

val Background = Color(0xFF0E1523)
val Surface = Color(0xFF17202F)
val SurfaceFocus = Color(0xFF2E4060)
val TextPrimary = Color(0xFFE8EEF7)
val TextSecondary = Color(0xFF9AA7BC)
val GridLine = Color(0xFF2A3750)

private val DarkColors = darkColorScheme(
    primary = BatteryGreen,
    secondary = BatteryTeal,
    tertiary = BatteryPurple,
    background = Background,
    surface = Surface,
    onPrimary = Color(0xFF08120C),
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = GridLine,
)

@Composable
fun BatteryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content,
    )
}
