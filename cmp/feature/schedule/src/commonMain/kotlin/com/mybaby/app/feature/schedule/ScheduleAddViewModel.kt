package com.mybaby.app.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.ScheduleRepository
import com.mybaby.app.core.model.NotificationTiming
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.core.model.ScheduleCategory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

class ScheduleAddViewModel(
    private val repository: ScheduleRepository,
    initialDate: LocalDate? = null
) : ViewModel() {

    private val tz = TimeZone.currentSystemDefault()

    private val _state = MutableStateFlow(
        ScheduleAddState(
            dateMillis = (initialDate ?: Clock.System.now().toLocalDateTime(tz).date)
                .atStartOfDayIn(tz).toEpochMilliseconds()
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<ScheduleUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: ScheduleAddIntent) {
        when (intent) {
            is ScheduleAddIntent.SetTitle -> _state.update { it.copy(title = intent.value) }
            is ScheduleAddIntent.SetDescription -> _state.update { it.copy(description = intent.value) }
            is ScheduleAddIntent.SetLocation -> _state.update { it.copy(location = intent.value) }
            is ScheduleAddIntent.SetCategory -> _state.update { it.copy(selectedCategory = intent.category) }
            is ScheduleAddIntent.SetNotification -> _state.update { it.copy(selectedNotification = intent.timing) }
            is ScheduleAddIntent.Save -> saveSchedule()
            is ScheduleAddIntent.RequestBack -> viewModelScope.launch {
                _events.send(ScheduleUiEvent.NavigateBack)
            }
        }
    }

    private fun saveSchedule() {
        val s = _state.value
        if (s.title.isBlank()) {
            _state.update { it.copy(errorMessage = "제목을 입력해 주세요") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val schedule = Schedule(
                    id = "sch_${now}_${Random.nextInt(9999)}",
                    title = s.title,
                    description = s.description,
                    dateMillis = s.dateMillis,
                    location = s.location.takeIf { it.isNotBlank() },
                    category = s.selectedCategory,
                    notification = s.selectedNotification,
                    isCompleted = false,
                    createdAt = now,
                    updatedAt = now
                )
                repository.saveSchedule(schedule)
                _events.send(ScheduleUiEvent.ShowSnackbar("일정이 추가되었습니다"))
                _events.send(ScheduleUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _events.send(ScheduleUiEvent.ShowSnackbar("오류가 발생했어요. 다시 시도해 주세요"))
            }
        }
    }
}
