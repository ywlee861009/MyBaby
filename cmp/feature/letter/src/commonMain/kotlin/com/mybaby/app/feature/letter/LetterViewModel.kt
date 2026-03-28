package com.mybaby.app.feature.letter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.model.Letter
import com.mybaby.app.core.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class LetterViewModel : ViewModel() {
    private val _state = MutableStateFlow(LetterState())
    val state = _state.asStateFlow()

    init {
        handleIntent(LetterIntent.LoadLetters)
    }

    fun handleIntent(intent: LetterIntent) {
        when (intent) {
            is LetterIntent.LoadLetters -> loadLetters()
            is LetterIntent.OpenWriteMode -> openWriteMode()
            is LetterIntent.CloseWriteMode -> closeWriteMode()
            is LetterIntent.UpdateDraftContent -> updateDraftContent(intent.content)
            is LetterIntent.SaveLetter -> saveLetter()
            is LetterIntent.EditLetter -> editLetter(intent.letter)
        }
    }

    private fun loadLetters() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // TODO: 추후 LocalRepository에서 조회
            // 현재는 더미 데이터로 동작 여부 확인
            val mockLetters = listOf<Letter>() // 초기에는 빈 목록
            
            _state.update { it.copy(
                isLoading = false,
                letters = mockLetters,
                canWriteToday = checkIfCanWriteToday(mockLetters)
            ) }
        }
    }

    private fun openWriteMode() {
        _state.update { it.copy(isWritingMode = true, currentLetterId = null, draftContent = "") }
    }

    private fun closeWriteMode() {
        _state.update { it.copy(isWritingMode = false, currentLetterId = null, draftContent = "") }
    }

    private fun updateDraftContent(content: String) {
        _state.update { it.copy(draftContent = content) }
    }

    private fun saveLetter() {
        viewModelScope.launch {
            val currentContent = _state.value.draftContent
            if (currentContent.isBlank()) return@launch

            _state.update { it.copy(isLoading = true) }

            // TODO: 추후 Repository를 통해 DB 또는 서버에 저장
            // 현재는 메모리 상에서 시뮬레이션
            val newLetter = Letter(
                id = _state.value.currentLetterId ?: "letter_${Random.nextInt()}",
                content = currentContent,
                createdAt = System.currentTimeMillis(), // TODO: 실제 날짜 처리 필요
                updatedAt = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING
            )

            val updatedLetters = if (_state.value.currentLetterId != null) {
                _state.value.letters.map { if (it.id == newLetter.id) newLetter else it }
            } else {
                listOf(newLetter) + _state.value.letters
            }

            _state.update { it.copy(
                isLoading = false,
                letters = updatedLetters,
                isWritingMode = false,
                canWriteToday = checkIfCanWriteToday(updatedLetters)
            ) }
        }
    }

    private fun editLetter(letter: Letter) {
        _state.update { it.copy(
            isWritingMode = true,
            currentLetterId = letter.id,
            draftContent = letter.content
        ) }
    }

    private fun checkIfCanWriteToday(letters: List<Letter>): Boolean {
        // 오늘 날짜(00:00:00 ~ 23:59:59) 사이에 작성된 편지가 있는지 확인하는 로직 (단순화된 예시)
        // 실제로는 KMP 전용 날짜 라이브러리 등을 사용하는 것이 좋음
        return true // TODO: 날짜 비교 로직 정교화
    }
}
