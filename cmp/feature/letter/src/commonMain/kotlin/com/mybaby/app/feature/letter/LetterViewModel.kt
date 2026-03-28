package com.mybaby.app.feature.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.LetterRepository
import com.mybaby.app.core.model.Letter
import com.mybaby.app.core.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class LetterViewModel(
    private val repository: LetterRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LetterState())
    val state = _state.asStateFlow()

    init {
        observeLetters()
        checkCanWriteToday()
    }

    fun handleIntent(intent: LetterIntent) {
        when (intent) {
            is LetterIntent.LoadLetters -> { /* Flow를 관찰 중이므로 별도 로드 불필요 */ }
            is LetterIntent.OpenWriteMode -> openWriteMode()
            is LetterIntent.CloseWriteMode -> closeWriteMode()
            is LetterIntent.UpdateDraftContent -> updateDraftContent(intent.content)
            is LetterIntent.SaveLetter -> saveLetter()
            is LetterIntent.EditLetter -> editLetter(intent.letter)
        }
    }

    private fun observeLetters() {
        viewModelScope.launch {
            repository.getAllLetters().collectLatest { letters ->
                _state.update { it.copy(letters = letters, isLoading = false) }
            }
        }
    }

    private fun checkCanWriteToday() {
        viewModelScope.launch {
            val canWrite = repository.canWriteToday()
            _state.update { it.copy(canWriteToday = canWrite) }
        }
    }

    private fun openWriteMode() {
        _state.update { it.copy(isWritingMode = true, currentLetterId = null, draftContent = "") }
    }

    private fun closeWriteMode() {
        _state.update { it.copy(isWritingMode = false, currentLetterId = null, draftContent = "") }
    }

    private fun updateDraftContent(content: String) {
        _state.update { it.copy(draftContent = content) }
    }

    private fun saveLetter() {
        viewModelScope.launch {
            val currentContent = _state.value.draftContent
            if (currentContent.isBlank()) return@launch

            _state.update { it.copy(isLoading = true) }

            val now = Clock.System.now().toEpochMilliseconds()
            val newLetter = Letter(
                id = _state.value.currentLetterId ?: generateUniqueId(),
                content = currentContent,
                createdAt = now,
                updatedAt = now,
                syncStatus = SyncStatus.PENDING
            )

            repository.saveLetter(newLetter)
            checkCanWriteToday()

            _state.update { it.copy(
                isLoading = false,
                isWritingMode = false,
                currentLetterId = null,
                draftContent = ""
            ) }
        }
    }

    private fun editLetter(letter: Letter) {
        _state.update { it.copy(
            isWritingMode = true,
            currentLetterId = letter.id,
            draftContent = letter.content
        ) }
    }

    private fun generateUniqueId(): String {
        return "letter_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000)}"
    }
}
