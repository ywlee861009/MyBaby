package com.mybaby.app

import androidx.compose.runtime.Composable
import com.mybaby.app.navigation.AppNavigation
import com.mybaby.app.ui.theme.MyBabyTheme

@Composable
fun App() {
    MyBabyTheme {
        AppNavigation()
    }
}
