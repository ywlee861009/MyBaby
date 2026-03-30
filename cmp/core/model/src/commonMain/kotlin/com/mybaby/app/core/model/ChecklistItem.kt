package com.mybaby.app.core.model

data class ChecklistItem(
    val id: String,
    val text: String,
    val isChecked: Boolean,
    val weekNumber: Int,
    val createdAt: Long = 0L
)
