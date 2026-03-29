package com.mybaby.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.RecordCategory
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordAddScreen(
    viewModel: HealthRecordAddViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RecordUiEvent.NavigateBack -> onNavigateBack()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(HealthRecordAddIntent.RequestBack) }) {
                        Icon(Icons.Rounded.Close, contentDescription = "닫기")
                    }
                },
                title = {
                    Text(
                        "기록 추가",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(HealthRecordAddIntent.Save) },
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
            // 카테고리 선택
            AddSection(title = "카테고리") {
                CategorySelector(
                    selected = state.selectedCategory,
                    onSelect = { viewModel.handleIntent(HealthRecordAddIntent.SetCategory(it)) }
                )
            }

            // 에러 메시지
            state.errorMessage?.let { msg ->
                Text(msg, color = Color(0xFFE53935), fontSize = 13.sp)
            }

            // 카테고리별 입력 폼
            when (state.selectedCategory) {
                RecordCategory.WEIGHT -> WeightForm(state, viewModel)
                RecordCategory.BLOOD_PRESSURE -> BloodPressureForm(state, viewModel)
                RecordCategory.KICK -> KickForm(state, viewModel)
                RecordCategory.MEMO -> MemoForm(state, viewModel)
                RecordCategory.PHOTO -> PhotoPlaceholder()
                else -> {}
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
private fun CategorySelector(
    selected: RecordCategory,
    onSelect: (RecordCategory) -> Unit
) {
    val categories = listOf(
        RecordCategory.WEIGHT to "체중",
        RecordCategory.BLOOD_PRESSURE to "혈압",
        RecordCategory.KICK to "태동",
        RecordCategory.MEMO to "메모"
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { (category, label) ->
            val isSelected = selected == category
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PumTheme.colors.primary else PumTheme.colors.surface)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) PumTheme.colors.onPrimary else PumTheme.colors.onSurface
                )
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
private fun WeightForm(state: HealthRecordAddState, viewModel: HealthRecordAddViewModel) {
    AddSection(title = "체중") {
        OutlinedTextField(
            value = state.weightKg,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetWeight(it)) },
            placeholder = { Text("예: 62.5") },
            suffix = { Text("kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
    AddSection(title = "메모 (선택)") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetMemoContent(it)) },
            placeholder = { Text("메모를 입력하세요") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
}

@Composable
private fun BloodPressureForm(state: HealthRecordAddState, viewModel: HealthRecordAddViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AddSection(title = "수축기 혈압", content = {
            OutlinedTextField(
                value = state.systolicBp,
                onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetSystolicBp(it)) },
                placeholder = { Text("예: 120") },
                suffix = { Text("mmHg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PumTheme.colors.primary,
                    unfocusedBorderColor = PumTheme.colors.outline
                )
            )
        })
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AddSection(title = "이완기 혈압", content = {
            OutlinedTextField(
                value = state.diastolicBp,
                onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetDiastolicBp(it)) },
                placeholder = { Text("예: 80") },
                suffix = { Text("mmHg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PumTheme.colors.primary,
                    unfocusedBorderColor = PumTheme.colors.outline
                )
            )
        })
    }
    AddSection(title = "메모 (선택)") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetMemoContent(it)) },
            placeholder = { Text("메모를 입력하세요") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
}

@Composable
private fun KickForm(state: HealthRecordAddState, viewModel: HealthRecordAddViewModel) {
    AddSection(title = "태동 횟수") {
        OutlinedTextField(
            value = state.kickCount,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetKickCount(it)) },
            placeholder = { Text("예: 10") },
            suffix = { Text("회") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
    AddSection(title = "측정 시간 (선택)") {
        OutlinedTextField(
            value = state.kickTimeMinutes,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetKickTimeMinutes(it)) },
            placeholder = { Text("예: 120") },
            suffix = { Text("분") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
    AddSection(title = "메모 (선택)") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetMemoContent(it)) },
            placeholder = { Text("메모를 입력하세요") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
}

@Composable
private fun MemoForm(state: HealthRecordAddState, viewModel: HealthRecordAddViewModel) {
    AddSection(title = "제목 (선택)") {
        OutlinedTextField(
            value = state.memoTitle,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetMemoTitle(it)) },
            placeholder = { Text("메모 제목") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
    AddSection(title = "내용") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordAddIntent.SetMemoContent(it)) },
            placeholder = { Text("메모 내용을 입력하세요") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline
            )
        )
    }
}

@Composable
private fun PhotoPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PumTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📷", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "사진 추가 기능은 준비 중이에요",
                fontSize = 14.sp,
                color = PumTheme.colors.onSurfaceSubtle
            )
        }
    }
}
