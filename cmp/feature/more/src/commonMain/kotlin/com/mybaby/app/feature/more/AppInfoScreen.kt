package com.mybaby.app.feature.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mybaby.app.ui.theme.PumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoScreen(onNavigateBack: () -> Unit) {
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
                        "앱 정보",
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
                .background(PumTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 앱 아이콘
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PumTheme.colors.primaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text("🤰", fontSize = 44.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 앱 이름
            Text(
                text = "품 (MyBaby)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PumTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 태그라인
            Text(
                text = "소중한 10개월의 기록",
                fontSize = 14.sp,
                color = PumTheme.colors.onSurfaceSubtle
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 버전
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PumTheme.colors.primaryLight)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "v1.0.0",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PumTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 정보 카드
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PumTheme.colors.surface)
            ) {
                InfoRow(label = "버전", value = "1.0.0")
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = PumTheme.colors.outline
                )
                InfoRow(label = "빌드", value = "2026.03")
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = PumTheme.colors.outline
                )
                InfoRow(label = "플랫폼", value = "Android / iOS")
            }

            Spacer(modifier = Modifier.weight(1f))

            // 저작권
            Text(
                text = "© 2026 MyBaby Team\n모든 권리 보유",
                fontSize = 12.sp,
                color = PumTheme.colors.onSurfaceSubtle,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = PumTheme.colors.onSurfaceSubtle
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PumTheme.colors.onSurface
        )
    }
}
