package com.mybaby.app.core.data

import com.mybaby.app.core.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    fun getItemsByWeek(weekNumber: Int): Flow<List<ChecklistItem>>
    suspend fun saveItem(item: ChecklistItem)
    suspend fun updateChecked(id: String, isChecked: Boolean)
    suspend fun deleteItem(id: String)
}
