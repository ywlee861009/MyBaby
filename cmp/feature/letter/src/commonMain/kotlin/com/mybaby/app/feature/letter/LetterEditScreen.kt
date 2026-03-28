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

private val editThemeColors = listOf(
    "#FFF8F0",
    "#F0F4FF",
    "#F5FFF0",
    "#FFF5F8",
    "#FFFFF0"
)

@Composable
fun LetterEditScreen(
    viewModel: LetterEditViewModel,
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
        ) {
            // TopBar
            LetterEditTopBar(
                onClose = onNavigateBack,
                onSave = { viewModel.handleIntent(LetterEditIntent.Save) }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PumTheme.colors.primary)
                }
            } else {
                val letter = state.letter

                // 편지지 영역
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(parseHexColor(state.selectedTheme)))
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        if ((letter?.weekNumber ?: 0) > 0) {
                            WeekBadge(week = letter!!.weekNumber)
                        }
                    }

                    letter?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formatEpochMillis(it.createdAt),
                                fontSize = 13.sp,
                                color = PumTheme.colors.onSurfaceSubtle
                            )
                        }
                    }

                    HorizontalDivider(color = PumTheme.colors.outline, thickness = 1.dp)

                    // 텍스트 입력 영역
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PumTheme.colors.surface)
                            .border(
                                width = 1.5.dp,
                                color = PumTheme.colors.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = state.draftContent,
                            onValueChange = { viewModel.handleIntent(LetterEditIntent.UpdateContent(it)) },
                            modifier = Modifier.fillMaxSize(),
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 28.sp,
                                color = PumTheme.colors.onSurface
                            ),
                            cursorBrush = SolidColor(PumTheme.colors.primary),
                            decorationBox = { innerTextField ->
                                if (state.draftContent.isEmpty()) {
                                    Text(
                                        "내용을 입력하세요...",
                                        fontSize = 15.sp,
                                        lineHeight = 28.sp,
                                        color = PumTheme.colors.onSurfaceSubtle
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                // 테마 선택 영역
                ThemeSelectorArea(
                    selectedTheme = state.selectedTheme,
                    themes = editThemeColors,
                    label = "편지지 배경",
                    swatchSize = 52.dp to 52.dp,
                    onThemeSelect = { viewModel.handleIntent(LetterEditIntent.SelectTheme(it)) }
                )
            }
        }
    }
}

@Composable
private fun LetterEditTopBar(
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
            text = "편지 편집",
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
    HorizontalDivider(color = colors.outline, thickness = 1.dp)
}
