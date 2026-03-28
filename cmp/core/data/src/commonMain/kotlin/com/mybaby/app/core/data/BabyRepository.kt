package com.mybaby.app.core.data

import com.mybaby.app.core.model.Baby
import kotlinx.coroutines.flow.Flow

interface BabyRepository {
    fun getBaby(): Flow<Baby?>
    suspend fun saveBaby(baby: Baby)
    suspend fun deleteBaby()
}
