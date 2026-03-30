package com.mybaby.app.feature.record

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightChartScreen(
    viewModel: WeightChartViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                        "체중 변화",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PumTheme.colors.surface
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PumTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PumTheme.colors.primary)
                    }
                }
                state.records.isEmpty() -> {
                    EmptyChartState()
                }
                else -> {
                    WeightChartCard(records = state.records)
                    WeightStatsCard(records = state.records)
                }
            }
        }
    }
}

@Composable
private fun WeightChartCard(records: List<HealthRecord>) {
    val colors = PumTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
            .background(colors.surface, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "체중 변화 추이",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )

            // Simple Chart
            WeightLineChart(
                records = records,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = "가장 최근 기록: ${records.last().weightKg}kg",
                fontSize = 13.sp,
                color = colors.onSurfaceSubtle,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun WeightLineChart(
    records: List<HealthRecord>,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors
    val primaryColor = colors.primary
    val gridColor = colors.outline

    Canvas(modifier = modifier) {
        if (records.size < 2) return@Canvas

        val minWeight = records.minOf { it.weightKg ?: 0.0 } - 2.0
        val maxWeight = records.maxOf { it.weightKg ?: 0.0 } + 2.0
        val weightRange = maxWeight - minWeight

        val width = size.width
        val height = size.height
        val pointSpacing = width / (records.size - 1)

        // Draw horizontal grid lines (3 lines)
        val gridLines = 3
        for (i in 0 until gridLines) {
            val y = height - (i * height / (gridLines - 1))
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Prepare points
        val points = records.mapIndexed { index, record ->
            val x = index * pointSpacing
            val y = height - ((record.weightKg!! - minWeight) / weightRange * height).toFloat()
            Offset(x, y)
        }

        // Draw the line path
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                // Bezier curve would be nice, but simple line first
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw dots at points
        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
private fun WeightStatsCard(records: List<HealthRecord>) {
    val colors = PumTheme.colors
    val firstWeight = records.first().weightKg ?: 0.0
    val lastWeight = records.last().weightKg ?: 0.0
    val diff = lastWeight - firstWeight

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x1A000000))
            .background(colors.surface, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("기록 시작일로부터", fontSize = 13.sp, color = colors.onSurfaceSubtle)
                val diffStr = if (diff >= 0) "+${"%.1f".format(diff)}kg" else "${"%.1f".format(diff)}kg"
                Text(
                    text = diffStr,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (diff >= 0) colors.primary else Color(0xFF4A90D9)
                )
            }

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("현재 체중", fontSize = 13.sp, color = colors.onSurfaceSubtle)
                Text("${lastWeight}kg", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface)
            }
        }
    }
}

@Composable
private fun EmptyChartState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PumTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📈", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "체중 기록이 2개 이상 필요해요",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = PumTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "체중을 꾸준히 기록해 보세요",
                fontSize = 13.sp,
                color = PumTheme.colors.onSurfaceSubtle
            )
        }
    }
}
