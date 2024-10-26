package com.andannn.melodify.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

// ./gradlew core:database:connectedAndroidTest

actual val dummyDatabase: MelodifyDataBase
    get() = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MelodifyDataBase::class.java
        ).allowMainThreadQueries().build()
