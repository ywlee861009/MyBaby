package com.mybaby.app.core.model

import kotlinx.serialization.Serializable

/**
 * 아기에게 보내는 편지 모델
 */
@Serializable
data class Letter(
    val id: String,
    val content: String,
    val createdAt: Long, // Epoch timestamp (생성일)
    val updatedAt: Long, // Epoch timestamp (수정일)
    val weekNumber: Int = 0, // 작성 시점 임신 주차
    val themeColor: String = "#FFF8F0", // 편지지 배경색
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

@Serializable
enum class SyncStatus {
    PENDING, // 로컬에만 저장됨
    SYNCED,  // 서버와 동기화됨
    FAILED   // 동기화 실패
}
