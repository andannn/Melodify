/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

class DesktopDatabaseTest : AbstractDatabaseTest() {
    override fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase> =
        Room
            .inMemoryDatabaseBuilder<MelodifyDataBase>()
            .setDriver(BundledSQLiteDriver())
}
