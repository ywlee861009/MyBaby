package com.mybaby.app.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mybaby.app.core.model.NotificationTiming
import com.mybaby.app.core.model.Schedule
import com.mybaby.app.core.model.ScheduleCategory
import com.mybaby.app.db.PumDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl(
    private val database: PumDatabase
) : ScheduleRepository {

    private val queries = database.scheduleQueries

    override fun getAllSchedules(): Flow<List<Schedule>> =
        queries.selectAll().asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toModel() }
        }

    override fun getSchedulesByDateRange(startMillis: Long, endMillis: Long): Flow<List<Schedule>> =
        queries.selectByDateRange(startMillis, endMillis).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toModel() }
        }

    override fun getScheduleById(id: String): Flow<Schedule?> =
        queries.selectById(id).asFlow().mapToOneOrNull(Dispatchers.Default).map { entity ->
            entity?.toModel()
        }

    override suspend fun saveSchedule(schedule: Schedule) {
        queries.insertSchedule(
            id = schedule.id,
            title = schedule.title,
            description = schedule.description,
            dateMillis = schedule.dateMillis,
            location = schedule.location,
            category = schedule.category.name,
            notification = schedule.notification.name,
            isCompleted = if (schedule.isCompleted) 1L else 0L,
            createdAt = schedule.createdAt,
            updatedAt = schedule.updatedAt
        )
    }

    override suspend fun deleteSchedule(id: String) {
        queries.deleteSchedule(id)
    }

    private fun com.mybaby.app.db.ScheduleEntity.toModel() = Schedule(
        id = id,
        title = title,
        description = description,
        dateMillis = dateMillis,
        location = location,
        category = try { ScheduleCategory.valueOf(category) } catch (e: Exception) { ScheduleCategory.ETC },
        notification = try { NotificationTiming.valueOf(notification) } catch (e: Exception) { NotificationTiming.NONE },
        isCompleted = isCompleted != 0L,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
