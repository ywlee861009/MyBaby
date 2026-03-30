package com.mybaby.app.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.HealthRecordRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HealthRecordDetailViewModel(
    private val repository: HealthRecordRepository,
    private val recordId: String
) : ViewModel() {

    private val _state = MutableStateFlow(HealthRecordDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecordUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.getRecordById(recordId).collectLatest { record ->
                _state.update { it.copy(record = record, isLoading = false) }
            }
        }
    }

    fun handleIntent(intent: HealthRecordDetailIntent) {
        when (intent) {
            is HealthRecordDetailIntent.OpenMenu -> _state.update { it.copy(showMenu = true) }
            is HealthRecordDetailIntent.CloseMenu -> _state.update { it.copy(showMenu = false) }
            is HealthRecordDetailIntent.ShowDeleteDialog -> _state.update { it.copy(showMenu = false, showDeleteDialog = true) }
            is HealthRecordDetailIntent.DismissDeleteDialog -> _state.update { it.copy(showDeleteDialog = false) }
            is HealthRecordDetailIntent.ConfirmDelete -> confirmDelete()
            is HealthRecordDetailIntent.NavigateEdit -> {
                _state.update { it.copy(showMenu = false) }
                viewModelScope.launch { _events.send(RecordUiEvent.NavigateToEdit(recordId)) }
            }
        }
    }

    private fun confirmDelete() {
        viewModelScope.launch {
            _state.update { it.copy(showDeleteDialog = false) }
            try {
                repository.deleteRecord(recordId)
                _events.send(RecordUiEvent.NavigateBack)
            } catch (e: Exception) {
                _events.send(RecordUiEvent.ShowSnackbar("삭제 중 오류가 발생했어요"))
            }
        }
    }
}
