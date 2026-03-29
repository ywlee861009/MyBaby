package com.mybaby.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.core.model.ScheduleCategory
import com.mybaby.app.ui.components.ChecklistItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(private val babyRepository: BabyRepository) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        handleIntent(HomeIntent.LoadData)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData, HomeIntent.Refresh -> loadData()
            is HomeIntent.ToggleChecklistItem -> toggleChecklistItem(intent.itemId)
            HomeIntent.OnWeightRecordClick -> sendEvent(HomeUiEvent.NavigateToHealthRecord)
            HomeIntent.OnLetterWriteClick -> sendEvent(HomeUiEvent.NavigateToLetterWrite)
            HomeIntent.OnScheduleAddClick -> sendEvent(HomeUiEvent.NavigateToScheduleAdd)
            HomeIntent.OnMoreChecklistClick -> sendEvent(HomeUiEvent.NavigateToMore)
            HomeIntent.OnMoreScheduleClick -> sendEvent(HomeUiEvent.NavigateToSchedule)
            HomeIntent.OnMoreRecordClick -> sendEvent(HomeUiEvent.NavigateToHealthRecord)
        }
    }

    private fun sendEvent(event: HomeUiEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val baby = babyRepository.getBaby().first()
                val nowMillis = Clock.System.now().toEpochMilliseconds()
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val todayLabel = "${today.monthNumber}월 ${today.dayOfMonth}일 ${today.dayOfWeek.korLabel()}"

                val dummyChecklist = listOf(
                    ChecklistItem("cl_1", "엽산 복용하기", true),
                    ChecklistItem("cl_2", "철분제 복용하기", false),
                    ChecklistItem("cl_3", "산부인과 예약 확인하기", false)
                )

                val dummySchedules = listOf(
                    Schedule(
                        id = "sc_1",
                        title = "정기 검진",
                        description = "○○산부인과",
                        dateMillis = nowMillis + 7 * 24 * 3600 * 1000L,
                        category = ScheduleCategory.CHECKUP
                    ),
                    Schedule(
                        id = "sc_2",
                        title = "초음파 검사",
                        description = "태아 성장 확인",
                        dateMillis = nowMillis + 14 * 24 * 3600 * 1000L,
                        category = ScheduleCategory.ULTRASOUND
                    )
                )

                val dummyRecords = listOf(
                    HealthRecord(
                        id = "rc_1",
                        date = nowMillis - 2 * 24 * 3600 * 1000L,
                        weight = 62.5
                    ),
                    HealthRecord(
                        id = "rc_2",
                        date = nowMillis - 9 * 24 * 3600 * 1000L,
                        weight = 62.3
                    )
                )

                val msPerDay = 24L * 3600L * 1000L
                val msPerWeek = 7L * msPerDay
                val dueDate = baby?.dueDate
                val (currentWeek, currentDay, dDay) = if (dueDate != null) {
                    val pregnancyStartMillis = dueDate - 280 * msPerDay
                    val elapsedMillis = nowMillis - pregnancyStartMillis
                    val week = (elapsedMillis / msPerWeek).toInt().coerceIn(0, 40)
                    val day = ((elapsedMillis % msPerWeek) / msPerDay).toInt().coerceIn(0, 6)
                    val daysRemaining = ((dueDate - nowMillis) / msPerDay).toInt().coerceAtLeast(0)
                    Triple(week, day, daysRemaining)
                } else {
                    Triple(0, 0, 0)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        nickname = baby?.nickname ?: "엄마",
                        todayLabel = todayLabel,
                        currentWeek = currentWeek,
                        currentDay = currentDay,
                        dDay = dDay,
                        babySizeDescription = getBabySizeDescription(currentWeek),
                        weeklyChecklist = dummyChecklist,
                        upcomingSchedules = dummySchedules,
                        recentRecords = dummyRecords
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun toggleChecklistItem(itemId: String) {
        _state.update { state ->
            state.copy(
                weeklyChecklist = state.weeklyChecklist.map { item ->
                    if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
                }
            )
        }
    }

    private fun getBabySizeDescription(week: Int): String = when (week) {
        in 1..4 -> "양귀비 씨앗"
        in 5..8 -> "블루베리 (1.5cm)"
        in 9..12 -> "자두 (5cm)"
        in 13..16 -> "복숭아 (10cm)"
        in 17..20 -> "바나나 (15cm)"
        in 21..24 -> "옥수수 (30cm)"
        in 25..28 -> "오이 (35cm)"
        in 29..32 -> "가지 (40cm)"
        in 33..36 -> "파인애플 (45cm)"
        in 37..40 -> "수박 (50cm)"
        else -> "성장 중"
    }
}

private fun DayOfWeek.korLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "월요일"
    DayOfWeek.TUESDAY -> "화요일"
    DayOfWeek.WEDNESDAY -> "수요일"
    DayOfWeek.THURSDAY -> "목요일"
    DayOfWeek.FRIDAY -> "금요일"
    DayOfWeek.SATURDAY -> "토요일"
    DayOfWeek.SUNDAY -> "일요일"
    else -> ""
}
