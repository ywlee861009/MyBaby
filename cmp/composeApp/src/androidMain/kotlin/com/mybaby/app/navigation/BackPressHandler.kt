package com.mybaby.app.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*

@Composable
actual fun ExitOnDoubleBackPress(
    enabled: Boolean,
    onShowWarning: () -> Unit,
    onExit: () -> Unit
) {
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = enabled) {
        val now = System.currentTimeMillis()
        if (now - backPressedTime < 2000L) {
            onExit()
        } else {
            backPressedTime = now
            onShowWarning()
        }
    }
}
