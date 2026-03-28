package com.mybaby.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mybaby.app.ui.theme.PumTheme

/** 주요 액션 버튼. fill=primary, radius=8, height=52 */
@Composable
fun PumPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.primary,
            contentColor = PumTheme.colors.onPrimary,
            disabledContainerColor = PumTheme.colors.outline,
            disabledContentColor = PumTheme.colors.onSurfaceSubtle
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** 보조 버튼. outlined 형태, stroke=primary 1.5, height=52 */
@Composable
fun PumSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PumTheme.colors.primary,
            disabledContentColor = PumTheme.colors.onSurfaceSubtle
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (enabled) PumTheme.colors.primary else PumTheme.colors.outline
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** 텍스트 버튼. 배경 없음, text=primary, height=44 */
@Composable
fun PumTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = PumTheme.colors.primary
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** 아이콘 버튼. circle 48dp, fill=surface, shadow */
@Composable
fun PumIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = PumTheme.colors.primary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .shadow(elevation = 4.dp, shape = CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.surface,
            contentColor = tint
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tint
        )
    }
}

/** 플로팅 액션 버튼. circle 56dp, fill=primary, shadow */
@Composable
fun PumFab(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(56.dp)
            .shadow(elevation = 6.dp, shape = CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.primary,
            contentColor = PumTheme.colors.onPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

/** 파괴적 액션 버튼. fill=error, height=52 (삭제 등 위험 액션) */
@Composable
fun PumDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PumTheme.colors.error,
            contentColor = Color.White,
            disabledContainerColor = PumTheme.colors.outline,
            disabledContentColor = PumTheme.colors.onSurfaceSubtle
        ),
        contentPadding = PaddingValues(horizontal = PumTheme.spacing.medium)
    ) {
        Text(text = text, style = PumTheme.typography.labelLarge)
    }
}

/** @deprecated PumPrimaryButton을 사용하세요 */
@Composable
fun PumButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = PumPrimaryButton(text = text, onClick = onClick, modifier = modifier, enabled = enabled)
