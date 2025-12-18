/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.remove
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class NativeMigrationTest : AbstractMigrationTest() {
    private val filename = "/tmp/test-${Random.nextInt()}.db"
    private val driver: SQLiteDriver = BundledSQLiteDriver()

    override val helper: MigrationTestHelper =
        MigrationTestHelper(
            schemaDirectoryPath = getSchemaDirectoryPath(),
            driver = driver,
            databaseClass = MelodifyDataBase::class,
            fileName = filename,
        )

    @OptIn(ExperimentalForeignApi::class)
    @BeforeTest
    fun before() {
        deleteDatabaseFile()
    }

    @AfterTest
    fun after() {
        helper.finished()
        deleteDatabaseFile()
    }

    private fun deleteDatabaseFile() {
        remove(filename)
        remove("$filename-wal")
        remove("$filename-shm")
    }
}
