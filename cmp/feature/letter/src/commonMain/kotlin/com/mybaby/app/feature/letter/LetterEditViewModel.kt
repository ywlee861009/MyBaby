package com.mybaby.app.feature.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.data.LetterRepository
import com.mybaby.app.core.model.SyncStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class LetterEditViewModel(
    private val repository: LetterRepository,
    private val babyRepository: BabyRepository,
    private val letterId: String
) : ViewModel() {

    private val _state = MutableStateFlow(LetterEditState())
    val state = _state.asStateFlow()

    private val _events = Channel<LetterUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeLetter()
        viewModelScope.launch {
            babyRepository.getBaby().collectLatest { baby ->
                baby?.nickname?.let { nick ->
                    _state.update { it.copy(babyNickname = nick) }
                }
            }
        }
    }

    fun handleIntent(intent: LetterEditIntent) {
        when (intent) {
            is LetterEditIntent.UpdateContent -> _state.update { it.copy(draftContent = intent.content) }
            is LetterEditIntent.SelectTheme -> _state.update { it.copy(selectedTheme = intent.color) }
            is LetterEditIntent.Save -> saveLetter()
        }
    }

    private fun observeLetter() {
        viewModelScope.launch {
            repository.getLetterById(letterId).collectLatest { letter ->
                if (letter != null && _state.value.isLoading) {
                    _state.update {
                        it.copy(
                            letter = letter,
                            draftContent = letter.content,
                            selectedTheme = letter.themeColor,
                            isLoading = false
                        )
                    }
                } else if (!_state.value.isLoading) {
                    _state.update { it.copy(letter = letter) }
                }
            }
        }
    }

    private fun saveLetter() {
        val currentState = _state.value
        val content = currentState.draftContent
        if (content.isBlank()) return
        val original = currentState.letter ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val updated = original.copy(
                content = content,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                themeColor = currentState.selectedTheme,
                syncStatus = SyncStatus.PENDING
            )
            repository.saveLetter(updated)
            _state.update { it.copy(isSaving = false) }
            _events.send(LetterUiEvent.ShowSnackbar("편지가 수정되었습니다"))
            _events.send(LetterUiEvent.NavigateBack)
        }
    }
}
