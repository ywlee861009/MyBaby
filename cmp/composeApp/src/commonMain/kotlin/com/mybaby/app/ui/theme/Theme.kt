package com.mybaby.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary - 부드러운 코랄/핑크 계열
val Pink40 = Color(0xFFE8879A)
val Pink80 = Color(0xFFF4B8C4)
val Pink90 = Color(0xFFFDE8EC)

// Secondary - 따뜻한 라벤더 계열
val Lavender40 = Color(0xFF9B8EC2)
val Lavender80 = Color(0xFFC9BFE6)
val Lavender90 = Color(0xFFEDE8F5)

// Neutral
val Warm50 = Color(0xFF5D5148)
val Warm80 = Color(0xFFC4B8AE)
val Warm95 = Color(0xFFFFF8F3)
val WarmWhite = Color(0xFFFFFAF5)

// Semantic
val Success = Color(0xFF6BBF7A)
val Warning = Color(0xFFF2C94C)
val Error = Color(0xFFE57373)
val Info = Color(0xFF64B5F6)

private val LightColorScheme = lightColorScheme(
    primary = Pink40,
    onPrimary = Color.White,
    primaryContainer = Pink90,
    onPrimaryContainer = Color(0xFF3E0A16),
    secondary = Lavender40,
    onSecondary = Color.White,
    secondaryContainer = Lavender90,
    onSecondaryContainer = Color(0xFF1D1145),
    background = WarmWhite,
    onBackground = Warm50,
    surface = Color.White,
    onSurface = Warm50,
    surfaceVariant = Warm95,
    onSurfaceVariant = Warm80,
    outline = Warm80,
    error = Error,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Pink80,
    onPrimary = Color(0xFF3E0A16),
    primaryContainer = Color(0xFF7A3045),
    onPrimaryContainer = Pink90,
    secondary = Lavender80,
    onSecondary = Color(0xFF1D1145),
    secondaryContainer = Color(0xFF4A3D6E),
    onSecondaryContainer = Lavender90,
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF252328),
    onSurface = Color(0xFFE6E1E5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun MyBabyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyBabyTypography,
        content = content
    )
}
