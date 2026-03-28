package com.mybaby.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.mybaby.app.core.data.LetterRepositoryImpl
import com.mybaby.app.core.database.DatabaseDriverFactory
import com.mybaby.app.db.PumDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = remember {
                val driver = DatabaseDriverFactory(this@MainActivity).createDriver()
                val database = PumDatabase(driver)
                LetterRepositoryImpl(database)
            }
            App(letterRepository = repository, onExit = { finish() })
        }
    }
}
