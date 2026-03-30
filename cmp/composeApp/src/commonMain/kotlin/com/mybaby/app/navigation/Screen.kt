package com.mybaby.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable data object Home : Screen()
    @Serializable data object HealthRecord : Screen()
    @Serializable data class HealthRecordAdd(val category: String? = null) : Screen()
    @Serializable data class HealthRecordDetail(val id: String) : Screen()
    @Serializable data class HealthRecordEdit(val id: String) : Screen()
    @Serializable data object WeightChart : Screen()
    @Serializable sealed class Letter : Screen() {
        @Serializable data object List : Letter()
        @Serializable data object Write : Letter()
        @Serializable data class Detail(val id: String) : Letter()
        @Serializable data class Edit(val id: String) : Letter()
    }
    @Serializable data object Schedule : Screen()
    @Serializable data class ScheduleAdd(val dateMillis: Long = 0L) : Screen()
    @Serializable data object More : Screen()
    @Serializable sealed class Setup : Screen() {
        @Serializable data object BabyInfo : Setup()
        @Serializable data class PregnancyInfo(
            val nickname: String,
            val gender: String,
            val isBorn: Boolean
        ) : Setup()
    }
}
