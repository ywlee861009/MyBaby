package com.mybaby.app.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.RecordCategory
import com.mybaby.app.db.PumDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HealthRecordRepositoryImpl(
    private val database: PumDatabase
) : HealthRecordRepository {

    private val queries = database.healthRecordQueries

    override fun getAllRecords(): Flow<List<HealthRecord>> =
        queries.selectAll().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toModel() }
        }

    override fun getRecordsByCategory(category: RecordCategory): Flow<List<HealthRecord>> =
        queries.selectByCategory(category.name).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toModel() }
        }

    override fun getRecordById(id: String): Flow<HealthRecord?> =
        queries.selectById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { entity ->
            entity?.toModel()
        }

    override suspend fun saveRecord(record: HealthRecord) {
        queries.insertRecord(
            id = record.id,
            category = record.category.name,
            date = record.date,
            weekNumber = record.weekNumber.toLong(),
            weightKg = record.weightKg,
            systolicBp = record.systolicBp?.toLong(),
            diastolicBp = record.diastolicBp?.toLong(),
            kickCount = record.kickCount?.toLong(),
            kickTimeMinutes = record.kickTimeMinutes?.toLong(),
            photoUri = record.photoUri,
            memoTitle = record.memoTitle,
            memoContent = record.memoContent,
            createdAt = record.createdAt,
            updatedAt = record.updatedAt
        )
    }

    override suspend fun deleteRecord(id: String) {
        queries.deleteRecord(id)
    }

    private fun com.mybaby.app.db.HealthRecordEntity.toModel() = HealthRecord(
        id = id,
        category = try { RecordCategory.valueOf(category) } catch (e: Exception) { RecordCategory.MEMO },
        date = date,
        weekNumber = weekNumber.toInt(),
        weightKg = weightKg,
        systolicBp = systolicBp?.toInt(),
        diastolicBp = diastolicBp?.toInt(),
        kickCount = kickCount?.toInt(),
        kickTimeMinutes = kickTimeMinutes?.toInt(),
        photoUri = photoUri,
        memoTitle = memoTitle,
        memoContent = memoContent,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
