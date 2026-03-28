package com.mybaby.app.feature.letter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mybaby.app.core.model.Letter
import com.mybaby.app.ui.components.PumButton
import com.mybaby.app.ui.theme.PumTheme

@Composable
fun LetterScreen(
    viewModel: LetterViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("아기에게 보내는 편지", style = PumTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
        ) {
            if (state.isWritingMode) {
                LetterComposeContent(state, viewModel)
            } else {
                LetterListContent(state, viewModel)
            }
        }
    }
}

@Composable
fun LetterListContent(state: LetterState, viewModel: LetterViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(PumTheme.spacing.medium)) {
        if (state.letters.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "아직 작성된 편지가 없어요.\n오늘의 마음을 전해보세요.",
                    style = PumTheme.typography.bodyMedium,
                    color = PumTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(PumTheme.spacing.small)
            ) {
                items(state.letters) { letter ->
                    LetterItem(letter) {
                        viewModel.handleIntent(LetterIntent.EditLetter(letter))
                    }
                }
            }
        }

        if (state.canWriteToday) {
            PumButton(
                text = "오늘의 편지 쓰기",
                onClick = { viewModel.handleIntent(LetterIntent.OpenWriteMode) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LetterComposeContent(state: LetterState, viewModel: LetterViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(PumTheme.spacing.medium)) {
        OutlinedTextField(
            value = state.draftContent,
            onValueChange = { viewModel.handleIntent(LetterIntent.UpdateDraftContent(it)) },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            placeholder = { Text("아기에게 하고 싶은 말을 적어보세요.") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PumTheme.colors.surface,
                unfocusedContainerColor = PumTheme.colors.surface
            )
        )
        
        Spacer(modifier = Modifier.height(PumTheme.spacing.medium))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.handleIntent(LetterIntent.CloseWriteMode) },
                modifier = Modifier.weight(1f)
            ) {
                Text("취소")
            }
            PumButton(
                text = "저장하기",
                onClick = { viewModel.handleIntent(LetterIntent.SaveLetter) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LetterItem(letter: Letter, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PumTheme.colors.surface)
    ) {
        Column(modifier = Modifier.padding(PumTheme.spacing.medium)) {
            Text(
                text = "2026. 03. 28", // TODO: 실제 날짜 포맷팅
                style = PumTheme.typography.labelSmall,
                color = PumTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(PumTheme.spacing.extraSmall))
            Text(
                text = letter.content,
                style = PumTheme.typography.bodyMedium,
                maxLines = 2,
                color = PumTheme.colors.onSurface
            )
        }
    }
}
