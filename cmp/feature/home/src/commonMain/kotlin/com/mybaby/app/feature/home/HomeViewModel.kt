package com.mybaby.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.model.BabyStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        handleIntent(HomeIntent.LoadData)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData, HomeIntent.Refresh -> loadDashboardData()
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // MVP를 위한 더미 데이터 로드
            val mockStatus = BabyStatus(
                weeks = 24,
                days = 3,
                dDay = 110,
                title = "열무는 지금 쑥쑥 크는 중!",
                message = "이제 아기의 심장 소리가 더 또렷해졌어요. 가벼운 산책을 추천해요."
            )
            
            _state.update { it.copy(
                isLoading = false,
                babyStatus = mockStatus
            ) }
        }
    }
}
