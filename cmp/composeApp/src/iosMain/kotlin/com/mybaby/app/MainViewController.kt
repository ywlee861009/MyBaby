package com.mybaby.app

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.mybaby.app.core.data.LetterRepositoryImpl
import com.mybaby.app.core.database.DatabaseDriverFactory
import com.mybaby.app.db.PumDatabase

fun MainViewController() = ComposeUIViewController {
    val repository = remember {
        val driver = DatabaseDriverFactory().createDriver()
        val database = PumDatabase(driver)
        LetterRepositoryImpl(database)
    }
    App(letterRepository = repository)
}
