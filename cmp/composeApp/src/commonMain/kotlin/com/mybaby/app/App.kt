package com.mybaby.app

import androidx.compose.runtime.Composable
import com.mybaby.app.core.data.LetterRepository
import com.mybaby.app.navigation.AppNavigation
import com.mybaby.app.ui.theme.MyBabyTheme

@Composable
fun App(
    letterRepository: LetterRepository,
    onExit: () -> Unit = {}
) {
    MyBabyTheme {
        AppNavigation(letterRepository = letterRepository, onExit = onExit)
    }
}
