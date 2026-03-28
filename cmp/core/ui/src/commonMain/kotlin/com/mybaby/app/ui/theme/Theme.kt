package com.mybaby.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Primary - 코랄핑크 계열
private val Pink40 = Color(0xFFFF8FAB)
private val PinkVariant = Color(0xFFE5607A)
private val PinkLight = Color(0xFFFFF0F3)
private val Pink80 = Color(0xFFF4B8C4)
private val Pink90 = Color(0xFFFDE8EC)

// Secondary - 라벤더 계열
private val Lavender40 = Color(0xFF9B72C8)
private val LavenderMid = Color(0xFFC9A8E0)
private val LavenderLight = Color(0xFFF3EDFB)
private val Lavender80 = Color(0xFFC9BFE6)
private val Lavender90 = Color(0xFFEDE8F5)

// Neutral
private val OnSurface = Color(0xFF2D2020)
private val OnSurfaceSubtle = Color(0xFF9E8A8A)
private val Outline = Color(0xFFE8DDD5)
private val WarmWhite = Color(0xFFFFFAF5)

// Semantic
private val Error = Color(0xFFE53935)
private val ErrorLight = Color(0xFFFDECEA)
private val Success = Color(0xFF5CB85C)
private val SuccessLight = Color(0xFFEFF8EF)
private val Warning = Color(0xFFF0A500)
private val WarningLight = Color(0xFFFFF8E6)
private val Info = Color(0xFF4A90D9)
private val InfoLight = Color(0xFFEBF4FC)

private val LightPumColors = PumColors(
    primary = Pink40,
    onPrimary = Color.White,
    primaryContainer = Pink90,
    onPrimaryContainer = Color(0xFF3E0A16),
    primaryLight = PinkLight,
    primaryVariant = PinkVariant,
    secondary = LavenderMid,
    onSecondary = Color.White,
    secondaryLight = LavenderLight,
    secondaryVariant = Lavender40,
    background = WarmWhite,
    onBackground = OnSurface,
    surface = Color.White,
    onSurface = OnSurface,
    onSurfaceSubtle = OnSurfaceSubtle,
    outline = Outline,
    error = Error,
    onError = Color.White,
    errorLight = ErrorLight,
    success = Success,
    successLight = SuccessLight,
    warning = Warning,
    warningLight = WarningLight,
    info = Info,
    infoLight = InfoLight,
    isLight = true
)

private val DarkPumColors = PumColors(
    primary = Pink80,
    onPrimary = Color(0xFF3E0A16),
    primaryContainer = Color(0xFF7A3045),
    onPrimaryContainer = Pink90,
    primaryLight = Color(0xFF3A1A22),
    primaryVariant = Color(0xFFFF6B8A),
    secondary = Lavender80,
    onSecondary = Color(0xFF1D1145),
    secondaryLight = Color(0xFF2A2040),
    secondaryVariant = Lavender80,
    background = Color(0xFF1C1B1E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF252328),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceSubtle = Color(0xFF9E8A8A),
    outline = Color(0xFF4A3F3F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorLight = Color(0xFF3A1A18),
    success = Color(0xFF81C784),
    successLight = Color(0xFF1A2E1A),
    warning = Color(0xFFFFCC02),
    warningLight = Color(0xFF2E2500),
    info = Color(0xFF64B5F6),
    infoLight = Color(0xFF0D1F2E),
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
