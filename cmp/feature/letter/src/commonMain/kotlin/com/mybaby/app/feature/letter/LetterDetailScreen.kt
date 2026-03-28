package com.mybaby.app.feature.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterDetailScreen(
    viewModel: LetterDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LetterUiEvent.NavigateBack -> onNavigateBack()
                is LetterUiEvent.NavigateToEdit -> onNavigateToEdit(event.id)
                is LetterUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
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
                        "편지 상세",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                actions = {
                    Box {
                        Text(
                            text = "⋮",
                            fontSize = 22.sp,
                            color = PumTheme.colors.onSurface,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable { viewModel.handleIntent(LetterDetailIntent.OpenMenu) }
                        )
                        DropdownMenu(
                            expanded = state.showMenu,
                            onDismissRequest = { viewModel.handleIntent(LetterDetailIntent.CloseMenu) }
                        ) {
                            DropdownMenuItem(
                                text = { Text("편집") },
                                onClick = { viewModel.handleIntent(LetterDetailIntent.NavigateEdit) }
                            )
                            DropdownMenuItem(
                                text = { Text("삭제", color = PumTheme.colors.error) },
                                onClick = { viewModel.handleIntent(LetterDetailIntent.ShowDeleteDialog) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.surface
                )
            )
        },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PumTheme.colors.primary
                )
            } else {
                state.letter?.let { letter ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(parseHexColor(letter.themeColor))
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            Text(
                                "To. 콩이",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PumTheme.colors.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = formatEpochMillis(letter.createdAt),
                                    fontSize = 13.sp,
                                    color = PumTheme.colors.onSurfaceSubtle
                                )
                                if (letter.weekNumber > 0) {
                                    WeekBadge(week = letter.weekNumber)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(
                                color = PumTheme.colors.outline,
                                thickness = 1.dp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = letter.content,
                                fontSize = 15.sp,
                                lineHeight = 28.sp,
                                color = PumTheme.colors.onSurface
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "From. 엄마",
                                fontSize = 14.sp,
                                color = PumTheme.colors.onSurfaceSubtle,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }

    // 삭제 확인 다이얼로그
    if (state.showDeleteDialog) {
        DeleteLetterDialog(
            onDismiss = { viewModel.handleIntent(LetterDetailIntent.DismissDeleteDialog) },
            onConfirm = { viewModel.handleIntent(LetterDetailIntent.ConfirmDelete) }
        )
    }
}

@Composable
private fun DeleteLetterDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = PumTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "편지를 삭제할까요?",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                "삭제한 편지는 복구할 수 없어요.",
                color = colors.onSurfaceSubtle
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = colors.primaryLight,
                    contentColor = colors.primary
                )
            ) {
                Text("취소")
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = colors.error,
                    contentColor = Color.White
                )
            ) {
                Text("삭제")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = colors.surface
    )
}
