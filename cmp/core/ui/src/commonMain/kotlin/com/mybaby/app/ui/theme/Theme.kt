package com.mybaby.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Primary - 부드러운 코랄/핑크 계열
private val Pink40 = Color(0xFFE8879A)
private val Pink80 = Color(0xFFF4B8C4)
private val Pink90 = Color(0xFFFDE8EC)

// Secondary - 따뜻한 라벤더 계열
private val Lavender40 = Color(0xFF9B8EC2)
private val Lavender80 = Color(0xFFC9BFE6)
private val Lavender90 = Color(0xFFEDE8F5)

// Neutral
private val Warm50 = Color(0xFF5D5148)
private val Warm80 = Color(0xFFC4B8AE)
private val Warm95 = Color(0xFFFFF8F3)
private val WarmWhite = Color(0xFFFFFAF5)

// Semantic
private val Success = Color(0xFF6BBF7A)
private val Warning = Color(0xFFF2C94C)
private val Error = Color(0xFFE57373)
private val Info = Color(0xFF64B5F6)

private val LightPumColors = PumColors(
    primary = Pink40,
    onPrimary = Color.White,
    primaryContainer = Pink90,
    onPrimaryContainer = Color(0xFF3E0A16),
    secondary = Lavender40,
    onSecondary = Color.White,
    background = WarmWhite,
    onBackground = Warm50,
    surface = Color.White,
    onSurface = Warm50,
    error = Error,
    onError = Color.White,
    isLight = true
)

private val DarkPumColors = PumColors(
    primary = Pink80,
    onPrimary = Color(0xFF3E0A16),
    primaryContainer = Color(0xFF7A3045),
    onPrimaryContainer = Pink90,
    secondary = Lavender80,
    onSecondary = Color(0xFF1D1145),
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF252328),
    onSurface = Color(0xFFE6E1E5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    isLight = false
)

private val LightColorScheme = lightColorScheme(
    primary = LightPumColors.primary,
    onPrimary = LightPumColors.onPrimary,
    primaryContainer = LightPumColors.primaryContainer,
    onPrimaryContainer = LightPumColors.onPrimaryContainer,
    secondary = LightPumColors.secondary,
    onSecondary = LightPumColors.onSecondary,
    background = LightPumColors.background,
    onBackground = LightPumColors.onBackground,
    surface = LightPumColors.surface,
    onSurface = LightPumColors.onSurface,
    error = LightPumColors.error,
    onError = LightPumColors.onError,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPumColors.primary,
    onPrimary = DarkPumColors.onPrimary,
    primaryContainer = DarkPumColors.primaryContainer,
    onPrimaryContainer = DarkPumColors.onPrimaryContainer,
    secondary = DarkPumColors.secondary,
    onSecondary = DarkPumColors.onSecondary,
    background = DarkPumColors.background,
    onBackground = DarkPumColors.onBackground,
    surface = DarkPumColors.surface,
    onSurface = DarkPumColors.onSurface,
    error = DarkPumColors.error,
    onError = DarkPumColors.onError,
)

@Composable
fun MyBabyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val pumColors = if (darkTheme) DarkPumColors else LightPumColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // PumDesignSystem의 토큰들을 주입합니다.
    CompositionLocalProvider(
        LocalPumColors provides pumColors,
        LocalPumTypography provides PumTypography(
            headlineLarge = MyBabyTypography.headlineLarge,
            headlineMedium = MyBabyTypography.headlineMedium,
            headlineSmall = MyBabyTypography.headlineSmall,
            bodyLarge = MyBabyTypography.bodyLarge,
            bodyMedium = MyBabyTypography.bodyMedium,
            labelLarge = MyBabyTypography.labelLarge,
            labelSmall = MyBabyTypography.labelSmall
        ),
        LocalPumSpacing provides PumSpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MyBabyTypography,
            content = content
        )
    }
}
