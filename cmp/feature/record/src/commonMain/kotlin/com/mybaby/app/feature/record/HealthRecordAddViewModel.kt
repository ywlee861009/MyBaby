package com.mybaby.app.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.RecordCategory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class HealthRecordAddViewModel(
    private val repository: HealthRecordRepository,
    private val babyRepository: BabyRepository,
    initialCategory: String? = null
) : ViewModel() {

    private val _state = MutableStateFlow(HealthRecordAddState())
    val state = _state.asStateFlow()

    private val _events = Channel<RecordUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        val category = if (initialCategory != null) {
            try { RecordCategory.valueOf(initialCategory.uppercase()) } catch (e: Exception) { RecordCategory.WEIGHT }
        } else RecordCategory.WEIGHT

        viewModelScope.launch {
            val now = Clock.System.now().toEpochMilliseconds()
            val baby = babyRepository.getBaby().first()
            val msPerDay = 24L * 3600L * 1000L
            val msPerWeek = 7L * msPerDay
            val weekNumber = baby?.dueDate?.let { dueDate ->
                val pregnancyStartMillis = dueDate - 280 * msPerDay
                val elapsedMillis = now - pregnancyStartMillis
                (elapsedMillis / msPerWeek).toInt().coerceIn(0, 40)
            } ?: 0

            _state.update {
                it.copy(
                    date = now,
                    selectedCategory = category,
                    weekNumber = weekNumber
                )
            }
        }
    }

    fun handleIntent(intent: HealthRecordAddIntent) {
        when (intent) {
            is HealthRecordAddIntent.SetCategory -> _state.update { it.copy(selectedCategory = intent.category) }
            is HealthRecordAddIntent.SetWeight -> _state.update { it.copy(weightKg = intent.value) }
            is HealthRecordAddIntent.SetSystolicBp -> _state.update { it.copy(systolicBp = intent.value) }
            is HealthRecordAddIntent.SetDiastolicBp -> _state.update { it.copy(diastolicBp = intent.value) }
            is HealthRecordAddIntent.SetKickCount -> _state.update { it.copy(kickCount = intent.value) }
            is HealthRecordAddIntent.SetKickTimeMinutes -> _state.update { it.copy(kickTimeMinutes = intent.value) }
            is HealthRecordAddIntent.SetMemoTitle -> _state.update { it.copy(memoTitle = intent.value) }
            is HealthRecordAddIntent.SetMemoContent -> _state.update { it.copy(memoContent = intent.value) }
            is HealthRecordAddIntent.Save -> saveRecord()
            is HealthRecordAddIntent.RequestBack -> viewModelScope.launch {
                _events.send(RecordUiEvent.NavigateBack)
            }
        }
    }

    private fun saveRecord() {
        val s = _state.value

        // 유효성 검사
        val isValid = when (s.selectedCategory) {
            RecordCategory.WEIGHT -> s.weightKg.toDoubleOrNull() != null
            RecordCategory.BLOOD_PRESSURE -> s.systolicBp.toIntOrNull() != null && s.diastolicBp.toIntOrNull() != null
            RecordCategory.KICK -> s.kickCount.toIntOrNull() != null
            RecordCategory.MEMO -> s.memoContent.isNotBlank()
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
                val record = HealthRecord(
                    id = "rec_${now}_${Random.nextInt(9999)}",
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
                    createdAt = now,
                    updatedAt = now
                )
                repository.saveRecord(record)
                _events.send(RecordUiEvent.ShowSnackbar("저장되었습니다"))
                _events.send(RecordUiEvent.NavigateBack)
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                _events.send(RecordUiEvent.ShowSnackbar("오류가 발생했어요. 다시 시도해 주세요"))
            }
        }
    }
}
