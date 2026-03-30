package com.mybaby.app.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.model.RecordCategory
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HealthRecordListViewModel(
    private val repository: HealthRecordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HealthRecordListState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecordUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var observeJob: Job? = null

    init {
        observeRecords(RecordCategory.ALL)
    }

    fun handleIntent(intent: HealthRecordListIntent) {
        when (intent) {
            is HealthRecordListIntent.SelectCategory -> {
                _state.update { it.copy(selectedCategory = intent.category, isLoading = true) }
                observeRecords(intent.category)
            }
            is HealthRecordListIntent.DeleteRecord -> deleteRecord(intent.id)
            is HealthRecordListIntent.OpenDetail -> viewModelScope.launch {
                _events.send(RecordUiEvent.NavigateToDetail(intent.id))
            }
        }
    }

    private fun observeRecords(category: RecordCategory) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            val flow = if (category == RecordCategory.ALL) {
                repository.getAllRecords()
            } else {
                repository.getRecordsByCategory(category)
            }
            flow.collectLatest { records ->
                _state.update { it.copy(records = records, isLoading = false, errorMessage = null) }
            }
        }
    }

    private fun deleteRecord(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(id)
                _events.send(RecordUiEvent.ShowSnackbar("삭제되었습니다"))
            } catch (e: Exception) {
                _events.send(RecordUiEvent.ShowSnackbar("오류가 발생했어요. 다시 시도해 주세요"))
            }
        }
    }
}
