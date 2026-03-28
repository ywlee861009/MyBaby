package com.mybaby.app.feature.home

import com.mybaby.app.core.model.BabyStatus

/**
 * 대시보드 화면의 상태
 */
data class HomeState(
    val isLoading: Boolean = false,
    val babyStatus: BabyStatus? = null,
    val errorMessage: String? = null
)

/**
 * 대시보드 화면에서의 사용자 액션(Intent)
 */
sealed interface HomeIntent {
    object LoadData : HomeIntent
    object Refresh : HomeIntent
}
