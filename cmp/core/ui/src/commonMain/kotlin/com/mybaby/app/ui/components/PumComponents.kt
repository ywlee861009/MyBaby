package com.mybaby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme

enum class ChipVariant { PRIMARY, SECONDARY, WARNING, SUCCESS, ERROR }

/** 상태/주차 표시 칩. rounded-full 형태 */
@Composable
fun PumChip(
    text: String,
    modifier: Modifier = Modifier,
    variant: ChipVariant = ChipVariant.PRIMARY
) {
    val colors = PumTheme.colors
    val (bgColor, textColor) = when (variant) {
        ChipVariant.PRIMARY -> colors.primaryLight to colors.primary
        ChipVariant.SECONDARY -> colors.secondaryLight to colors.secondaryVariant
        ChipVariant.WARNING -> colors.warningLight to colors.warning
        ChipVariant.SUCCESS -> colors.successLight to colors.success
        ChipVariant.ERROR -> colors.errorLight to colors.error
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** 임신 주차 진행률 바 */
@Composable
fun PumProgressIndicator(
    label: String,
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors
    val progress = if (total > 0) (current.toFloat() / total).coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 라벨 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = colors.onSurfaceSubtle,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${current}주 / ${total}주",
                color = colors.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 트랙
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(colors.primaryLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
            )
        }
    }
}

/** 섹션 구분선. 높이 1dp */
@Composable
fun PumDivider(
    modifier: Modifier = Modifier,
    color: Color = PumTheme.colors.outline
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

/**
 * 프로필 아바타. 이미지가 없으면 Person 아이콘을 표시.
 * [painter] = null 이면 기본 아이콘.
 */
@Composable
fun PumAvatar(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    size: Dp = 48.dp
) {
    val colors = PumTheme.colors

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.primaryLight),
        contentAlignment = Alignment.Center
    ) {
        if (painter != null) {
            androidx.compose.foundation.Image(
                painter = painter,
                contentDescription = "프로필",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(size * 0.58f)
            )
        }
    }
}
