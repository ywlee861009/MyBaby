package com.mybaby.app.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mybaby.app.core.model.Letter
import com.mybaby.app.core.model.SyncStatus
import com.mybaby.app.db.PumDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.*

class LetterRepositoryImpl(
    private val database: PumDatabase
) : LetterRepository {
    private val queries = database.letterQueries

    override fun getAllLetters(): Flow<List<Letter>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Default).map { entities ->
            entities.map { it.toLetter() }
        }
    }

    override fun getLetterById(id: String): Flow<Letter?> {
        return queries.selectById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { entity ->
            entity?.toLetter()
        }
    }

    override suspend fun saveLetter(letter: Letter) {
        queries.insertLetter(
            id = letter.id,
            content = letter.content,
            createdAt = letter.createdAt,
            updatedAt = letter.updatedAt,
            weekNumber = letter.weekNumber.toLong(),
            themeColor = letter.themeColor,
            syncStatus = letter.syncStatus.name
        )
    }

    override suspend fun deleteLetter(id: String) {
        queries.deleteLetter(id)
    }

    override suspend fun canWriteToday(): Boolean {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val startOfDay = today.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endOfDay = today.atTime(LocalTime(23, 59, 59))
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        val lettersToday = queries.selectByDateRange(startOfDay, endOfDay).executeAsList()
        return lettersToday.isEmpty()
    }

    private fun com.mybaby.app.db.LetterEntity.toLetter() = Letter(
        id = id,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
        weekNumber = weekNumber.toInt(),
        themeColor = themeColor,
        syncStatus = SyncStatus.valueOf(syncStatus)
    )
}
