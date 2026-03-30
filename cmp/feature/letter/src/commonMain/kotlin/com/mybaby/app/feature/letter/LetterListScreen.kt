package com.mybaby.app.feature.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.Letter
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterListScreen(
    viewModel: LetterListViewModel,
    onNavigateToWrite: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LetterUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "아기에게 보내는 편지",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.surface
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWrite,
                containerColor = PumTheme.colors.primary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Text("✏", fontSize = 20.sp)
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
        ) {
            if (state.letters.isNotEmpty()) {
                Text(
                    text = "${state.letters.size}통의 편지",
                    color = PumTheme.colors.onSurfaceSubtle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PumTheme.colors.primary)
                }
            } else if (state.letters.isEmpty()) {
                LetterEmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.letters) { letter ->
                        LetterCard(
                            letter = letter,
                            onClick = { onNavigateToDetail(letter.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterCard(
    letter: Letter,
    onClick: () -> Unit
) {
    val colors = PumTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (letter.weekNumber > 0) {
                    WeekBadge(week = letter.weekNumber)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatEpochMillis(letter.createdAt),
                    color = colors.onSurfaceSubtle,
                    fontSize = 12.sp
                )
            }
            Text(
                text = letter.content,
                color = colors.onSurface,
                fontSize = 14.sp,
                maxLines = 2,
                lineHeight = 20.sp
            )
            Text(
                text = "편지 보기 →",
                color = colors.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun WeekBadge(week: Int) {
    val colors = PumTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.secondaryLight)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "${week}주차",
            color = colors.secondaryVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LetterEmptyState(modifier: Modifier = Modifier) {
    val colors = PumTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📭", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "아직 편지가 없어요",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "FAB을 눌러 첫 편지를 써보세요",
            fontSize = 14.sp,
            color = colors.onSurfaceSubtle
        )
    }
}

internal fun formatEpochMillis(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = localDateTime.year
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    return "$year. $month. $day"
}
