/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.andannn.melodify.core.database.MelodifyDataBase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual val databaseBuilder =
    module {
        single<RoomDatabase.Builder<MelodifyDataBase>> {
            val dbFilePath = documentDirectory() + "/my_room.db"
            Room.databaseBuilder<MelodifyDataBase>(
                name = dbFilePath,
            )
                .setDriver(BundledSQLiteDriver())
        }
    }

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory =
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
    return requireNotNull(documentDirectory?.path)
}
