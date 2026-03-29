package com.mybaby.app.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.NotificationTiming
import com.mybaby.app.core.model.ScheduleCategory
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAddScreen(
    viewModel: ScheduleAddViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ScheduleUiEvent.NavigateBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(ScheduleAddIntent.RequestBack) }) {
                        Icon(Icons.Rounded.Close, contentDescription = "닫기")
                    }
                },
                title = {
                    Text(
                        "일정 추가",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(ScheduleAddIntent.Save) },
                        enabled = !state.isSaving
                    ) {
                        Text(
                            "저장",
                            color = if (state.isSaving) PumTheme.colors.onSurfaceSubtle else PumTheme.colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PumTheme.colors.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 에러
            state.errorMessage?.let { msg ->
                Text(msg, color = Color(0xFFE53935), fontSize = 13.sp)
            }

            // 제목
            AddSection(title = "제목") {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.handleIntent(ScheduleAddIntent.SetTitle(it)) },
                    placeholder = { Text("일정 제목") },
                    singleLine = true,
                    isError = state.errorMessage != null && state.title.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PumTheme.colors.primary,
                        unfocusedBorderColor = PumTheme.colors.outline
                    )
                )
            }

            // 카테고리
            AddSection(title = "카테고리") {
                CategoryChips(
                    selected = state.selectedCategory,
                    onSelect = { viewModel.handleIntent(ScheduleAddIntent.SetCategory(it)) }
                )
            }

            // 장소
            AddSection(title = "장소 (선택)") {
                OutlinedTextField(
                    value = state.location,
                    onValueChange = { viewModel.handleIntent(ScheduleAddIntent.SetLocation(it)) },
                    placeholder = { Text("병원/장소 이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PumTheme.colors.primary,
                        unfocusedBorderColor = PumTheme.colors.outline
                    )
                )
            }

            // 메모
            AddSection(title = "메모 (선택)") {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.handleIntent(ScheduleAddIntent.SetDescription(it)) },
                    placeholder = { Text("추가 메모") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PumTheme.colors.primary,
                        unfocusedBorderColor = PumTheme.colors.outline
                    )
                )
            }

            // 알림
            AddSection(title = "알림") {
                NotificationChips(
                    selected = state.selectedNotification,
                    onSelect = { viewModel.handleIntent(ScheduleAddIntent.SetNotification(it)) }
                )
            }

            if (state.isSaving) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PumTheme.colors.primary, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
private fun AddSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = PumTheme.colors.onSurfaceSubtle
        )
        content()
    }
}

@Composable
private fun CategoryChips(
    selected: ScheduleCategory,
    onSelect: (ScheduleCategory) -> Unit
) {
    val categories = ScheduleCategory.entries
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = selected == category
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PumTheme.colors.primary else PumTheme.colors.surface)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = category.label(),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) PumTheme.colors.onPrimary else PumTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun NotificationChips(
    selected: NotificationTiming,
    onSelect: (NotificationTiming) -> Unit
) {
    val timings = NotificationTiming.entries
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(timings) { timing ->
            val isSelected = selected == timing
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PumTheme.colors.secondaryLight else PumTheme.colors.surface)
                    .clickable { onSelect(timing) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = timing.label(),
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) PumTheme.colors.secondaryVariant else PumTheme.colors.onSurface
                )
            }
        }
    }
}
