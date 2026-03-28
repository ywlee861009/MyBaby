package com.mybaby.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mybaby.app.ui.theme.PumTheme

/**
 * 앱 상단 바.
 * - navigationIcon: null이면 왼쪽 영역 빈 공간으로 유지 (title centering 보장)
 * - actions: 오른쪽 아이콘 버튼 영역
 */
@Composable
fun PumTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val colors = PumTheme.colors
    val borderColor = colors.outline

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawBehind {
                val y = size.height
                drawLine(
                    color = borderColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽: 네비게이션 아이콘 (고정 너비로 title centering 보장)
        Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.Center) {
            navigationIcon?.invoke()
        }

        // 중앙: 타이틀
        Text(
            text = title,
            style = PumTheme.typography.headlineSmall,
            color = colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        // 오른쪽: 액션 버튼들
        Row(
            modifier = Modifier.width(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
    }
}

/** 뒤로가기 아이콘 버튼 */
@Composable
fun PumBackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Rounded.ArrowBackIosNew,
            contentDescription = "뒤로가기",
            tint = PumTheme.colors.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

/** 닫기(X) 아이콘 버튼 */
@Composable
fun PumCloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "닫기",
            tint = PumTheme.colors.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

/** 커스텀 아이콘 액션 버튼 */
@Composable
fun PumActionButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = PumTheme.colors.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}
