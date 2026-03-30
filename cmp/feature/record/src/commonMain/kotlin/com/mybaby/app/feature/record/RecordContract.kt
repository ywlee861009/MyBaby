package com.mybaby.app.feature.record

import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.RecordCategory

// ─── 공통 UiEvent ──────────────────────────────────────────────────────────────

sealed interface RecordUiEvent {
    data class ShowSnackbar(val message: String) : RecordUiEvent
    data object NavigateBack : RecordUiEvent
    data class NavigateToAdd(val category: RecordCategory? = null) : RecordUiEvent
    data class NavigateToDetail(val id: String) : RecordUiEvent
    data class NavigateToEdit(val id: String) : RecordUiEvent
}

// ─── 기록 상세 ──────────────────────────────────────────────────────────────────

data class HealthRecordDetailState(
    val isLoading: Boolean = true,
    val record: HealthRecord? = null,
    val showMenu: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String? = null
)

sealed interface HealthRecordDetailIntent {
    data object OpenMenu : HealthRecordDetailIntent
    data object CloseMenu : HealthRecordDetailIntent
    data object ShowDeleteDialog : HealthRecordDetailIntent
    data object DismissDeleteDialog : HealthRecordDetailIntent
    data object ConfirmDelete : HealthRecordDetailIntent
    data object NavigateEdit : HealthRecordDetailIntent
}

// ─── 기록 수정 ──────────────────────────────────────────────────────────────────

data class HealthRecordEditState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val selectedCategory: RecordCategory = RecordCategory.WEIGHT,
    val date: Long = 0L,
    val weekNumber: Int = 0,
    val weightKg: String = "",
    val systolicBp: String = "",
    val diastolicBp: String = "",
    val kickCount: String = "",
    val kickTimeMinutes: String = "",
    val memoTitle: String = "",
    val memoContent: String = "",
    val errorMessage: String? = null
)

sealed interface HealthRecordEditIntent {
    data class SetWeight(val value: String) : HealthRecordEditIntent
    data class SetSystolicBp(val value: String) : HealthRecordEditIntent
    data class SetDiastolicBp(val value: String) : HealthRecordEditIntent
    data class SetKickCount(val value: String) : HealthRecordEditIntent
    data class SetKickTimeMinutes(val value: String) : HealthRecordEditIntent
    data class SetMemoTitle(val value: String) : HealthRecordEditIntent
    data class SetMemoContent(val value: String) : HealthRecordEditIntent
    data object Save : HealthRecordEditIntent
    data object RequestBack : HealthRecordEditIntent
}

// ─── 기록 목록 ──────────────────────────────────────────────────────────────────

data class HealthRecordListState(
    val isLoading: Boolean = true,
    val records: List<HealthRecord> = emptyList(),
    val selectedCategory: RecordCategory = RecordCategory.ALL,
    val errorMessage: String? = null
)

sealed interface HealthRecordListIntent {
    data class SelectCategory(val category: RecordCategory) : HealthRecordListIntent
    data class DeleteRecord(val id: String) : HealthRecordListIntent
    data class OpenDetail(val id: String) : HealthRecordListIntent
}

// ─── 기록 추가 ──────────────────────────────────────────────────────────────────

data class HealthRecordAddState(
    val isSaving: Boolean = false,
    val selectedCategory: RecordCategory = RecordCategory.WEIGHT,
    val date: Long = 0L,
    val weekNumber: Int = 0,
    val weightKg: String = "",
    val systolicBp: String = "",
    val diastolicBp: String = "",
    val kickCount: String = "",
    val kickTimeMinutes: String = "",
    val memoTitle: String = "",
    val memoContent: String = "",
    val errorMessage: String? = null
)

sealed interface HealthRecordAddIntent {
    data class SetCategory(val category: RecordCategory) : HealthRecordAddIntent
    data class SetWeight(val value: String) : HealthRecordAddIntent
    data class SetSystolicBp(val value: String) : HealthRecordAddIntent
    data class SetDiastolicBp(val value: String) : HealthRecordAddIntent
    data class SetKickCount(val value: String) : HealthRecordAddIntent
    data class SetKickTimeMinutes(val value: String) : HealthRecordAddIntent
    data class SetMemoTitle(val value: String) : HealthRecordAddIntent
    data class SetMemoContent(val value: String) : HealthRecordAddIntent
    data object Save : HealthRecordAddIntent
    data object RequestBack : HealthRecordAddIntent
}
