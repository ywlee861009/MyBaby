package com.mybaby.app

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.mybaby.app.core.data.BabyRepositoryImpl
import com.mybaby.app.core.data.ChecklistRepositoryImpl
import com.mybaby.app.core.data.HealthRecordRepositoryImpl
import com.mybaby.app.core.data.LetterRepositoryImpl
import com.mybaby.app.core.data.ScheduleRepositoryImpl
import com.mybaby.app.core.database.DatabaseDriverFactory
import com.mybaby.app.db.PumDatabase

fun MainViewController() = ComposeUIViewController {
    val database = remember {
        val driver = DatabaseDriverFactory().createDriver()
        PumDatabase(driver)
    }
    val babyRepository = remember { BabyRepositoryImpl(database) }
    val letterRepository = remember { LetterRepositoryImpl(database) }
    val healthRecordRepository = remember { HealthRecordRepositoryImpl(database) }
    val scheduleRepository = remember { ScheduleRepositoryImpl(database) }
    val checklistRepository = remember { ChecklistRepositoryImpl(database) }
    App(
        babyRepository = babyRepository,
        letterRepository = letterRepository,
        healthRecordRepository = healthRecordRepository,
        scheduleRepository = scheduleRepository,
        checklistRepository = checklistRepository
    )
}
