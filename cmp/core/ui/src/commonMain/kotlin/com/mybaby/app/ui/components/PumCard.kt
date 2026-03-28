package com.mybaby.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme

enum class RecordStatusType { SUCCESS, WARNING, ERROR }

data class ChecklistItem(
    val id: String,
    val text: String,
    val isChecked: Boolean
)

private val cardShape = RoundedCornerShape(12.dp)

private fun Modifier.pumCardSurface() = this
    .shadow(elevation = 4.dp, shape = cardShape)
    .clip(cardShape)

/** 건강 기록 카드. 날짜/수치/단위/라벨 + 선택적 상태 배지 */
@Composable
fun PumRecordCard(
    date: String,
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
    statusText: String? = null,
    statusType: RecordStatusType = RecordStatusType.SUCCESS
) {
    val colors = PumTheme.colors

    Column(
        modifier = modifier
            .pumCardSurface()
            .background(colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 헤더: 날짜 + 상태 배지
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                color = colors.onSurfaceSubtle,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            if (statusText != null) {
                val (bgColor, textColor) = when (statusType) {
                    RecordStatusType.SUCCESS -> colors.successLight to colors.success
                    RecordStatusType.WARNING -> colors.warningLight to colors.warning
                    RecordStatusType.ERROR -> colors.errorLight to colors.error
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(bgColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = statusText, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // 수치 행
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, color = colors.onSurface, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = unit, color = colors.onSurfaceSubtle, fontSize = 14.sp)
        }

        // 라벨
        Text(text = label, color = colors.onSurfaceSubtle, fontSize = 14.sp)
    }
}

/** 편지 카드. 주차 배지/날짜/미리보기/링크 */
@Composable
fun PumLetterCard(
    weekNumber: Int,
    date: String,
    preview: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors

    Column(
        modifier = modifier
            .pumCardSurface()
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 주차 배지 + 날짜
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.secondaryLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${weekNumber}주차",
                    color = colors.secondaryVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = date, color = colors.onSurfaceSubtle, fontSize = 12.sp)
        }

        // 미리보기
        Text(
            text = preview,
            color = colors.onSurface,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // 링크
        Text(
            text = "편지 보기 →",
            color = colors.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** 체크리스트 카드. 제목/완료 수/항목 목록 */
@Composable
fun PumChecklistCard(
    title: String,
    items: List<ChecklistItem>,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors

    Column(
        modifier = modifier
            .pumCardSurface()
            .clip(cardShape)
            .background(colors.surface)
    ) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.primaryLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$completedCount/$totalCount",
                    color = colors.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 항목들
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (item.isChecked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (item.isChecked) colors.primary else colors.outline,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = item.text,
                    color = if (item.isChecked) colors.onSurface else colors.onSurfaceSubtle,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** 일정 카드. 날짜 박스/제목/부제 */
@Composable
fun PumScheduleCard(
    month: String,
    day: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors

    Row(
        modifier = modifier
            .pumCardSurface()
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 날짜 박스
        Column(
            modifier = Modifier
                .size(width = 52.dp, height = 60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.primaryLight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = month, color = colors.primary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(text = day, color = colors.primary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                color = colors.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = colors.onSurfaceSubtle,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 화살표
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = colors.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}

/** 사진 카드. 썸네일 이미지 + 주차/날짜 */
@Composable
fun PumPhotoCard(
    weekNumber: Int,
    date: String,
    painter: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors

    Column(
        modifier = modifier
            .pumCardSurface()
            .clip(cardShape)
            .background(colors.surface)
            .clickable(onClick = onClick)
    ) {
        // 썸네일
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(colors.outline),
            contentAlignment = Alignment.Center
        ) {
            if (painter != null) {
                androidx.compose.foundation.Image(
                    painter = painter,
                    contentDescription = "초음파 사진",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Image,
                    contentDescription = null,
                    tint = colors.onSurfaceSubtle,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // 정보
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "${weekNumber}주차", color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = date, color = colors.onSurfaceSubtle, fontSize = 11.sp)
        }
    }
}
