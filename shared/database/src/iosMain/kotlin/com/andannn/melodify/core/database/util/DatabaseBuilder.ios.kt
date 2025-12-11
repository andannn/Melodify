/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.util

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.andannn.melodify.core.database.MelodifyDataBase

internal actual fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase> =
    Room
        .inMemoryDatabaseBuilder<MelodifyDataBase>()
        .setDriver(BundledSQLiteDriver())
