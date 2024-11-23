package com.andannn.melodify.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.andannn.melodify.core.database.MelodifyDataBase
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseBuilder: Module = module {
    single<RoomDatabase.Builder<MelodifyDataBase>> {
        Room.databaseBuilder<MelodifyDataBase>(
            name = System.getProperty("user.home") + ".melodify/cache/" + DATABASE_FILE_NAME
        )
            .setDriver(BundledSQLiteDriver())
    }
}
