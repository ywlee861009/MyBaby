package com.mybaby.app.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mybaby.app.core.model.ChecklistItem
import com.mybaby.app.db.PumDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChecklistRepositoryImpl(
    private val database: PumDatabase
) : ChecklistRepository {

    private val queries = database.checklistItemQueries

    override fun getItemsByWeek(weekNumber: Int): Flow<List<ChecklistItem>> =
        queries.selectByWeek(weekNumber.toLong()).asFlow().mapToList(Dispatchers.Default).map { list ->
            list.map { it.toModel() }
        }

    override suspend fun saveItem(item: ChecklistItem) {
        queries.insertItem(
            id = item.id,
            text = item.text,
            isChecked = if (item.isChecked) 1L else 0L,
            weekNumber = item.weekNumber.toLong(),
            createdAt = item.createdAt
        )
    }

    override suspend fun updateChecked(id: String, isChecked: Boolean) {
        queries.updateChecked(isChecked = if (isChecked) 1L else 0L, id = id)
    }

    override suspend fun deleteItem(id: String) {
        queries.deleteItem(id)
    }

    private fun com.mybaby.app.db.ChecklistItemEntity.toModel() = ChecklistItem(
        id = id,
        text = text,
        isChecked = isChecked != 0L,
        weekNumber = weekNumber.toInt(),
        createdAt = createdAt
    )
}
