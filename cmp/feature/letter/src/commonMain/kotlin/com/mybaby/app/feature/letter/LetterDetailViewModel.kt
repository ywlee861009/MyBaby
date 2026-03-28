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

class LetterDetailViewModel(
    private val repository: LetterRepository,
    private val letterId: String
) : ViewModel() {

    private val _state = MutableStateFlow(LetterDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<LetterUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeLetter()
    }

    fun handleIntent(intent: LetterDetailIntent) {
        when (intent) {
            is LetterDetailIntent.OpenMenu -> _state.update { it.copy(showMenu = true) }
            is LetterDetailIntent.CloseMenu -> _state.update { it.copy(showMenu = false) }
            is LetterDetailIntent.ShowDeleteDialog -> _state.update { it.copy(showMenu = false, showDeleteDialog = true) }
            is LetterDetailIntent.DismissDeleteDialog -> _state.update { it.copy(showDeleteDialog = false) }
            is LetterDetailIntent.ConfirmDelete -> confirmDelete()
            is LetterDetailIntent.NavigateEdit -> {
                _state.update { it.copy(showMenu = false) }
                viewModelScope.launch { _events.send(LetterUiEvent.NavigateToEdit(letterId)) }
            }
        }
    }

    private fun observeLetter() {
        viewModelScope.launch {
            repository.getLetterById(letterId).collectLatest { letter ->
                _state.update { it.copy(letter = letter, isLoading = false) }
            }
        }
    }

    private fun confirmDelete() {
        viewModelScope.launch {
            _state.update { it.copy(showDeleteDialog = false) }
            repository.deleteLetter(letterId)
            _events.send(LetterUiEvent.NavigateBack)
        }
    }
}
