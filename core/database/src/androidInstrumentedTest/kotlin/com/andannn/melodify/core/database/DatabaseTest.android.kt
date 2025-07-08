package com.andannn.melodify.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider

internal actual fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase> =
    Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        MelodifyDataBase::class.java,
    ).allowMainThreadQueries()
