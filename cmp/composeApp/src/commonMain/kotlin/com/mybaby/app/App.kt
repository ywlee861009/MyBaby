package com.mybaby.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.data.LetterRepository
import com.mybaby.app.core.data.ScheduleRepository
import com.mybaby.app.navigation.AppNavigation
import com.mybaby.app.navigation.Screen
import com.mybaby.app.ui.theme.MyBabyTheme
import kotlinx.coroutines.flow.first

@Composable
fun App(
    babyRepository: BabyRepository,
    letterRepository: LetterRepository,
    healthRecordRepository: HealthRecordRepository,
    scheduleRepository: ScheduleRepository,
    onExit: () -> Unit = {}
) {
    MyBabyTheme {
        var startDestination by remember { mutableStateOf<Screen?>(null) }

        LaunchedEffect(Unit) {
            val baby = babyRepository.getBaby().first()
            startDestination = if (baby != null) Screen.Home else Screen.Setup.BabyInfo
        }

        val dest = startDestination
        if (dest != null) {
            AppNavigation(
                startDestination = dest,
                babyRepository = babyRepository,
                letterRepository = letterRepository,
                healthRecordRepository = healthRecordRepository,
                scheduleRepository = scheduleRepository,
                onExit = onExit
            )
        } else {
            // 초기 로딩 중 빈 배경 표시
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
