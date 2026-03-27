package com.mybaby.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable data object Home : Screen()
    @Serializable data object HealthRecord : Screen()
    @Serializable data object Letter : Screen()
    @Serializable data object Schedule : Screen()
    @Serializable data object More : Screen()
}
