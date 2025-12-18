/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.junit.Before
import java.io.File

class DeskTopMigrationTest : AbstractMigrationTest() {
    private val tmpDir = System.getProperty("java.io.tmpdir")

    private val dbPath = File(tmpDir).resolve(TEST_DB)

    override val helper: MigrationTestHelper
        get() =
            MigrationTestHelper(
                schemaDirectoryPath = File("schemas").toPath(),
                driver = BundledSQLiteDriver(),
                databaseClass = MelodifyDataBase::class,
                databasePath = dbPath.toPath(),
            )

    @Before
    fun setup() {
        dbPath.delete()
    }
}
