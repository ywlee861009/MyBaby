package com.mybaby.app.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.model.HealthRecord
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HealthRecordEditViewModel(
    private val repository: HealthRecordRepository,
    private val recordId: String
) : ViewModel() {

    private val _state = MutableStateFlow(HealthRecordEditState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecordUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val record = repository.getRecordById(recordId).first()
            if (record != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        selectedCategory = record.category,
                        date = record.date,
                        weekNumber = record.weekNumber,
                        weightKg = record.weightKg?.toString() ?: "",
                        systolicBp = record.systolicBp?.toString() ?: "",
                        diastolicBp = record.diastolicBp?.toString() ?: "",
                        kickCount = record.kickCount?.toString() ?: "",
                        kickTimeMinutes = record.kickTimeMinutes?.toString() ?: "",
                        memoTitle = record.memoTitle ?: "",
                        memoContent = record.memoContent ?: ""
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun handleIntent(intent: HealthRecordEditIntent) {
        when (intent) {
            is HealthRecordEditIntent.SetWeight -> _state.update { it.copy(weightKg = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.SetSystolicBp -> _state.update { it.copy(systolicBp = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.SetDiastolicBp -> _state.update { it.copy(diastolicBp = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.SetKickCount -> _state.update { it.copy(kickCount = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.SetKickTimeMinutes -> _state.update { it.copy(kickTimeMinutes = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.SetMemoTitle -> _state.update { it.copy(memoTitle = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.SetMemoContent -> _state.update { it.copy(memoContent = intent.value, errorMessage = null) }
            is HealthRecordEditIntent.Save -> saveRecord()
            is HealthRecordEditIntent.RequestBack -> viewModelScope.launch {
                _events.send(RecordUiEvent.NavigateBack)
            }
        }
    }

    private fun saveRecord() {
        val s = _state.value

        val isValid = when (s.selectedCategory) {
            com.mybaby.app.core.model.RecordCategory.WEIGHT -> s.weightKg.toDoubleOrNull() != null
            com.mybaby.app.core.model.RecordCategory.BLOOD_PRESSURE -> s.systolicBp.toIntOrNull() != null && s.diastolicBp.toIntOrNull() != null
            com.mybaby.app.core.model.RecordCategory.KICK -> s.kickCount.toIntOrNull() != null
            com.mybaby.app.core.model.RecordCategory.MEMO -> s.memoContent.isNotBlank()
            else -> true
        }

        if (!isValid) {
            _state.update { it.copy(errorMessage = "필수 항목을 입력해 주세요") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val updated = HealthRecord(
                    id = recordId,
                    category = s.selectedCategory,
                    date = s.date,
                    weekNumber = s.weekNumber,
                    weightKg = s.weightKg.toDoubleOrNull(),
                    systolicBp = s.systolicBp.toIntOrNull(),
                    diastolicBp = s.diastolicBp.toIntOrNull(),
                    kickCount = s.kickCount.toIntOrNull(),
                    kickTimeMinutes = s.kickTimeMinutes.toIntOrNull(),
                    memoTitle = s.memoTitle.takeIf { it.isNotBlank() },
                    memoContent = s.memoContent.takeIf { it.isNotBlank() },
                    createdAt = s.date,
                    updatedAt = now
                )
                repository.saveRecord(updated)
                _events.send(RecordUiEvent.ShowSnackbar("수정되었습니다"))
                _events.send(RecordUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _events.send(RecordUiEvent.ShowSnackbar("오류가 발생했어요. 다시 시도해 주세요"))
            }
        }
    }
}
