package com.andannn.melodify.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

internal actual fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase> =
    Room.inMemoryDatabaseBuilder<MelodifyDataBase>()
        .setDriver(BundledSQLiteDriver())