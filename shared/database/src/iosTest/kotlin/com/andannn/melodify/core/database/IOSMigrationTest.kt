/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

class IOSMigrationTest : AbstractMigrationTest() {
    override fun getMigrationTestHelper(fileName: String): MigrationTestHelper =
        MigrationTestHelper(
            schemaDirectoryPath = "schemas",
            driver = BundledSQLiteDriver(),
            databaseClass = MelodifyDataBase::class,
            fileName = fileName,
        )
}
