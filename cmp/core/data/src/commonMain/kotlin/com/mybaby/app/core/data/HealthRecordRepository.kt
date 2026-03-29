package com.mybaby.app.core.data

import com.mybaby.app.core.model.HealthRecord
import com.mybaby.app.core.model.RecordCategory
import kotlinx.coroutines.flow.Flow

interface HealthRecordRepository {
    fun getAllRecords(): Flow<List<HealthRecord>>
    fun getRecordsByCategory(category: RecordCategory): Flow<List<HealthRecord>>
    fun getRecordById(id: String): Flow<HealthRecord?>
    suspend fun saveRecord(record: HealthRecord)
    suspend fun deleteRecord(id: String)
}
