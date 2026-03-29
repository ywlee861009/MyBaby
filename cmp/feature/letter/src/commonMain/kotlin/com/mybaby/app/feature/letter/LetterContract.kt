package com.mybaby.app.feature.letter

import com.mybaby.app.core.model.Letter

// ─── 공통 UiEvent ─────────────────────────────────────────────────────────────

sealed interface LetterUiEvent {
    data class ShowSnackbar(val message: String) : LetterUiEvent
    data object NavigateBack : LetterUiEvent
    data class NavigateToDetail(val id: String) : LetterUiEvent
    data class NavigateToEdit(val id: String) : LetterUiEvent
}

// ─── 편지 목록 ─────────────────────────────────────────────────────────────────

data class LetterListState(
    val isLoading: Boolean = false,
    val letters: List<Letter> = emptyList(),
    val canWriteToday: Boolean = true,
    val errorMessage: String? = null
)

sealed interface LetterListIntent {
    data object LoadLetters : LetterListIntent
    data class DeleteLetter(val id: String) : LetterListIntent
}

// ─── 편지 작성 ─────────────────────────────────────────────────────────────────

data class LetterWriteState(
    val draftContent: String = "",
    val selectedTheme: String = "#FFF8F0",
    val weekNumber: Int = 0,
    val isSaving: Boolean = false,
    val babyNickname: String = "아기"
)

sealed interface LetterWriteIntent {
    data class UpdateContent(val content: String) : LetterWriteIntent
    data class SelectTheme(val color: String) : LetterWriteIntent
    data object Save : LetterWriteIntent
}

// ─── 편지 상세 ─────────────────────────────────────────────────────────────────

data class LetterDetailState(
    val letter: Letter? = null,
    val isLoading: Boolean = true,
    val showMenu: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val babyNickname: String = "아기"
)

sealed interface LetterDetailIntent {
    data object OpenMenu : LetterDetailIntent
    data object CloseMenu : LetterDetailIntent
    data object ShowDeleteDialog : LetterDetailIntent
    data object DismissDeleteDialog : LetterDetailIntent
    data object ConfirmDelete : LetterDetailIntent
    data object NavigateEdit : LetterDetailIntent
}

// ─── 편지 편집 ─────────────────────────────────────────────────────────────────

data class LetterEditState(
    val letter: Letter? = null,
    val draftContent: String = "",
    val selectedTheme: String = "#FFF8F0",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val babyNickname: String = "아기"
)

sealed interface LetterEditIntent {
    data class UpdateContent(val content: String) : LetterEditIntent
    data class SelectTheme(val color: String) : LetterEditIntent
    data object Save : LetterEditIntent
}
