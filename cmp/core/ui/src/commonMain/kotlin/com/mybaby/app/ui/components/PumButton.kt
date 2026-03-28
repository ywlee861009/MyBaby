package com.mybaby.app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mybaby.app.ui.theme.PumTheme

/**
 * 앱 전역에서 사용할 공통 버튼입니다.
 * 나중에 디자인 가이드가 나오면 이 내부 구현(Shape, Elevation, Animation 등)만 변경합니다.
 */
@Composable
fun PumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.primary,
            contentColor = PumTheme.colors.onPrimary
        ),
        contentPadding = PaddingValues(
            horizontal = PumTheme.spacing.medium,
            vertical = PumTheme.spacing.small
        )
    ) {
        Text(
            text = text,
            style = PumTheme.typography.labelLarge
        )
    }
}
