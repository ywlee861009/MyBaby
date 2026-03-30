package com.mybaby.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun HealthRecordEditScreen(
    viewModel: HealthRecordEditViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RecordUiEvent.NavigateBack -> onNavigateBack()
                is RecordUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleIntent(HealthRecordEditIntent.RequestBack) }) {
                        Icon(Icons.Rounded.Close, contentDescription = "닫기")
                    }
                },
                title = {
                    Text(
                        "기록 수정",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.handleIntent(HealthRecordEditIntent.Save) },
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
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PumTheme.colors.primary)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(PumTheme.colors.background)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 카테고리 표시 (수정 불가 — 기존 카테고리 유지)
                    EditSection(title = "카테고리") {
                        Box(
                            modifier = Modifier
                                .background(PumTheme.colors.primaryLight, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = state.selectedCategory.toLabel(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PumTheme.colors.primary
                            )
                        }
                    }

                    state.errorMessage?.let { msg ->
                        Text(msg, color = Color(0xFFE53935), fontSize = 13.sp)
                    }

                    when (state.selectedCategory) {
                        RecordCategory.WEIGHT -> WeightEditForm(state, viewModel)
                        RecordCategory.BLOOD_PRESSURE -> BloodPressureEditForm(state, viewModel)
                        RecordCategory.KICK -> KickEditForm(state, viewModel)
                        RecordCategory.MEMO -> MemoEditForm(state, viewModel)
                        RecordCategory.PHOTO -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(PumTheme.colors.surface, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("사진 수정 기능은 준비 중이에요", fontSize = 14.sp, color = PumTheme.colors.onSurfaceSubtle)
                            }
                        }
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
    }
}

private fun RecordCategory.toLabel(): String = when (this) {
    RecordCategory.WEIGHT -> "체중"
    RecordCategory.BLOOD_PRESSURE -> "혈압"
    RecordCategory.KICK -> "태동"
    RecordCategory.MEMO -> "메모"
    RecordCategory.PHOTO -> "사진"
    RecordCategory.ALL -> "전체"
}

@Composable
private fun EditSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PumTheme.colors.onSurfaceSubtle)
        content()
    }
}

@Composable
private fun WeightEditForm(state: HealthRecordEditState, viewModel: HealthRecordEditViewModel) {
    EditSection(title = "체중") {
        OutlinedTextField(
            value = state.weightKg,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetWeight(it)) },
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
    EditSection(title = "메모 (선택)") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetMemoContent(it)) },
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
private fun BloodPressureEditForm(state: HealthRecordEditState, viewModel: HealthRecordEditViewModel) {
    EditSection(title = "수축기 혈압") {
        OutlinedTextField(
            value = state.systolicBp,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetSystolicBp(it)) },
            placeholder = { Text("예: 120") },
            suffix = { Text("mmHg") },
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
    EditSection(title = "이완기 혈압") {
        OutlinedTextField(
            value = state.diastolicBp,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetDiastolicBp(it)) },
            placeholder = { Text("예: 80") },
            suffix = { Text("mmHg") },
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
    EditSection(title = "메모 (선택)") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetMemoContent(it)) },
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
private fun KickEditForm(state: HealthRecordEditState, viewModel: HealthRecordEditViewModel) {
    EditSection(title = "태동 횟수") {
        OutlinedTextField(
            value = state.kickCount,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetKickCount(it)) },
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
    EditSection(title = "측정 시간 (선택)") {
        OutlinedTextField(
            value = state.kickTimeMinutes,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetKickTimeMinutes(it)) },
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
    EditSection(title = "메모 (선택)") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetMemoContent(it)) },
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
private fun MemoEditForm(state: HealthRecordEditState, viewModel: HealthRecordEditViewModel) {
    EditSection(title = "제목 (선택)") {
        OutlinedTextField(
            value = state.memoTitle,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetMemoTitle(it)) },
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
    EditSection(title = "내용") {
        OutlinedTextField(
            value = state.memoContent,
            onValueChange = { viewModel.handleIntent(HealthRecordEditIntent.SetMemoContent(it)) },
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
