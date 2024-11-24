package com.andannn.melodify.core.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

internal actual fun createInMemoryDatabase()=  Room.inMemoryDatabaseBuilder<MelodifyDataBase>()
    .setDriver(BundledSQLiteDriver())
    .build()