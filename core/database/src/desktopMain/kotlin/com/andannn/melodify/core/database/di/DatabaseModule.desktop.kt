package com.andannn.melodify.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.andannn.melodify.core.database.MelodifyDataBase
import com.andannn.melodify.core.platform.PlatformInfo
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseBuilder: Module =
    module {
        single<RoomDatabase.Builder<MelodifyDataBase>> {
            Room.databaseBuilder<MelodifyDataBase>(
                name = get<PlatformInfo>().databasePath,
            )
                .setDriver(BundledSQLiteDriver())
        }
    }
