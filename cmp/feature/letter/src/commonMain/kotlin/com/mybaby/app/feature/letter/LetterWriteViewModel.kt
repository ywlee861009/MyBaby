package com.mybaby.app.feature.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.LetterRepository
import com.mybaby.app.core.model.Letter
import com.mybaby.app.core.model.SyncStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class LetterWriteViewModel(
    private val repository: LetterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LetterWriteState())
    val state = _state.asStateFlow()

    private val _events = Channel<LetterUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: LetterWriteIntent) {
        when (intent) {
            is LetterWriteIntent.UpdateContent -> _state.update { it.copy(draftContent = intent.content) }
            is LetterWriteIntent.SelectTheme -> _state.update { it.copy(selectedTheme = intent.color) }
            is LetterWriteIntent.Save -> saveLetter()
        }
    }

    private fun saveLetter() {
        val content = _state.value.draftContent
        if (content.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val now = Clock.System.now().toEpochMilliseconds()
            val letter = Letter(
                id = "letter_${now}_${Random.nextInt(1000)}",
                content = content,
                createdAt = now,
                updatedAt = now,
                weekNumber = _state.value.weekNumber,
                themeColor = _state.value.selectedTheme,
                syncStatus = SyncStatus.PENDING
            )
            repository.saveLetter(letter)
            _state.update { it.copy(isSaving = false) }
            _events.send(LetterUiEvent.ShowSnackbar("편지가 저장되었습니다"))
            _events.send(LetterUiEvent.NavigateBack)
        }
    }
}
