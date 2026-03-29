package com.mybaby.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.ui.components.*
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToRecord: () -> Unit,
    onNavigateToLetterWrite: () -> Unit,
    onNavigateToScheduleAdd: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToSchedule: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                HomeUiEvent.NavigateToLetterWrite -> onNavigateToLetterWrite()
                HomeUiEvent.NavigateToScheduleAdd -> onNavigateToScheduleAdd()
                HomeUiEvent.NavigateToMore -> onNavigateToMore()
                HomeUiEvent.NavigateToHealthRecord -> onNavigateToRecord()
                HomeUiEvent.NavigateToSchedule -> onNavigateToSchedule()
            }
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PumTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PumTheme.colors.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PumTheme.colors.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            HomeSectionA_Header(state = state)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            HomeSectionB_Progress(state = state)
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            HomeSectionC_QuickActions(
                onWeightRecord = { viewModel.handleIntent(HomeIntent.OnWeightRecordClick) },
                onLetterWrite = { viewModel.handleIntent(HomeIntent.OnLetterWriteClick) },
                onScheduleAdd = { viewModel.handleIntent(HomeIntent.OnScheduleAddClick) },
                onPhotoAdd = {}
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            HomeSectionD_Checklist(
                state = state,
                onToggle = { id -> viewModel.handleIntent(HomeIntent.ToggleChecklistItem(id)) },
                onMoreClick = { viewModel.handleIntent(HomeIntent.OnMoreChecklistClick) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            HomeSectionE_Schedules(
                state = state,
                onMoreClick = { viewModel.handleIntent(HomeIntent.OnMoreScheduleClick) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            HomeSectionF_Records(
                state = state,
                onMoreClick = { viewModel.handleIntent(HomeIntent.OnMoreRecordClick) }
            )
        }
    }
}

// ── A. 인사 헤더 ─────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionA_Header(state: HomeState) {
    val colors = PumTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "안녕하세요, ${state.nickname}님",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.todayLabel,
                fontSize = 13.sp,
                color = colors.onSurfaceSubtle
            )
        }
        PumAvatar(size = 44.dp)
    }
}

// ── B. 임신 진행률 ───────────────────────────────────────────────────────────

@Composable
private fun HomeSectionB_Progress(state: HomeState) {
    val colors = PumTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PumChip(
                text = "${state.currentWeek}주 ${state.currentDay}일",
                variant = ChipVariant.PRIMARY
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "출산까지",
                    fontSize = 11.sp,
                    color = colors.onSurfaceSubtle
                )
                Text(
                    text = "D-${state.dDay}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        PumProgressIndicator(
            label = "임신 진행률",
            current = state.currentWeek,
            total = 40
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "아기 크기",
                fontSize = 12.sp,
                color = colors.onSurfaceSubtle
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = state.babySizeDescription,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
        }
    }
}

// ── C. 빠른 액션 ─────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionC_QuickActions(
    onWeightRecord: () -> Unit,
    onLetterWrite: () -> Unit,
    onScheduleAdd: () -> Unit,
    onPhotoAdd: () -> Unit
) {
    val colors = PumTheme.colors

    data class QuickAction(val icon: ImageVector, val label: String, val onClick: () -> Unit)

    val actions = listOf(
        QuickAction(Icons.Rounded.FavoriteBorder, "체중기록", onWeightRecord),
        QuickAction(Icons.Rounded.Edit, "편지쓰기", onLetterWrite),
        QuickAction(Icons.Rounded.DateRange, "일정추가", onScheduleAdd),
        QuickAction(Icons.Rounded.Image, "사진추가", onPhotoAdd)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { action ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PumIconButton(
                    icon = action.icon,
                    contentDescription = action.label,
                    onClick = action.onClick
                )
                Text(
                    text = action.label,
                    fontSize = 12.sp,
                    color = colors.onSurfaceSubtle,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── D. 이번 주 할 일 ─────────────────────────────────────────────────────────

@Composable
private fun HomeSectionD_Checklist(
    state: HomeState,
    onToggle: (String) -> Unit,
    onMoreClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        HomeSectionHeader(title = "이번 주 할 일", onMoreClick = onMoreClick)
        Spacer(modifier = Modifier.height(10.dp))

        if (state.weeklyChecklist.isEmpty()) {
            HomeEmptyState(message = "이번 주 할 일이 없어요")
        } else {
            val items = state.weeklyChecklist.take(3)
            val completed = items.count { it.isChecked }
            PumChecklistCard(
                title = "체크리스트",
                items = items,
                completedCount = completed,
                totalCount = items.size,
                modifier = Modifier.fillMaxWidth(),
                onItemClick = onToggle
            )
        }
    }
}

// ── E. 다가오는 일정 ─────────────────────────────────────────────────────────

@Composable
private fun HomeSectionE_Schedules(
    state: HomeState,
    onMoreClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        HomeSectionHeader(title = "다가오는 일정", onMoreClick = onMoreClick)
        Spacer(modifier = Modifier.height(10.dp))

        if (state.upcomingSchedules.isEmpty()) {
            HomeEmptyState(message = "다가오는 일정이 없어요")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.upcomingSchedules.take(2).forEach { schedule ->
                    val localDate = Instant.fromEpochMilliseconds(schedule.dateMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    PumScheduleCard(
                        month = "${localDate.monthNumber}월",
                        day = "${localDate.dayOfMonth}",
                        title = schedule.title,
                        subtitle = schedule.description.ifBlank { schedule.category.label() },
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ── F. 최근 기록 ─────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionF_Records(
    state: HomeState,
    onMoreClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 기록",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PumTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )
            PumTextButton(text = "더보기", onClick = onMoreClick)
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (state.recentRecords.isEmpty()) {
            HomeEmptyState(
                message = "아직 기록이 없어요",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.recentRecords.take(2)) { record ->
                    val localDate = Instant.fromEpochMilliseconds(record.date)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val dateStr = "${localDate.monthNumber}월 ${localDate.dayOfMonth}일"

                    val (value, unit, label) = when {
                        record.weight != null -> Triple(
                            record.weight.toString(),
                            "kg",
                            "체중"
                        )
                        record.fetalMovementCount != null -> Triple(
                            record.fetalMovementCount.toString(),
                            "회",
                            "태동"
                        )
                        else -> Triple("-", "", "기록")
                    }

                    PumRecordCard(
                        date = dateStr,
                        value = value,
                        unit = unit,
                        label = label,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }
        }
    }
}

// ── 공통 헬퍼 ────────────────────────────────────────────────────────────────

@Composable
private fun HomeSectionHeader(title: String, onMoreClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = PumTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        PumTextButton(text = "더보기", onClick = onMoreClick)
    }
}

@Composable
private fun HomeEmptyState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PumTheme.colors.surface)
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = PumTheme.colors.onSurfaceSubtle
        )
    }
}
