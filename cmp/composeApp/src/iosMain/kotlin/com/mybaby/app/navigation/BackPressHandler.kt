package com.mybaby.app.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun ExitOnDoubleBackPress(
    enabled: Boolean,
    onShowWarning: () -> Unit,
    onExit: () -> Unit
) { /* iOS는 뒤로가기 버튼 없음 */ }
