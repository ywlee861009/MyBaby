package com.mybaby.app.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.data.ChecklistRepository
import com.mybaby.app.core.model.ChecklistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*

data class WeeklyChecklistState(
    val isLoading: Boolean = true,
    val selectedWeek: Int = 1,
    val currentWeek: Int = 1,
    val items: List<ChecklistItem> = emptyList()
)

sealed interface WeeklyChecklistIntent {
    data class SelectWeek(val week: Int) : WeeklyChecklistIntent
    data class ToggleItem(val id: String, val isChecked: Boolean) : WeeklyChecklistIntent
}

class WeeklyChecklistViewModel(
    private val babyRepository: BabyRepository,
    private val checklistRepository: ChecklistRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeeklyChecklistState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val baby = babyRepository.getBaby().first()
            val currentWeek = calculateCurrentWeek(baby?.dueDate)
            _state.update { it.copy(selectedWeek = currentWeek, currentWeek = currentWeek) }
            loadItems(currentWeek)
        }
    }

    fun handleIntent(intent: WeeklyChecklistIntent) {
        when (intent) {
            is WeeklyChecklistIntent.SelectWeek -> {
                _state.update { it.copy(selectedWeek = intent.week, isLoading = true) }
                loadItems(intent.week)
            }
            is WeeklyChecklistIntent.ToggleItem -> {
                viewModelScope.launch {
                    checklistRepository.updateChecked(intent.id, intent.isChecked)
                }
            }
        }
    }

    private fun loadItems(week: Int) {
        viewModelScope.launch {
            checklistRepository.getItemsByWeek(week).collect { items ->
                _state.update { it.copy(isLoading = false, items = items) }
            }
        }
    }

    private fun calculateCurrentWeek(dueDateMillis: Long?): Int {
        if (dueDateMillis == null) return 1
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dueDate = Instant.fromEpochMilliseconds(dueDateMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysUntilDue = today.daysUntil(dueDate)
        val daysPregnant = 280 - daysUntilDue
        return (daysPregnant / 7).coerceIn(1, 40)
    }
}
