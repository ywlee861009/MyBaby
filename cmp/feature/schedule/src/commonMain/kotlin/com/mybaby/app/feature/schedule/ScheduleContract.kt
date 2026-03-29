package com.mybaby.app.feature.schedule

import com.mybaby.app.core.model.NotificationTiming
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.core.model.ScheduleCategory
import kotlinx.datetime.LocalDate

// ─── 공통 UiEvent ──────────────────────────────────────────────────────────────

sealed interface ScheduleUiEvent {
    data class ShowSnackbar(val message: String) : ScheduleUiEvent
    data object NavigateBack : ScheduleUiEvent
}

// ─── 일정 목록 (달력) ─────────────────────────────────────────────────────────

data class ScheduleState(
    val isLoading: Boolean = true,
    val selectedDate: LocalDate? = null,      // 선택된 날짜
    val currentYear: Int = 0,
    val currentMonth: Int = 0,               // 1~12
    val scheduledDays: Set<Int> = emptySet(), // 일정 있는 날짜 (dayOfMonth)
    val schedulesOnSelectedDate: List<Schedule> = emptyList(),
    val errorMessage: String? = null
)

sealed interface ScheduleIntent {
    data object LoadSchedules : ScheduleIntent
    data class SelectDate(val date: LocalDate) : ScheduleIntent
    data object PreviousMonth : ScheduleIntent
    data object NextMonth : ScheduleIntent
    data class DeleteSchedule(val id: String) : ScheduleIntent
}

// ─── 일정 추가 ──────────────────────────────────────────────────────────────────

data class ScheduleAddState(
    val isSaving: Boolean = false,
    val title: String = "",
    val description: String = "",
    val dateMillis: Long = 0L,
    val location: String = "",
    val selectedCategory: ScheduleCategory = ScheduleCategory.CHECKUP,
    val selectedNotification: NotificationTiming = NotificationTiming.NONE,
    val errorMessage: String? = null
)

sealed interface ScheduleAddIntent {
    data class SetTitle(val value: String) : ScheduleAddIntent
    data class SetDescription(val value: String) : ScheduleAddIntent
    data class SetLocation(val value: String) : ScheduleAddIntent
    data class SetCategory(val category: ScheduleCategory) : ScheduleAddIntent
    data class SetNotification(val timing: NotificationTiming) : ScheduleAddIntent
    data object Save : ScheduleAddIntent
    data object RequestBack : ScheduleAddIntent
}
