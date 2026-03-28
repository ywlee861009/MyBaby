package com.mybaby.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Baby(
    val id: String,
    val nickname: String,
    val gender: BabyGender,
    val dueDate: Long? = null,
    val birthDate: Long? = null,
    val createdAt: Long
)

@Serializable
enum class BabyGender {
    BOY,
    GIRL,
    UNKNOWN;

    fun label(): String = when (this) {
        BOY -> "남아"
        GIRL -> "여아"
        UNKNOWN -> "모름"
    }
}
