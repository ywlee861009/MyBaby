package com.mybaby.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 디자인 시스템의 '뼈대'가 되는 색상 체계입니다.
 * 나중에 디자인 가이드가 확정되면 이 항목들을 실제 브랜드 컬러로 채웁니다.
 */
@Immutable
data class PumColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
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
 * 하드코딩된 숫자(8.dp, 16.dp) 대신 역할을 부여합니다.
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

// CompositionLocal을 통해 앱 전체에서 접근 가능하게 설정합니다.
val LocalPumColors = staticCompositionLocalOf<PumColors> {
    error("No PumColors provided! Make sure to wrap your content in MyBabyTheme.")
}

val LocalPumTypography = staticCompositionLocalOf<PumTypography> {
    error("No PumTypography provided! Make sure to wrap your content in MyBabyTheme.")
}

val LocalPumSpacing = staticCompositionLocalOf<PumSpacing> {
    PumSpacing() // 기본값 제공
}

/**
 * UI 코드에서 PumTheme.colors.primary 처럼 접근하기 위한 오브젝트입니다.
 */
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
