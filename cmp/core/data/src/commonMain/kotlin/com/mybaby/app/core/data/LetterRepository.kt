package com.mybaby.app.core.data

import com.mybaby.app.core.model.Letter
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    fun getAllLetters(): Flow<List<Letter>>
    suspend fun saveLetter(letter: Letter)
    suspend fun deleteLetter(id: String)
    suspend fun canWriteToday(): Boolean
}
