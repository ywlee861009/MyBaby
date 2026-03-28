package com.mybaby.app.feature.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val themeColors = listOf(
    "#FFF8F0",
    "#F0F4FF",
    "#F5FFF0",
    "#FFF5F8",
    "#FFFFF0"
)

@Composable
fun LetterWriteScreen(
    viewModel: LetterWriteViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LetterUiEvent.NavigateBack -> onNavigateBack()
                is LetterUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2D2020),
                    contentColor = Color.White,
                    actionColor = PumTheme.colors.primary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // TopBar
            LetterWriteTopBar(
                onClose = onNavigateBack,
                onSave = { viewModel.handleIntent(LetterWriteIntent.Save) }
            )

            // 편지지 영역
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(parseHexColor(state.selectedTheme)))
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "To. ${state.babyNickname}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PumTheme.colors.onSurface
                    )
                    if (state.weekNumber > 0) {
                        WeekBadge(week = state.weekNumber)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatEpochMillis(Clock.System.now().toEpochMilliseconds()),
                    fontSize = 13.sp,
                    color = PumTheme.colors.onSurfaceSubtle
                )

                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = state.draftContent,
                    onValueChange = { viewModel.handleIntent(LetterWriteIntent.UpdateContent(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 28.sp,
                        color = PumTheme.colors.onSurface
                    ),
                    cursorBrush = SolidColor(PumTheme.colors.primary),
                    decorationBox = { innerTextField ->
                        if (state.draftContent.isEmpty()) {
                            Text(
                                "아기에게 전하고 싶은 이야기를 적어보세요...",
                                fontSize = 15.sp,
                                lineHeight = 28.sp,
                                color = PumTheme.colors.onSurfaceSubtle
                            )
                        }
                        innerTextField()
                    }
                )
            }

            // 테마 선택 영역
            ThemeSelectorArea(
                selectedTheme = state.selectedTheme,
                themes = themeColors,
                label = "편지지 테마",
                swatchSize = 60.dp to 40.dp,
                onThemeSelect = { viewModel.handleIntent(LetterWriteIntent.SelectTheme(it)) }
            )
        }
    }
}

@Composable
private fun LetterWriteTopBar(
    onClose: () -> Unit,
    onSave: () -> Unit
) {
    val colors = PumTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✕",
            fontSize = 20.sp,
            color = colors.onSurface,
            modifier = Modifier.clickable(onClick = onClose)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "편지 쓰기",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "저장",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.primary,
            modifier = Modifier.clickable(onClick = onSave)
        )
    }
    HorizontalDivider(color = PumTheme.colors.outline, thickness = 1.dp)
}

@Composable
internal fun ThemeSelectorArea(
    selectedTheme: String,
    themes: List<String>,
    label: String,
    swatchSize: Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp>,
    onThemeSelect: (String) -> Unit
) {
    val colors = PumTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 1.dp, color = colors.outline, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurfaceSubtle
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            themes.forEach { colorHex ->
                val isSelected = colorHex == selectedTheme
                Box(
                    modifier = Modifier
                        .size(width = swatchSize.first, height = swatchSize.second)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(parseHexColor(colorHex)))
                        .then(
                            if (isSelected) Modifier.border(
                                width = 2.dp,
                                color = colors.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) else Modifier.border(
                                width = 1.dp,
                                color = colors.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                        )
                        .clickable { onThemeSelect(colorHex) }
                )
            }
        }
    }
}

internal fun parseHexColor(hex: String): Long {
    val clean = hex.trimStart('#')
    return when (clean.length) {
        6 -> (0xFF000000L or clean.toLong(16))
        8 -> clean.toLong(16)
        else -> 0xFFFFF8F0L
    }
}
