package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

actual fun getMigrationTestHelper(fileName: String) = MigrationTestHelper(
    instrumentation = InstrumentationRegistry.getInstrumentation(),
    databaseClass = MelodifyDataBase::class,
    file = File(fileName),
    driver = AndroidSQLiteDriver(),
)
