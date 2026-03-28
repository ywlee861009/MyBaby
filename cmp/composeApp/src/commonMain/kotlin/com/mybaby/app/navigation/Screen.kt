package com.mybaby.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable data object Home : Screen()
    @Serializable data object HealthRecord : Screen()
    @Serializable sealed class Letter : Screen() {
        @Serializable data object List : Letter()
        @Serializable data object Write : Letter()
        @Serializable data class Detail(val id: String) : Letter()
        @Serializable data class Edit(val id: String) : Letter()
    }
    @Serializable data object Schedule : Screen()
    @Serializable data object More : Screen()
}
