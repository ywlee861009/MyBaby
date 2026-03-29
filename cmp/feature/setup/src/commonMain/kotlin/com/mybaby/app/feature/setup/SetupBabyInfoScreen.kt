package com.mybaby.app.feature.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import com.mybaby.app.core.model.BabyGender
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SetupBabyInfoScreen(
    viewModel: SetupBabyInfoViewModel,
    onNavigateNext: (nickname: String, gender: BabyGender, isBorn: Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SetupBabyInfoEvent.NavigateNext ->
                    onNavigateNext(event.nickname, event.gender, event.isBorn)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PumTheme.colors.background)
            .imePadding()
    ) {
        // 스크롤 가능한 컨텐츠 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 일러스트 영역
            BabyIllustration()

            Spacer(modifier = Modifier.height(32.dp))

            // 타이틀
            Text(
                text = "아기에 대해\n알려주세요",
                style = PumTheme.typography.headlineLarge,
                color = PumTheme.colors.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "나중에 언제든지 변경할 수 있어요",
                style = PumTheme.typography.bodyMedium,
                color = PumTheme.colors.onSurfaceSubtle
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 태명 입력
            SectionLabel("아기 태명")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.nickname,
                onValueChange = { viewModel.handleIntent(SetupBabyInfoIntent.UpdateNickname(it)) },
                placeholder = {
                    Text(
                        text = "예: 콩이, 열무, 별님",
                        color = PumTheme.colors.onSurfaceSubtle
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PumTheme.colors.primary,
                    unfocusedBorderColor = PumTheme.colors.outline,
                    focusedContainerColor = PumTheme.colors.surface,
                    unfocusedContainerColor = PumTheme.colors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 성별 선택
            SectionLabel("성별")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BabyGender.entries.forEach { gender ->
                    GenderChip(
                        label = gender.label(),
                        isSelected = state.gender == gender,
                        onClick = { viewModel.handleIntent(SetupBabyInfoIntent.SelectGender(gender)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 현재 상태
            SectionLabel("현재 상태")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatusChip(
                    label = "임신 중",
                    isSelected = !state.isBorn,
                    onClick = { viewModel.handleIntent(SetupBabyInfoIntent.SetBornStatus(false)) },
                    modifier = Modifier.weight(1f)
                )
                StatusChip(
                    label = "이미 출산",
                    isSelected = state.isBorn,
                    onClick = { viewModel.handleIntent(SetupBabyInfoIntent.SetBornStatus(true)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 다음 버튼 — 키보드 위에 고정
        Button(
            onClick = { viewModel.handleIntent(SetupBabyInfoIntent.Next) },
            enabled = state.canProceed,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PumTheme.colors.primary,
                disabledContainerColor = PumTheme.colors.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .height(56.dp)
        ) {
            Text(
                text = "다음",
                style = PumTheme.typography.labelLarge,
                color = if (state.canProceed) PumTheme.colors.onPrimary else PumTheme.colors.onSurfaceSubtle
            )
        }
    }
}

@Composable
private fun BabyIllustration() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(PumTheme.colors.primaryLight),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🍼", fontSize = 40.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = PumTheme.typography.labelLarge,
        color = PumTheme.colors.onSurface,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GenderChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) PumTheme.colors.primaryLight else PumTheme.colors.surface
    val borderColor = if (isSelected) PumTheme.colors.primary else PumTheme.colors.outline
    val textColor = if (isSelected) PumTheme.colors.primary else PumTheme.colors.onSurfaceSubtle

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
private fun StatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) PumTheme.colors.secondaryLight else PumTheme.colors.surface
    val borderColor = if (isSelected) PumTheme.colors.secondaryVariant else PumTheme.colors.outline
    val textColor = if (isSelected) PumTheme.colors.secondaryVariant else PumTheme.colors.onSurfaceSubtle

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
