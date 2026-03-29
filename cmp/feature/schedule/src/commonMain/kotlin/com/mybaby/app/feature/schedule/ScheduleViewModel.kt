package com.mybaby.app.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.ScheduleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class ScheduleViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    private val _events = Channel<ScheduleUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loadJob: Job? = null
    private val tz = TimeZone.currentSystemDefault()

    init {
        val today = Clock.System.now().toLocalDateTime(tz).date
        _state.update {
            it.copy(
                selectedDate = today,
                currentYear = today.year,
                currentMonth = today.monthNumber
            )
        }
        loadMonthSchedules(today.year, today.monthNumber)
    }

    fun handleIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.LoadSchedules -> {
                val s = _state.value
                loadMonthSchedules(s.currentYear, s.currentMonth)
            }
            is ScheduleIntent.SelectDate -> {
                _state.update { it.copy(selectedDate = intent.date) }
                updateSchedulesForSelectedDate(intent.date)
            }
            is ScheduleIntent.PreviousMonth -> navigateMonth(-1)
            is ScheduleIntent.NextMonth -> navigateMonth(1)
            is ScheduleIntent.DeleteSchedule -> deleteSchedule(intent.id)
        }
    }

    private fun navigateMonth(delta: Int) {
        val s = _state.value
        var newMonth = s.currentMonth + delta
        var newYear = s.currentYear
        when {
            newMonth < 1 -> { newMonth = 12; newYear-- }
            newMonth > 12 -> { newMonth = 1; newYear++ }
        }
        _state.update { it.copy(currentYear = newYear, currentMonth = newMonth, isLoading = true) }
        loadMonthSchedules(newYear, newMonth)
    }

    private fun loadMonthSchedules(year: Int, month: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val startMillis = LocalDate(year, month, 1).atStartOfDayIn(tz).toEpochMilliseconds()
            val lastDay = daysInMonth(year, month)
            val endMillis = LocalDate(year, month, lastDay).atStartOfDayIn(tz).toEpochMilliseconds() + 86_400_000L - 1L

            repository.getSchedulesByDateRange(startMillis, endMillis).collectLatest { schedules ->
                val scheduledDays = schedules.map { schedule ->
                    schedule.dateMillis.toLocalDate().dayOfMonth
                }.toSet()

                val selectedDate = _state.value.selectedDate
                val schedulesOnSelected = if (selectedDate != null && selectedDate.year == year && selectedDate.monthNumber == month) {
                    schedules.filter { it.dateMillis.toLocalDate() == selectedDate }
                } else emptyList()

                _state.update {
                    it.copy(
                        isLoading = false,
                        scheduledDays = scheduledDays,
                        schedulesOnSelectedDate = schedulesOnSelected,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private fun updateSchedulesForSelectedDate(date: LocalDate) {
        viewModelScope.launch {
            val startMillis = date.atStartOfDayIn(tz).toEpochMilliseconds()
            val endMillis = startMillis + 86_400_000L - 1L
            repository.getSchedulesByDateRange(startMillis, endMillis).collectLatest { schedules ->
                _state.update { it.copy(schedulesOnSelectedDate = schedules) }
            }
        }
    }

    private fun deleteSchedule(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteSchedule(id)
                _events.send(ScheduleUiEvent.ShowSnackbar("삭제되었습니다"))
            } catch (e: Exception) {
                _events.send(ScheduleUiEvent.ShowSnackbar("오류가 발생했어요. 다시 시도해 주세요"))
            }
        }
    }

    private fun Long.toLocalDate(): LocalDate =
        kotlinx.datetime.Instant.fromEpochMilliseconds(this).toLocalDateTime(tz).date

    private fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> 30
        }
    }
}
