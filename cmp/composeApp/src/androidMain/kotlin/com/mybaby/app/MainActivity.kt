package com.mybaby.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.mybaby.app.core.data.BabyRepositoryImpl
import com.mybaby.app.core.data.HealthRecordRepositoryImpl
import com.mybaby.app.core.data.LetterRepositoryImpl
import com.mybaby.app.core.data.ScheduleRepositoryImpl
import com.mybaby.app.core.database.DatabaseDriverFactory
import com.mybaby.app.db.PumDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val database = remember {
                val driver = DatabaseDriverFactory(this@MainActivity).createDriver()
                PumDatabase(driver)
            }
            val letterRepository = remember { LetterRepositoryImpl(database) }
            val babyRepository = remember { BabyRepositoryImpl(database) }
            val healthRecordRepository = remember { HealthRecordRepositoryImpl(database) }
            val scheduleRepository = remember { ScheduleRepositoryImpl(database) }
            App(
                babyRepository = babyRepository,
                letterRepository = letterRepository,
                healthRecordRepository = healthRecordRepository,
                scheduleRepository = scheduleRepository,
                onExit = { finish() }
            )
        }
    }
}
