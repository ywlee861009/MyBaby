package com.mybaby.app.feature.more

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.Baby
import com.mybaby.app.core.model.BabyGender
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(viewModel: MoreViewModel) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.editDateMillis
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                MoreEvent.SaveSuccess ->
                    snackbarHostState.showSnackbar("저장되었습니다")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.handleIntent(MoreIntent.SelectDate(millis))
                        }
                        showDatePicker = false
                    }
                ) { Text("확인", color = PumTheme.colors.primary) }
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
                        text = if (state.editIsBorn) "아기 생일 선택" else "출산 예정일 선택",
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                        style = PumTheme.typography.headlineSmall,
                        color = PumTheme.colors.onSurface
                    )
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PumTheme.colors.background)
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PumTheme.colors.primary
                    )
                }
                state.baby == null -> {
                    Text(
                        text = "아기 정보가 없습니다",
                        modifier = Modifier.align(Alignment.Center),
                        color = PumTheme.colors.onSurfaceSubtle
                    )
                }
                state.isEditing -> {
                    EditContent(
                        state = state,
                        onIntent = { viewModel.handleIntent(it) },
                        onShowDatePicker = { showDatePicker = true },
                        onHideKeyboard = { keyboardController?.hide() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    ViewContent(
                        baby = state.baby!!,
                        onEditClick = { viewModel.handleIntent(MoreIntent.StartEdit) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewContent(
    baby: Baby,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBorn = baby.birthDate != null
    val dateMillis = if (isBorn) baby.birthDate else baby.dueDate
    val pregnancyInfo: Pair<Int, Int>? = if (!isBorn && dateMillis != null) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dueDate = Instant.fromEpochMilliseconds(dateMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysUntilDue = today.daysUntil(dueDate)
        val daysPregnant = 280 - daysUntilDue
        Pair((daysPregnant / 7).coerceAtLeast(0), (daysPregnant % 7).coerceAtLeast(0))
    } else null

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "더보기",
                style = PumTheme.typography.headlineMedium,
                color = PumTheme.colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "수정",
                    tint = PumTheme.colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 아기 프로필 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PumTheme.colors.surface)
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(PumTheme.colors.primaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBorn) "👶" else "🍼",
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = baby.nickname,
                            style = PumTheme.typography.headlineSmall,
                            color = PumTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoChip(
                                text = baby.gender.label(),
                                containerColor = PumTheme.colors.primaryLight,
                                textColor = PumTheme.colors.primary
                            )
                            InfoChip(
                                text = if (isBorn) "출산 완료" else "임신 중",
                                containerColor = PumTheme.colors.secondaryLight,
                                textColor = PumTheme.colors.secondaryVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 날짜 정보 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PumTheme.colors.surface)
                .padding(24.dp)
        ) {
            Column {
                SectionTitle(if (isBorn) "생일" else "출산 예정일")
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = null,
                        tint = PumTheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateMillis?.let { millis ->
                            val d = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            "${d.year}년 ${d.monthNumber}월 ${d.dayOfMonth}일"
                        } ?: "미정",
                        style = PumTheme.typography.bodyLarge,
                        color = PumTheme.colors.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (!isBorn && pregnancyInfo != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = PumTheme.colors.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "임신 주수",
                                style = PumTheme.typography.labelSmall,
                                color = PumTheme.colors.onSurfaceSubtle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${pregnancyInfo.first}주 ${pregnancyInfo.second}일",
                                style = PumTheme.typography.headlineSmall,
                                color = PumTheme.colors.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "출산까지",
                                style = PumTheme.typography.labelSmall,
                                color = PumTheme.colors.onSurfaceSubtle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val daysLeft = ((40 - pregnancyInfo.first) * 7 - pregnancyInfo.second).coerceAtLeast(0)
                            Text(
                                text = "${daysLeft}일",
                                style = PumTheme.typography.headlineSmall,
                                color = PumTheme.colors.secondaryVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditContent(
    state: MoreState,
    onIntent: (MoreIntent) -> Unit,
    onShowDatePicker: () -> Unit,
    onHideKeyboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        // 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "정보 수정",
                style = PumTheme.typography.headlineMedium,
                color = PumTheme.colors.onBackground,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { onIntent(MoreIntent.CancelEdit) }) {
                Text("취소", color = PumTheme.colors.onSurfaceSubtle)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 닉네임
        FieldLabel("아기 닉네임")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = state.editNickname,
            onValueChange = { onIntent(MoreIntent.UpdateNickname(it)) },
            placeholder = {
                Text("예: 콩이, 열무, 별님", color = PumTheme.colors.onSurfaceSubtle)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onHideKeyboard() }),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PumTheme.colors.primary,
                unfocusedBorderColor = PumTheme.colors.outline,
                focusedContainerColor = PumTheme.colors.surface,
                unfocusedContainerColor = PumTheme.colors.surface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 성별
        FieldLabel("성별")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BabyGender.entries.forEach { gender ->
                SelectableChip(
                    label = gender.label(),
                    isSelected = state.editGender == gender,
                    onClick = { onIntent(MoreIntent.SelectGender(gender)) },
                    selectedBg = PumTheme.colors.primaryLight,
                    selectedBorder = PumTheme.colors.primary,
                    selectedText = PumTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 현재 상태
        FieldLabel("현재 상태")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SelectableChip(
                label = "임신 중",
                isSelected = !state.editIsBorn,
                onClick = { onIntent(MoreIntent.SetBornStatus(false)) },
                selectedBg = PumTheme.colors.secondaryLight,
                selectedBorder = PumTheme.colors.secondaryVariant,
                selectedText = PumTheme.colors.secondaryVariant,
                modifier = Modifier.weight(1f)
            )
            SelectableChip(
                label = "이미 출산",
                isSelected = state.editIsBorn,
                onClick = { onIntent(MoreIntent.SetBornStatus(true)) },
                selectedBg = PumTheme.colors.secondaryLight,
                selectedBorder = PumTheme.colors.secondaryVariant,
                selectedText = PumTheme.colors.secondaryVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 날짜 선택
        FieldLabel(if (state.editIsBorn) "아기 생일" else "출산 예정일")
        Spacer(modifier = Modifier.height(8.dp))
        DateSelectButton(
            dateMillis = state.editDateMillis,
            isBorn = state.editIsBorn,
            onClick = onShowDatePicker
        )

        // 임신 주수 표시
        if (!state.editIsBorn && state.editDateMillis != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PumTheme.colors.primaryLight)
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "현재 임신",
                        style = PumTheme.typography.bodyMedium,
                        color = PumTheme.colors.onSurfaceSubtle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${state.pregnancyWeeks}주 ${state.pregnancyDays}일째",
                        style = PumTheme.typography.headlineMedium,
                        color = PumTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val daysLeft = ((40 - state.pregnancyWeeks) * 7 - state.pregnancyDays).coerceAtLeast(0)
                    Text(
                        text = "출산 예정일까지 ${daysLeft}일 남았어요",
                        style = PumTheme.typography.bodyMedium,
                        color = PumTheme.colors.onSurfaceSubtle,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 저장 버튼
        Button(
            onClick = { onIntent(MoreIntent.Save) },
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
                    text = "저장하기",
                    style = PumTheme.typography.labelLarge,
                    color = if (state.canSave) PumTheme.colors.onPrimary else PumTheme.colors.onSurfaceSubtle
                )
            }
        }
    }
}

@Composable
private fun DateSelectButton(
    dateMillis: Long?,
    isBorn: Boolean,
    onClick: () -> Unit
) {
    val dateText = dateMillis?.let { millis ->
        val d = Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        "${d.year}년 ${d.monthNumber}월 ${d.dayOfMonth}일"
    } ?: if (isBorn) "생일 선택하기" else "출산 예정일 선택하기"
    val isPlaceholder = dateMillis == null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PumTheme.colors.surface)
            .border(
                width = 1.5.dp,
                color = if (isPlaceholder) PumTheme.colors.outline else PumTheme.colors.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = null,
                tint = if (isPlaceholder) PumTheme.colors.onSurfaceSubtle else PumTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = dateText,
                style = PumTheme.typography.bodyMedium,
                color = if (isPlaceholder) PumTheme.colors.onSurfaceSubtle else PumTheme.colors.onSurface,
                fontWeight = if (isPlaceholder) FontWeight.Normal else FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    containerColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = PumTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedBg: Color,
    selectedBorder: Color,
    selectedText: Color,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) selectedBg else PumTheme.colors.surface
    val borderColor = if (isSelected) selectedBorder else PumTheme.colors.outline
    val textColor = if (isSelected) selectedText else PumTheme.colors.onSurfaceSubtle

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = PumTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = PumTheme.typography.labelLarge,
        color = PumTheme.colors.onSurface,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = PumTheme.typography.labelLarge,
        color = PumTheme.colors.onSurfaceSubtle,
        fontWeight = FontWeight.Medium
    )
}
