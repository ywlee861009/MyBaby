package com.mybaby.app.feature.setup

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

class SetupPregnancyInfoViewModel(
    private val babyRepository: BabyRepository,
    nickname: String,
    gender: BabyGender,
    isBorn: Boolean
) : ViewModel() {

    private val _state = MutableStateFlow(
        SetupPregnancyInfoState(
            nickname = nickname,
            gender = gender,
            isBorn = isBorn
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<SetupPregnancyInfoEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: SetupPregnancyInfoIntent) {
        when (intent) {
            is SetupPregnancyInfoIntent.SelectDate -> {
                _state.update { it.copy(selectedDateMillis = intent.dateMillis) }
                if (!_state.value.isBorn) {
                    calculatePregnancyWeeks(intent.dateMillis)
                }
            }
            SetupPregnancyInfoIntent.Save -> save()
        }
    }

    private fun calculatePregnancyWeeks(dueDateMillis: Long) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dueDate = Instant.fromEpochMilliseconds(dueDateMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

        val daysUntilDue = today.daysUntil(dueDate)
        // 임신 전체 기간 = 280일 (40주)
        val daysPregnant = 280 - daysUntilDue
        val weeks = (daysPregnant / 7).coerceAtLeast(0)
        val days = (daysPregnant % 7).coerceAtLeast(0)

        _state.update { it.copy(pregnancyWeeks = weeks, pregnancyDays = days) }
    }

    private fun save() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val s = _state.value
            val baby = Baby(
                id = "baby_1",
                nickname = s.nickname,
                gender = s.gender,
                dueDate = if (!s.isBorn) s.selectedDateMillis else null,
                birthDate = if (s.isBorn) s.selectedDateMillis else null,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            babyRepository.saveBaby(baby)
            _state.update { it.copy(isSaving = false) }
            _events.send(SetupPregnancyInfoEvent.SetupComplete)
        }
    }
}
