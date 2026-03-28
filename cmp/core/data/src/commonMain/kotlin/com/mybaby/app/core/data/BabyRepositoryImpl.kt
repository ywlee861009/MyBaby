package com.mybaby.app.core.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mybaby.app.core.model.Baby
import com.mybaby.app.core.model.BabyGender
import com.mybaby.app.db.PumDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BabyRepositoryImpl(
    private val database: PumDatabase
) : BabyRepository {
    private val queries = database.babyQueries

    override fun getBaby(): Flow<Baby?> {
        return queries.selectFirst().asFlow().mapToOneOrNull(Dispatchers.Default).map { entity ->
            entity?.let {
                Baby(
                    id = it.id,
                    nickname = it.nickname,
                    gender = BabyGender.valueOf(it.gender),
                    dueDate = it.dueDate,
                    birthDate = it.birthDate,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun saveBaby(baby: Baby) {
        queries.insert(
            id = baby.id,
            nickname = baby.nickname,
            gender = baby.gender.name,
            dueDate = baby.dueDate,
            birthDate = baby.birthDate,
            createdAt = baby.createdAt
        )
    }

    override suspend fun deleteBaby() {
        queries.deleteAll()
    }
}
