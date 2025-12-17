/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
class AndroidMigrationTest : AbstractMigrationTest() {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val file = instrumentation.targetContext.getDatabasePath("test.db")

    @get:Rule
    override val helper: MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = instrumentation,
            file = file,
            driver = AndroidSQLiteDriver(),
            databaseClass = MelodifyDataBase::class,
        )

    @BeforeTest
    fun before() {
        requireNotNull(file)
        file.parentFile?.mkdirs()
        deleteDatabaseFile()
    }

    @AfterTest
    fun after() {
        deleteDatabaseFile()
    }

    private fun deleteDatabaseFile() {
        instrumentation.targetContext.deleteDatabase(file.name)
    }
}
