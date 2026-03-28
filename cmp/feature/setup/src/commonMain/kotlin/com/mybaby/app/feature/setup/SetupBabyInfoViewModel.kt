package com.mybaby.app.feature.setup

import androidx.lifecycle.ViewModel
import com.mybaby.app.core.model.BabyGender
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class SetupBabyInfoViewModel : ViewModel() {

    private val _state = MutableStateFlow(SetupBabyInfoState())
    val state = _state.asStateFlow()

    private val _events = Channel<SetupBabyInfoEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: SetupBabyInfoIntent) {
        when (intent) {
            is SetupBabyInfoIntent.UpdateNickname ->
                _state.update { it.copy(nickname = intent.nickname) }

            is SetupBabyInfoIntent.SelectGender ->
                _state.update { it.copy(gender = intent.gender) }

            is SetupBabyInfoIntent.SetBornStatus ->
                _state.update { it.copy(isBorn = intent.isBorn) }

            SetupBabyInfoIntent.Next -> {
                val s = _state.value
                if (s.canProceed) {
                    _events.trySend(
                        SetupBabyInfoEvent.NavigateNext(
                            nickname = s.nickname.trim(),
                            gender = s.gender,
                            isBorn = s.isBorn
                        )
                    )
                }
            }
        }
    }
}
