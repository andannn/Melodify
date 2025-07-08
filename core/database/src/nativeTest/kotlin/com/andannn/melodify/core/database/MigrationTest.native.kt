package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual fun getMigrationTestHelper(fileName: String) =
    MigrationTestHelper(
        schemaDirectoryPath = "schemas",
        driver = BundledSQLiteDriver(),
        databaseClass = MelodifyDataBase::class,
        fileName = fileName,
    )
