package com.mybaby.app.navigation

import androidx.compose.runtime.Composable

@Composable
expect fun ExitOnDoubleBackPress(
    enabled: Boolean,
    onShowWarning: () -> Unit,
    onExit: () -> Unit
)
