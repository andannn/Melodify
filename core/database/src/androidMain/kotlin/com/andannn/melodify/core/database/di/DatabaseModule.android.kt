package com.andannn.melodify.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.andannn.melodify.core.database.MelodifyDataBase
import com.andannn.melodify.core.platform.PlatformInfo
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseBuilder: Module = module {
    single<RoomDatabase.Builder<MelodifyDataBase>> {
        val appContext = androidContext().applicationContext
        Room.databaseBuilder<MelodifyDataBase>(
            context = appContext,
            name = get<PlatformInfo>().databasePath
        )
            .setDriver(AndroidSQLiteDriver())
    }
}