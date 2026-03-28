package com.mybaby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme

/** 앱 전역 스낵바 호스트. Scaffold의 snackbarHost에 사용 */
@Composable
fun PumSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = Color(0xFF2D2020),
            contentColor = Color.White,
            actionColor = PumTheme.colors.primary,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * 확인/취소 다이얼로그.
 * [isDestructive] = true이면 확인 버튼이 error 색상으로 표시.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PumDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    cancelText: String = "취소",
    confirmText: String = "확인",
    isDestructive: Boolean = false
) {
    val colors = PumTheme.colors

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    color = colors.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = message,
                    color = colors.onSurfaceSubtle,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PumSecondaryButton(
                    text = cancelText,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                if (isDestructive) {
                    PumDestructiveButton(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    PumPrimaryButton(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/** 빈 상태 화면. 아이콘 원 + 제목 + 설명 + 선택적 버튼 */
@Composable
fun PumEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Inbox,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    val colors = PumTheme.colors

    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.primaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = title,
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = description,
            color = colors.onSurfaceSubtle,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )

        if (buttonText != null && onButtonClick != null) {
            PumPrimaryButton(text = buttonText, onClick = onButtonClick)
        }
    }
}
