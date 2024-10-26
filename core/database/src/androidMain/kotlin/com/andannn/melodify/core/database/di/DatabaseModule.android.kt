package com.andannn.melodify.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.andannn.melodify.core.database.MelodifyDataBase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseBuilder: Module = module {
    single<RoomDatabase.Builder<MelodifyDataBase>> {
        val appContext = androidContext().applicationContext
        val dbFile = appContext.getDatabasePath("melodify_database.db")
        Room.databaseBuilder<MelodifyDataBase>(
            context = appContext,
            name = dbFile.absolutePath
        )
            .setDriver(AndroidSQLiteDriver())
    }
}