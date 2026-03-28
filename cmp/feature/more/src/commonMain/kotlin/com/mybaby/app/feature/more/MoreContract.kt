package com.mybaby.app.feature.more

import com.mybaby.app.core.model.Baby
import com.mybaby.app.core.model.BabyGender

data class MoreState(
    val isLoading: Boolean = true,
    val baby: Baby? = null,
    val isEditing: Boolean = false,
    val editNickname: String = "",
    val editGender: BabyGender = BabyGender.UNKNOWN,
    val editIsBorn: Boolean = false,
    val editDateMillis: Long? = null,
    val pregnancyWeeks: Int = 0,
    val pregnancyDays: Int = 0,
    val isSaving: Boolean = false,
) {
    val canSave: Boolean get() = editNickname.isNotBlank() && editDateMillis != null
}

sealed interface MoreIntent {
    data object StartEdit : MoreIntent
    data object CancelEdit : MoreIntent
    data object Save : MoreIntent
    data class UpdateNickname(val nickname: String) : MoreIntent
    data class SelectGender(val gender: BabyGender) : MoreIntent
    data class SetBornStatus(val isBorn: Boolean) : MoreIntent
    data class SelectDate(val dateMillis: Long) : MoreIntent
}

sealed interface MoreEvent {
    data object SaveSuccess : MoreEvent
}
