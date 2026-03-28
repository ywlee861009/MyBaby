package com.mybaby.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 디자인 시스템의 '뼈대'가 되는 색상 체계입니다.
 */
@Immutable
data class PumColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val primaryLight: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryLight: Color,
    val secondaryVariant: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceSubtle: Color,
    val outline: Color,
    val error: Color,
    val onError: Color,
    val isLight: Boolean
)

/**
 * 디자인 시스템의 '서체' 체계입니다.
 */
@Immutable
data class PumTypography(
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelLarge: TextStyle,
    val labelSmall: TextStyle
)

/**
 * 디자인 시스템의 '간격(Spacing)' 체계입니다.
 */
@Immutable
data class PumSpacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp
)

val LocalPumColors = staticCompositionLocalOf<PumColors> {
    error("No PumColors provided! Make sure to wrap your content in MyBabyTheme.")
}

val LocalPumTypography = staticCompositionLocalOf<PumTypography> {
    error("No PumTypography provided! Make sure to wrap your content in MyBabyTheme.")
}

val LocalPumSpacing = staticCompositionLocalOf<PumSpacing> {
    PumSpacing()
}

object PumTheme {
    val colors: PumColors
        @androidx.compose.runtime.Composable
        get() = LocalPumColors.current

    val typography: PumTypography
        @androidx.compose.runtime.Composable
        get() = LocalPumTypography.current

    val spacing: PumSpacing
        @androidx.compose.runtime.Composable
        get() = LocalPumSpacing.current
}
