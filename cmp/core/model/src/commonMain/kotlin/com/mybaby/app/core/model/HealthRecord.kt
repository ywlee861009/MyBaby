package com.mybaby.app.core.model

import kotlinx.serialization.Serializable

/**
 * 산모의 일일 건강 기록 모델
 */
@Serializable
data class HealthRecord(
    val id: String,
    val date: Long, // Epoch timestamp
    val weight: Double? = null,
    val fetalMovementCount: Int? = null,
    val memo: String = ""
)

/**
 * 태아의 성장 상태 정보
 */
@Serializable
data class BabyStatus(
    val weeks: Int,
    val days: Int,
    val dDay: Int,
    val title: String,
    val message: String
)
