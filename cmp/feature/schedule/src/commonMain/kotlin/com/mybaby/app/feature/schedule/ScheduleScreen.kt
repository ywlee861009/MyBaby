package com.mybaby.app.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.core.model.ScheduleCategory
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onNavigateToAdd: (LocalDate) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ScheduleUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "일정",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.surface
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val date = state.selectedDate ?: Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    onNavigateToAdd(date)
                },
                containerColor = PumTheme.colors.primary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Text("+", fontSize = 24.sp, color = PumTheme.colors.onPrimary)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2D2020),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
        ) {
            // 달력
            MonthCalendar(
                year = state.currentYear,
                month = state.currentMonth,
                selectedDate = state.selectedDate,
                scheduledDays = state.scheduledDays,
                onPreviousMonth = { viewModel.handleIntent(ScheduleIntent.PreviousMonth) },
                onNextMonth = { viewModel.handleIntent(ScheduleIntent.NextMonth) },
                onSelectDate = { date -> viewModel.handleIntent(ScheduleIntent.SelectDate(date)) }
            )

            HorizontalDivider(color = PumTheme.colors.outline, thickness = 0.5.dp)

            // 선택된 날짜 일정 목록
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PumTheme.colors.primary, modifier = Modifier.size(32.dp))
                }
            } else if (state.schedulesOnSelectedDate.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "이 날은 일정이 없어요",
                        fontSize = 14.sp,
                        color = PumTheme.colors.onSurfaceSubtle
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.schedulesOnSelectedDate) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onDelete = { viewModel.handleIntent(ScheduleIntent.DeleteSchedule(schedule.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthCalendar(
    year: Int,
    month: Int,
    selectedDate: LocalDate?,
    scheduledDays: Set<Int>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit
) {
    val colors = PumTheme.colors
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val daysInMonth = daysInMonth(year, month)
    val firstDayOfWeek = LocalDate(year, month, 1).dayOfWeek.ordinal // 0=Mon ... 6=Sun
    // Convert to Sun=0 offset
    val startOffset = (firstDayOfWeek + 1) % 7

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PumTheme.colors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 월 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "이전 달", tint = colors.onSurface)
            }
            Text(
                text = "${year}년 ${month}월",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = "다음 달", tint = colors.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 요일 헤더
        val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = colors.onSurfaceSubtle,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 날짜 그리드
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - startOffset + 1

                    if (dayNumber < 1 || dayNumber > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = LocalDate(year, month, dayNumber)
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val hasSchedule = dayNumber in scheduledDays

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> colors.primary
                                        isSelected -> colors.primaryLight
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onSelectDate(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isToday -> colors.onPrimary
                                        isSelected -> colors.primary
                                        col == 0 -> Color(0xFFE53935) // 일요일 빨강
                                        col == 6 -> Color(0xFF4A90D9) // 토요일 파랑
                                        else -> colors.onSurface
                                    }
                                )
                                if (hasSchedule) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isToday) colors.onPrimary else colors.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    schedule: Schedule,
    onDelete: () -> Unit
) {
    val colors = PumTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("일정을 삭제할까요?") },
            text = { Text("삭제된 일정은 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("삭제", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(schedule.category)
                TextButton(
                    onClick = { showDeleteDialog = true },
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("삭제", color = Color(0xFFE53935), fontSize = 12.sp)
                }
            }
            Text(
                text = schedule.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            if (schedule.location != null) {
                Text(
                    text = "📍 ${schedule.location}",
                    fontSize = 13.sp,
                    color = colors.onSurfaceSubtle
                )
            }
            if (schedule.description.isNotBlank()) {
                Text(
                    text = schedule.description,
                    fontSize = 13.sp,
                    color = colors.onSurfaceSubtle,
                    maxLines = 2
                )
            }
            if (schedule.notification.name != "NONE") {
                Text(
                    text = "🔔 ${schedule.notification.label()}",
                    fontSize = 12.sp,
                    color = colors.onSurfaceSubtle
                )
            }
        }
    }
}

@Composable
private fun CategoryBadge(category: ScheduleCategory) {
    val (bgColor, textColor) = when (category) {
        ScheduleCategory.CHECKUP -> Pair(Color(0xFFFFF0F3), Color(0xFFFF8FAB))
        ScheduleCategory.ULTRASOUND -> Pair(Color(0xFFF3EDFB), Color(0xFF9B72C8))
        ScheduleCategory.BLOOD_TEST -> Pair(Color(0xFFFFF0F0), Color(0xFFE53935))
        ScheduleCategory.VACCINE -> Pair(Color(0xFFF0F4FF), Color(0xFF4A90D9))
        ScheduleCategory.ETC -> Pair(Color(0xFFF5F5F5), Color(0xFF757575))
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(category.label(), color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 30
}
