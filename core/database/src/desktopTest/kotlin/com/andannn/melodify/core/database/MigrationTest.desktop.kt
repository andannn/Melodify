package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual fun getMigrationTestHelper(fileName: String) = MigrationTestHelper(
    schemaDirectoryPath = File("schemas").toPath(),
    driver = BundledSQLiteDriver(),
    databaseClass = MelodifyDataBase::class,
    databasePath = File(fileName).toPath()
)