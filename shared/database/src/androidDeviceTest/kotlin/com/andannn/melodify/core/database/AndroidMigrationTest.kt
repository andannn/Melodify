/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidMigrationTest : AbstractMigrationTest() {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Before
    fun setup() {
        context.deleteDatabase(TEST_DB)
    }

    @get:Rule
    override val helper: MigrationTestHelper =
        MigrationTestHelper(
            instrumentation = instrumentation,
            file = context.getDatabasePath(TEST_DB),
            driver = AndroidSQLiteDriver(),
            databaseClass = MelodifyDataBase::class,
        )
}
