package com.mybaby.app.core.data

import com.mybaby.app.core.model.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getAllSchedules(): Flow<List<Schedule>>
    fun getSchedulesByDateRange(startMillis: Long, endMillis: Long): Flow<List<Schedule>>
    fun getScheduleById(id: String): Flow<Schedule?>
    suspend fun saveSchedule(schedule: Schedule)
    suspend fun deleteSchedule(id: String)
}
