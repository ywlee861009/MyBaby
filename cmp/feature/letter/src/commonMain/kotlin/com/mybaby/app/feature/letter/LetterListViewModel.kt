package com.mybaby.app.feature.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.LetterRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LetterListViewModel(
    private val repository: LetterRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LetterListState())
    val state = _state.asStateFlow()

    private val _events = Channel<LetterUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeLetters()
        checkCanWriteToday()
    }

    fun handleIntent(intent: LetterListIntent) {
        when (intent) {
            is LetterListIntent.LoadLetters -> { /* Flow 관찰 중 */ }
            is LetterListIntent.DeleteLetter -> deleteLetter(intent.id)
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

    private fun deleteLetter(id: String) {
        viewModelScope.launch {
            repository.deleteLetter(id)
            checkCanWriteToday()
        }
    }
}
