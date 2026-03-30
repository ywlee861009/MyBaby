package com.mybaby.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun HealthRecordListScreen(
    viewModel: HealthRecordListViewModel,
    onNavigateToAdd: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RecordUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "건강 기록",
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
                onClick = onNavigateToAdd,
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
            // 카테고리 필터
            CategoryFilterRow(
                selected = state.selectedCategory,
                onSelect = { viewModel.handleIntent(HealthRecordListIntent.SelectCategory(it)) }
            )

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PumTheme.colors.primary)
                    }
                }
                state.errorMessage != null -> {
                    ErrorState(message = state.errorMessage!!, modifier = Modifier.fillMaxSize())
                }
                state.records.isEmpty() -> {
                    RecordEmptyState(modifier = Modifier.fillMaxSize())
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.records) { record ->
                            RecordCard(
                                record = record,
                                onDelete = { viewModel.handleIntent(HealthRecordListIntent.DeleteRecord(record.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: RecordCategory,
    onSelect: (RecordCategory) -> Unit
) {
    val categories = listOf(
        RecordCategory.ALL to "전체",
        RecordCategory.WEIGHT to "체중",
        RecordCategory.BLOOD_PRESSURE to "혈압",
        RecordCategory.KICK to "태동",
        RecordCategory.MEMO to "메모"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (category, label) ->
            val isSelected = selected == category
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PumTheme.colors.primary else PumTheme.colors.surface)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) PumTheme.colors.onPrimary else PumTheme.colors.onSurfaceSubtle
                )
            }
        }
    }
}

@Composable
private fun RecordCard(
    record: HealthRecord,
    onDelete: () -> Unit
) {
    val colors = PumTheme.colors
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("기록을 삭제할까요?") },
            text = { Text("삭제된 기록은 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("삭제", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    val localDate = Instant.fromEpochMilliseconds(record.date)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dateStr = "${localDate.year}.${localDate.monthNumber.toString().padStart(2, '0')}.${localDate.dayOfMonth.toString().padStart(2, '0')}"

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
                CategoryBadge(category = record.category)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(dateStr, color = colors.onSurfaceSubtle, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { showDeleteDialog = true },
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("삭제", color = Color(0xFFE53935), fontSize = 12.sp)
                    }
                }
            }
            RecordValue(record = record)
        }
    }
}

@Composable
private fun CategoryBadge(category: RecordCategory) {
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
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RecordValue(record: HealthRecord) {
    val colors = PumTheme.colors
    when (record.category) {
        RecordCategory.WEIGHT -> {
            if (record.weightKg != null) {
                Text(
                    text = "${record.weightKg} kg",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
            }
        }
        RecordCategory.BLOOD_PRESSURE -> {
            if (record.systolicBp != null && record.diastolicBp != null) {
                Text(
                    text = "${record.systolicBp} / ${record.diastolicBp} mmHg",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                record.memoContent?.let {
                    Text(it, fontSize = 13.sp, color = colors.onSurfaceSubtle)
                }
            }
        }
        RecordCategory.KICK -> {
            if (record.kickCount != null) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${record.kickCount}회",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    record.kickTimeMinutes?.let {
                        Text(
                            text = "${it}분 측정",
                            fontSize = 13.sp,
                            color = colors.onSurfaceSubtle,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
            }
        }
        RecordCategory.MEMO -> {
            record.memoTitle?.let {
                Text(it, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
            }
            record.memoContent?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = colors.onSurfaceSubtle,
                    maxLines = 2,
                    lineHeight = 18.sp
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
private fun RecordEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📋", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "첫 번째 기록을 추가해 보세요",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = PumTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "+ 버튼을 눌러 기록하세요",
            fontSize = 14.sp,
            color = PumTheme.colors.onSurfaceSubtle
        )
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("기록을 불러오지 못했어요", fontSize = 16.sp, color = PumTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, fontSize = 13.sp, color = PumTheme.colors.onSurfaceSubtle)
    }
}
