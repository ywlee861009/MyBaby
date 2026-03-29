package com.mybaby.app.feature.home

import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.ui.components.ChecklistItem

data class HomeState(
    val isLoading: Boolean = true,
    val nickname: String = "",
    val todayLabel: String = "",
    val currentWeek: Int = 0,
    val currentDay: Int = 0,
    val dDay: Int = 0,
    val babySizeDescription: String = "",
    val weeklyChecklist: List<ChecklistItem> = emptyList(),
    val upcomingSchedules: List<Schedule> = emptyList(),
    val recentRecords: List<HealthRecord> = emptyList(),
    val error: String? = null
)

sealed interface HomeIntent {
    object LoadData : HomeIntent
    object Refresh : HomeIntent
    data class ToggleChecklistItem(val itemId: String) : HomeIntent
    object OnWeightRecordClick : HomeIntent
    object OnLetterWriteClick : HomeIntent
    object OnScheduleAddClick : HomeIntent
    object OnMoreChecklistClick : HomeIntent
    object OnMoreScheduleClick : HomeIntent
    object OnMoreRecordClick : HomeIntent
}

sealed interface HomeUiEvent {
    object NavigateToLetterWrite : HomeUiEvent
    object NavigateToScheduleAdd : HomeUiEvent
    object NavigateToMore : HomeUiEvent
    object NavigateToHealthRecord : HomeUiEvent
    object NavigateToSchedule : HomeUiEvent
}
