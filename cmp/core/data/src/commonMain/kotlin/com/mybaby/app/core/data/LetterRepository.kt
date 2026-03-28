package com.mybaby.app.core.data

import com.mybaby.app.core.model.Letter
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    fun getAllLetters(): Flow<List<Letter>>
    fun getLetterById(id: String): Flow<Letter?>
    suspend fun saveLetter(letter: Letter)
    suspend fun deleteLetter(id: String)
    suspend fun canWriteToday(): Boolean
}
