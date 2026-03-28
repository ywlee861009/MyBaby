package com.mybaby.app.feature.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPregnancyInfoScreen(
    viewModel: SetupPregnancyInfoViewModel,
    onNavigateBack: () -> Unit,
    onSetupComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.selectedDateMillis
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SetupPregnancyInfoEvent.SetupComplete -> onSetupComplete()
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.handleIntent(SetupPregnancyInfoIntent.SelectDate(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("확인", color = PumTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소", color = PumTheme.colors.onSurfaceSubtle)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = {
                    Text(
                        text = if (state.isBorn) "아기 생일 선택" else "출산 예정일 선택",
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                        style = PumTheme.typography.headlineSmall,
                        color = PumTheme.colors.onSurface
                    )
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PumTheme.colors.background)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp)
    ) {
        // 뒤로가기
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "뒤로",
                tint = PumTheme.colors.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 타이틀
        Text(
            text = if (state.isBorn) "아기 생일을\n알려주세요" else "출산 예정일을\n알려주세요",
            style = PumTheme.typography.headlineLarge,
            color = PumTheme.colors.onBackground,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (state.isBorn)
                "${state.nickname}이(가) 태어난 날짜를 선택해주세요"
            else
                "${state.nickname}이(가) 태어날 예정일을 선택해주세요",
            style = PumTheme.typography.bodyMedium,
            color = PumTheme.colors.onSurfaceSubtle
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 날짜 선택 카드
        DateSelectionCard(
            selectedDateMillis = state.selectedDateMillis,
            isBorn = state.isBorn,
            onClick = { showDatePicker = true }
        )

        // 임신 중인 경우 주수 표시
        if (!state.isBorn && state.selectedDateMillis != null) {
            Spacer(modifier = Modifier.height(20.dp))
            PregnancyWeeksCard(
                weeks = state.pregnancyWeeks,
                days = state.pregnancyDays
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 시작하기 버튼
        Button(
            onClick = { viewModel.handleIntent(SetupPregnancyInfoIntent.Save) },
            enabled = state.canSave && !state.isSaving,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PumTheme.colors.primary,
                disabledContainerColor = PumTheme.colors.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PumTheme.colors.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "시작하기",
                    style = PumTheme.typography.labelLarge,
                    color = if (state.canSave) PumTheme.colors.onPrimary else PumTheme.colors.onSurfaceSubtle
                )
            }
        }
    }
}

@Composable
private fun DateSelectionCard(
    selectedDateMillis: Long?,
    isBorn: Boolean,
    onClick: () -> Unit
) {
    val dateText = selectedDateMillis?.let { millis ->
        val date = Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        "${date.year}년 ${date.monthNumber}월 ${date.dayOfMonth}일"
    } ?: if (isBorn) "생일 선택하기" else "출산 예정일 선택하기"

    val isPlaceholder = selectedDateMillis == null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PumTheme.colors.surface)
            .border(
                width = 1.5.dp,
                color = if (isPlaceholder) PumTheme.colors.outline else PumTheme.colors.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = null,
                tint = if (isPlaceholder) PumTheme.colors.onSurfaceSubtle else PumTheme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = dateText,
                style = PumTheme.typography.bodyLarge,
                color = if (isPlaceholder) PumTheme.colors.onSurfaceSubtle else PumTheme.colors.onSurface,
                fontWeight = if (isPlaceholder) FontWeight.Normal else FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PregnancyWeeksCard(weeks: Int, days: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PumTheme.colors.primaryLight)
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "현재 임신",
                style = PumTheme.typography.bodyMedium,
                color = PumTheme.colors.onSurfaceSubtle
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${weeks}주 ${days}일째",
                style = PumTheme.typography.headlineMedium,
                color = PumTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "출산 예정일까지 ${((40 - weeks) * 7 - days).coerceAtLeast(0)}일 남았어요",
                style = PumTheme.typography.bodyMedium,
                color = PumTheme.colors.onSurfaceSubtle,
                textAlign = TextAlign.Center
            )
        }
    }
}
