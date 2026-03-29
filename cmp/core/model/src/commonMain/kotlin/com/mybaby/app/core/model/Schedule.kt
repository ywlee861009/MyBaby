package com.mybaby.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: String,
    val title: String,
    val description: String = "",    // 메모/설명
    val dateMillis: Long,            // 날짜 (일 시작 epoch millis)
    val location: String? = null,    // 장소 (선택)
    val category: ScheduleCategory = ScheduleCategory.ETC,
    val notification: NotificationTiming = NotificationTiming.NONE,
    val isCompleted: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
enum class ScheduleCategory {
    CHECKUP, ULTRASOUND, BLOOD_TEST, VACCINE, ETC;

    fun label(): String = when (this) {
        CHECKUP -> "정기검진"
        ULTRASOUND -> "초음파"
        BLOOD_TEST -> "혈액검사"
        VACCINE -> "예방접종"
        ETC -> "기타"
    }
}

@Serializable
enum class NotificationTiming {
    NONE, SAME_DAY, ONE_DAY, THREE_DAYS, ONE_WEEK;

    fun label(): String = when (this) {
        NONE -> "없음"
        SAME_DAY -> "당일"
        ONE_DAY -> "1일 전"
        THREE_DAYS -> "3일 전"
        ONE_WEEK -> "1주 전"
    }
}
