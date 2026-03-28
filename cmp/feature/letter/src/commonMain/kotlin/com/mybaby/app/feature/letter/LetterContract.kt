package com.mybaby.app.feature.letter

import com.mybaby.app.core.model.Letter

/**
 * 편지 화면의 상태
 */
data class LetterState(
    val isLoading: Boolean = false,
    val letters: List<Letter> = emptyList(),
    val canWriteToday: Boolean = true, // 오늘 편지를 작성할 수 있는지 여부
    val isWritingMode: Boolean = false, // 작성 모드인지 목록 모드인지 여부
    val currentLetterId: String? = null, // 작성/수정 중인 편지 ID
    val draftContent: String = "", // 작성 중인 내용
    val errorMessage: String? = null
)

/**
 * 사용자 액션(Intent)
 */
sealed interface LetterIntent {
    object LoadLetters : LetterIntent
    object OpenWriteMode : LetterIntent
    object CloseWriteMode : LetterIntent
    data class UpdateDraftContent(val content: String) : LetterIntent
    object SaveLetter : LetterIntent
    data class EditLetter(val letter: Letter) : LetterIntent
}
