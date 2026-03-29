package com.mybaby.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: String,
    val title: String,
    val description: String = "",
    val dateMillis: Long,
    val category: ScheduleCategory = ScheduleCategory.ETC
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
