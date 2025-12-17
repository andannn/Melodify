/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidDatabaseTest : AbstractDatabaseTest() {
    override fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase> =
        Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                MelodifyDataBase::class.java,
            ).allowMainThreadQueries()
}
