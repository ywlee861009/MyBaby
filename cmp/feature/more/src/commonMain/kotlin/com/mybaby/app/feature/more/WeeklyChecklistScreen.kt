package com.mybaby.app.feature.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyChecklistScreen(
    viewModel: WeeklyChecklistViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.selectedWeek) {
        val index = (state.selectedWeek - 1).coerceAtLeast(0)
        listState.animateScrollToItem(index)
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
                        "주차별 체크리스트",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.surface
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
        ) {
            // 주차 선택 가로 스크롤
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((1..40).toList()) { week ->
                    val isSelected = week == state.selectedWeek
                    val isCurrent = week == state.currentWeek
                    WeekChip(
                        week = week,
                        isSelected = isSelected,
                        isCurrent = isCurrent,
                        onClick = { viewModel.handleIntent(WeeklyChecklistIntent.SelectWeek(week)) }
                    )
                }
            }

            HorizontalDivider(color = PumTheme.colors.outline)

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PumTheme.colors.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 진행률 헤더
                    if (state.items.isNotEmpty()) {
                        val checkedCount = state.items.count { it.isChecked }
                        val total = state.items.size
                        ProgressHeader(checked = checkedCount, total = total)
                    }

                    if (state.items.isEmpty()) {
                        EmptyChecklistState(week = state.selectedWeek)
                    } else {
                        state.items.forEach { item ->
                            ChecklistItemRow(
                                item = item,
                                onToggle = { isChecked ->
                                    viewModel.handleIntent(
                                        WeeklyChecklistIntent.ToggleItem(item.id, isChecked)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekChip(
    week: Int,
    isSelected: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isSelected -> PumTheme.colors.primary
        isCurrent -> PumTheme.colors.primaryLight
        else -> PumTheme.colors.surface
    }
    val textColor = when {
        isSelected -> PumTheme.colors.onPrimary
        isCurrent -> PumTheme.colors.primary
        else -> PumTheme.colors.onSurfaceSubtle
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${week}주",
                fontSize = 13.sp,
                fontWeight = if (isSelected || isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
            if (isCurrent && !isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(PumTheme.colors.primary)
                )
            }
        }
    }
}

@Composable
private fun ProgressHeader(checked: Int, total: Int) {
    val progress = if (total > 0) checked.toFloat() / total else 0f
    val colors = PumTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.primaryLight)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "이번 주 진행률",
                    fontSize = 13.sp,
                    color = colors.onSurfaceSubtle
                )
                Text(
                    text = "$checked / $total",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = colors.primary,
                trackColor = colors.outline
            )
        }
    }
}

@Composable
private fun ChecklistItemRow(
    item: com.mybaby.app.core.model.ChecklistItem,
    onToggle: (Boolean) -> Unit
) {
    val colors = PumTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .clickable { onToggle(!item.isChecked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { onToggle(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.outline,
                checkmarkColor = colors.onPrimary
            )
        )
        Text(
            text = item.text,
            fontSize = 14.sp,
            color = if (item.isChecked) colors.onSurfaceSubtle else colors.onSurface,
            fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EmptyChecklistState(week: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("✅", fontSize = 48.sp)
            Text(
                text = "${week}주차 항목이 없어요",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = PumTheme.colors.onSurface
            )
            Text(
                text = "이번 주에 해야 할 일을 기록해 보세요",
                fontSize = 13.sp,
                color = PumTheme.colors.onSurfaceSubtle
            )
        }
    }
}
