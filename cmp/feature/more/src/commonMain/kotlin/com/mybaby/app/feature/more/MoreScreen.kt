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
fun MoreScreen(
    viewModel: MoreViewModel,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onNavigateToWeeklyChecklist: () -> Unit = {},
    onNavigateToAppInfo: () -> Unit = {}
) {
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
                    MenuContent(
                        baby = state.baby!!,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = onToggleDarkMode,
                        onEditProfile = { viewModel.handleIntent(MoreIntent.StartEdit) },
                        onNavigateToWeeklyChecklist = onNavigateToWeeklyChecklist,
                        onNavigateToAppInfo = onNavigateToAppInfo,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuContent(
    baby: Baby,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onNavigateToWeeklyChecklist: () -> Unit,
    onNavigateToAppInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBorn = baby.birthDate != null
    val dateMillis = if (isBorn) baby.birthDate else baby.dueDate
    val pregnancyWeeks: Int? = if (!isBorn && dateMillis != null) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dueDate = Instant.fromEpochMilliseconds(dateMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysUntilDue = today.daysUntil(dueDate)
        val daysPregnant = 280 - daysUntilDue
        (daysPregnant / 7).coerceAtLeast(0)
    } else null

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 헤더
        Text(
            text = "더보기",
            style = PumTheme.typography.headlineMedium,
            color = PumTheme.colors.onBackground,
            fontWeight = FontWeight.Bold
        )

        // 프로필 카드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PumTheme.colors.surface)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(PumTheme.colors.primaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isBorn) "👶" else "🤰", fontSize = 26.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = baby.nickname,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = PumTheme.colors.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            InfoChip(
                                text = baby.gender.label(),
                                containerColor = PumTheme.colors.primaryLight,
                                textColor = PumTheme.colors.primary
                            )
                            if (pregnancyWeeks != null) {
                                InfoChip(
                                    text = "${pregnancyWeeks}주차",
                                    containerColor = PumTheme.colors.secondaryLight,
                                    textColor = PumTheme.colors.secondaryVariant
                                )
                            } else if (isBorn) {
                                InfoChip(
                                    text = "출산 완료",
                                    containerColor = PumTheme.colors.secondaryLight,
                                    textColor = PumTheme.colors.secondaryVariant
                                )
                            }
                        }
                    }
                }

                TextButton(onClick = onEditProfile) {
                    Text(
                        text = "편집",
                        fontSize = 13.sp,
                        color = PumTheme.colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 임신 정보 섹션
        MenuSection(title = "임신 정보") {
            MenuRow(emoji = "📅", label = "출산 예정일 변경", onClick = onEditProfile)
            HorizontalDivider(
                modifier = Modifier.padding(start = 52.dp),
                color = PumTheme.colors.outline
            )
            MenuRow(
                emoji = "✅",
                label = "주차별 체크리스트",
                onClick = onNavigateToWeeklyChecklist
            )
        }

        // 설정 섹션
        MenuSection(title = "설정") {
            DarkModeRow(isDarkMode = isDarkMode, onToggle = onToggleDarkMode)
        }

        // 정보 섹션
        MenuSection(title = "정보") {
            MenuRow(emoji = "ℹ️", label = "앱 정보", onClick = onNavigateToAppInfo)
        }
    }
}

@Composable
private fun MenuSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PumTheme.colors.onSurfaceSubtle,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PumTheme.colors.surface),
            content = content
        )
    }
}

@Composable
private fun MenuRow(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = emoji, fontSize = 18.sp, modifier = Modifier.size(24.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            color = PumTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 22.sp,
            color = PumTheme.colors.onSurfaceSubtle
        )
    }
}

@Composable
private fun DarkModeRow(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "🌙", fontSize = 18.sp, modifier = Modifier.size(24.dp))
        Text(
            text = "다크 모드",
            fontSize = 15.sp,
            color = PumTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkMode,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PumTheme.colors.onPrimary,
                checkedTrackColor = PumTheme.colors.primary,
                uncheckedThumbColor = PumTheme.colors.onPrimary,
                uncheckedTrackColor = PumTheme.colors.outline
            )
        )
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

// ─── 편집 화면 ───────────────────────────────────────────────────────────────────

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

        FieldLabel(if (state.editIsBorn) "아기 생일" else "출산 예정일")
        Spacer(modifier = Modifier.height(8.dp))
        DateSelectButton(
            dateMillis = state.editDateMillis,
            isBorn = state.editIsBorn,
            onClick = onShowDatePicker
        )

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
