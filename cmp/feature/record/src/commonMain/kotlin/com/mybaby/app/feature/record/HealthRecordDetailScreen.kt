package com.mybaby.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.RecordCategory
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordDetailScreen(
    viewModel: HealthRecordDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RecordUiEvent.NavigateBack -> onNavigateBack()
                is RecordUiEvent.NavigateToEdit -> onNavigateToEdit(event.id)
                is RecordUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Text(
                        text = "←",
                        fontSize = 20.sp,
                        color = PumTheme.colors.onSurface,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable(onClick = onNavigateBack)
                    )
                },
                title = {
                    Text(
                        "기록 상세",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                actions = {
                    Box {
                        Text(
                            text = "⋮",
                            fontSize = 22.sp,
                            color = PumTheme.colors.onSurface,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { viewModel.handleIntent(HealthRecordDetailIntent.OpenMenu) }
                        )
                        DropdownMenu(
                            expanded = state.showMenu,
                            onDismissRequest = { viewModel.handleIntent(HealthRecordDetailIntent.CloseMenu) }
                        ) {
                            DropdownMenuItem(
                                text = { Text("수정") },
                                onClick = { viewModel.handleIntent(HealthRecordDetailIntent.NavigateEdit) }
                            )
                            DropdownMenuItem(
                                text = { Text("삭제", color = PumTheme.colors.error) },
                                onClick = { viewModel.handleIntent(HealthRecordDetailIntent.ShowDeleteDialog) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.surface
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PumTheme.colors.primary
                    )
                }
                state.record != null -> {
                    RecordDetailContent(
                        record = state.record!!,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Text(
                        "기록을 찾을 수 없어요",
                        modifier = Modifier.align(Alignment.Center),
                        color = PumTheme.colors.onSurfaceSubtle
                    )
                }
            }
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.handleIntent(HealthRecordDetailIntent.DismissDeleteDialog) },
            title = { Text("기록을 삭제할까요?", fontWeight = FontWeight.SemiBold) },
            text = { Text("삭제된 기록은 복구할 수 없습니다.", color = PumTheme.colors.onSurfaceSubtle) },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(HealthRecordDetailIntent.DismissDeleteDialog) },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = PumTheme.colors.primaryLight,
                        contentColor = PumTheme.colors.primary
                    )
                ) { Text("취소") }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(HealthRecordDetailIntent.ConfirmDelete) },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = PumTheme.colors.error,
                        contentColor = Color.White
                    )
                ) { Text("삭제") }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = PumTheme.colors.surface
        )
    }
}

@Composable
private fun RecordDetailContent(
    record: HealthRecord,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors
    val localDate = Instant.fromEpochMilliseconds(record.date)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dateStr = "${localDate.year}.${localDate.monthNumber.toString().padStart(2, '0')}.${localDate.dayOfMonth.toString().padStart(2, '0')}"

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 헤더 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
                .background(colors.surface, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RecordDetailCategoryBadge(category = record.category)
                    Text(dateStr, color = colors.onSurfaceSubtle, fontSize = 13.sp)
                }

                if (record.weekNumber > 0) {
                    Text(
                        text = "${record.weekNumber}주차",
                        fontSize = 13.sp,
                        color = colors.onSurfaceSubtle
                    )
                }

                HorizontalDivider(color = colors.outline, thickness = 1.dp)

                RecordDetailValues(record = record)
            }
        }

        // 메모 카드 (혈압/체중의 부가 메모, 또는 메모 카테고리)
        if (record.category != RecordCategory.MEMO && !record.memoContent.isNullOrBlank()) {
            DetailSectionCard(title = "메모") {
                Text(
                    text = record.memoContent!!,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun RecordDetailValues(record: HealthRecord) {
    val colors = PumTheme.colors
    when (record.category) {
        RecordCategory.WEIGHT -> {
            record.weightKg?.let { weight ->
                DetailValueRow(label = "체중", value = "$weight kg")
            }
        }
        RecordCategory.BLOOD_PRESSURE -> {
            if (record.systolicBp != null && record.diastolicBp != null) {
                DetailValueRow(label = "수축기 혈압", value = "${record.systolicBp} mmHg")
                DetailValueRow(label = "이완기 혈압", value = "${record.diastolicBp} mmHg")
            }
        }
        RecordCategory.KICK -> {
            record.kickCount?.let { count ->
                DetailValueRow(label = "태동 횟수", value = "${count}회")
            }
            record.kickTimeMinutes?.let { minutes ->
                DetailValueRow(label = "측정 시간", value = "${minutes}분")
            }
        }
        RecordCategory.MEMO -> {
            record.memoTitle?.let { title ->
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
            }
            record.memoContent?.let { content ->
                Text(
                    text = content,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = colors.onSurface
                )
            }
        }
        RecordCategory.PHOTO -> {
            Text("초음파 사진", fontSize = 14.sp, color = colors.onSurfaceSubtle)
        }
        RecordCategory.ALL -> {}
    }
}

@Composable
private fun DetailValueRow(label: String, value: String) {
    val colors = PumTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = colors.onSurfaceSubtle)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
    }
}

@Composable
private fun DetailSectionCard(title: String, content: @Composable () -> Unit) {
    val colors = PumTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
            .background(colors.surface, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurfaceSubtle)
            content()
        }
    }
}

@Composable
private fun RecordDetailCategoryBadge(category: RecordCategory) {
    val (label, bgColor, textColor) = when (category) {
        RecordCategory.WEIGHT -> Triple("체중", PumTheme.colors.primaryLight, PumTheme.colors.primary)
        RecordCategory.BLOOD_PRESSURE -> Triple("혈압", Color(0xFFFFF0F0), Color(0xFFE53935))
        RecordCategory.KICK -> Triple("태동", PumTheme.colors.secondaryLight, PumTheme.colors.secondaryVariant)
        RecordCategory.PHOTO -> Triple("사진", Color(0xFFF0F4FF), Color(0xFF4A90D9))
        RecordCategory.MEMO -> Triple("메모", Color(0xFFF5F5F5), Color(0xFF757575))
        RecordCategory.ALL -> Triple("전체", PumTheme.colors.primaryLight, PumTheme.colors.primary)
    }
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(label, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
