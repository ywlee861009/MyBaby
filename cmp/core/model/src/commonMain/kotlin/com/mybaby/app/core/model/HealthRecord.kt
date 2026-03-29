package com.mybaby.app.core.model

import kotlinx.serialization.Serializable

/**
 * 산모의 건강 기록 모델
 */
@Serializable
data class HealthRecord(
    val id: String,
    val category: RecordCategory = RecordCategory.WEIGHT,
    val date: Long,                    // Epoch millis
    val weekNumber: Int = 0,
    val weightKg: Double? = null,      // 체중 (kg)
    val systolicBp: Int? = null,       // 수축기 혈압 (mmHg)
    val diastolicBp: Int? = null,      // 이완기 혈압 (mmHg)
    val kickCount: Int? = null,        // 태동 횟수
    val kickTimeMinutes: Int? = null,  // 태동 측정 시간 (분)
    val photoUri: String? = null,      // 초음파 사진 로컬 경로
    val memoTitle: String? = null,     // 메모 제목
    val memoContent: String? = null,   // 메모 내용
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
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
