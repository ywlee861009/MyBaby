package com.mybaby.app.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.model.RecordCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeightChartViewModel(
    private val repository: HealthRecordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeightChartState())
    val state = _state.asStateFlow()

    init {
        handleIntent(WeightChartIntent.LoadData)
    }

    fun handleIntent(intent: WeightChartIntent) {
        when (intent) {
            WeightChartIntent.LoadData, WeightChartIntent.Refresh -> loadWeightRecords()
        }
    }

    private fun loadWeightRecords() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Fetch all WEIGHT records and sort by date
                val allRecords = repository.getAllRecords().first()
                val weightRecords = allRecords
                    .filter { it.category == RecordCategory.WEIGHT && it.weightKg != null }
                    .sortedBy { it.date }

                _state.update {
                    it.copy(
                        isLoading = false,
                        records = weightRecords,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "기록을 불러오지 못했어요: ${e.message}"
                    )
                }
            }
        }
    }
}
