package com.mybaby.app.feature.setup

import com.mybaby.app.core.model.BabyGender

// ─── BabyInfo 화면 ───────────────────────────────────────────

data class SetupBabyInfoState(
    val nickname: String = "",
    val gender: BabyGender = BabyGender.UNKNOWN,
    val isBorn: Boolean = false
) {
    val canProceed: Boolean get() = nickname.isNotBlank()
}

sealed interface SetupBabyInfoIntent {
    data class UpdateNickname(val nickname: String) : SetupBabyInfoIntent
    data class SelectGender(val gender: BabyGender) : SetupBabyInfoIntent
    data class SetBornStatus(val isBorn: Boolean) : SetupBabyInfoIntent
    data object Next : SetupBabyInfoIntent
}

sealed interface SetupBabyInfoEvent {
    data class NavigateNext(
        val nickname: String,
        val gender: BabyGender,
        val isBorn: Boolean
    ) : SetupBabyInfoEvent
}

// ─── PregnancyInfo 화면 ──────────────────────────────────────

data class SetupPregnancyInfoState(
    val nickname: String = "",
    val gender: BabyGender = BabyGender.UNKNOWN,
    val isBorn: Boolean = false,
    val selectedDateMillis: Long? = null,
    val pregnancyWeeks: Int = 0,
    val pregnancyDays: Int = 0,
    val isSaving: Boolean = false
) {
    val canSave: Boolean get() = selectedDateMillis != null
}

sealed interface SetupPregnancyInfoIntent {
    data class SelectDate(val dateMillis: Long) : SetupPregnancyInfoIntent
    data object Save : SetupPregnancyInfoIntent
}

sealed interface SetupPregnancyInfoEvent {
    data object SetupComplete : SetupPregnancyInfoEvent
}
