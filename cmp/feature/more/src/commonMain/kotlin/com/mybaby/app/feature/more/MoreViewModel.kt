package com.mybaby.app.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.model.Baby
import com.mybaby.app.core.model.BabyGender
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class MoreViewModel(
    private val babyRepository: BabyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    private val _events = Channel<MoreEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            babyRepository.getBaby().collect { baby ->
                _state.update { it.copy(isLoading = false, baby = baby) }
            }
        }
    }

    fun handleIntent(intent: MoreIntent) {
        when (intent) {
            MoreIntent.StartEdit -> startEdit()
            MoreIntent.CancelEdit -> _state.update { it.copy(isEditing = false) }
            MoreIntent.Save -> save()
            is MoreIntent.UpdateNickname ->
                _state.update { it.copy(editNickname = intent.nickname) }
            is MoreIntent.SelectGender ->
                _state.update { it.copy(editGender = intent.gender) }
            is MoreIntent.SetBornStatus ->
                _state.update {
                    it.copy(
                        editIsBorn = intent.isBorn,
                        editDateMillis = null,
                        pregnancyWeeks = 0,
                        pregnancyDays = 0
                    )
                }
            is MoreIntent.SelectDate -> {
                _state.update { it.copy(editDateMillis = intent.dateMillis) }
                if (!_state.value.editIsBorn) {
                    calculatePregnancyWeeks(intent.dateMillis)
                }
            }
        }
    }

    private fun startEdit() {
        val baby = _state.value.baby ?: return
        val isBorn = baby.birthDate != null
        val dateMillis = if (isBorn) baby.birthDate else baby.dueDate
        val weeks: Int
        val days: Int
        if (!isBorn && dateMillis != null) {
            val pair = calcWeeksAndDays(dateMillis)
            weeks = pair.first
            days = pair.second
        } else {
            weeks = 0
            days = 0
        }
        _state.update {
            it.copy(
                isEditing = true,
                editNickname = baby.nickname,
                editGender = baby.gender,
                editIsBorn = isBorn,
                editDateMillis = dateMillis,
                pregnancyWeeks = weeks,
                pregnancyDays = days
            )
        }
    }

    private fun calculatePregnancyWeeks(dueDateMillis: Long) {
        val pair = calcWeeksAndDays(dueDateMillis)
        _state.update { it.copy(pregnancyWeeks = pair.first, pregnancyDays = pair.second) }
    }

    private fun calcWeeksAndDays(dueDateMillis: Long): Pair<Int, Int> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dueDate = Instant.fromEpochMilliseconds(dueDateMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val daysUntilDue = today.daysUntil(dueDate)
        val daysPregnant = 280 - daysUntilDue
        return Pair(
            (daysPregnant / 7).coerceAtLeast(0),
            (daysPregnant % 7).coerceAtLeast(0)
        )
    }

    private fun save() {
        val s = _state.value
        if (!s.canSave) return
        val existing = s.baby ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val updated = Baby(
                id = existing.id,
                nickname = s.editNickname.trim(),
                gender = s.editGender,
                dueDate = if (!s.editIsBorn) s.editDateMillis else null,
                birthDate = if (s.editIsBorn) s.editDateMillis else null,
                createdAt = existing.createdAt
            )
            babyRepository.saveBaby(updated)
            _state.update { it.copy(isSaving = false, isEditing = false) }
            _events.send(MoreEvent.SaveSuccess)
        }
    }
}
