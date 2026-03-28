package com.mybaby.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mybaby.app.ui.components.PumButton
import com.mybaby.app.ui.theme.PumTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToRecord: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PumTheme.colors.background)
            .padding(PumTheme.spacing.medium)
    ) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PumTheme.colors.primary)
            }
        } else {
            state.babyStatus?.let { status ->
                DashboardHeader(status)
                Spacer(modifier = Modifier.height(PumTheme.spacing.large))
                StatusCard(status)
                Spacer(modifier = Modifier.weight(1f))
                PumButton(
                    text = "오늘 기록하기",
                    onClick = onNavigateToRecord,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun DashboardHeader(status: com.mybaby.app.core.model.BabyStatus) {
    Column {
        Text(
            text = "${status.weeks}주 ${status.days}일째",
            style = PumTheme.typography.headlineLarge,
            color = PumTheme.colors.onBackground
        )
        Text(
            text = "출산까지 D-${status.dDay}",
            style = PumTheme.typography.headlineSmall,
            color = PumTheme.colors.primary
        )
    }
}

@Composable
fun StatusCard(status: com.mybaby.app.core.model.BabyStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PumTheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(PumTheme.spacing.medium)) {
            Text(
                text = status.title,
                style = PumTheme.typography.headlineSmall,
                color = PumTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(PumTheme.spacing.small))
            Text(
                text = status.message,
                style = PumTheme.typography.bodyMedium,
                color = PumTheme.colors.onSurface
            )
        }
    }
}
