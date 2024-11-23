package com.andannn.melodify.core.database

import kotlin.test.Test
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import androidx.sqlite.use
import kotlin.test.assertEquals
import kotlin.test.assertTrue

expect fun getMigrationTestHelper(): MigrationTestHelper

class MigrationTest {

    @Test
    fun migrate1To2() {
        val migrationTestHelper = getMigrationTestHelper()
        // Create the database at version 1
        val newConnection = migrationTestHelper.createDatabase(1)
        // Insert some data that should be preserved
        newConnection.execSQL("INSERT INTO Pet (id, name) VALUES (1, 'Tom')")
        newConnection.close()

        // Migrate the database to version 2
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(2, listOf(MIGRATION_1_2))
        migratedConnection.prepare("SELECT  FROM Pet").use { stmt ->
            // Validates data is preserved between migrations.
            assertTrue { stmt.step() }
            assertEquals("Tom", stmt.getText(1))
        }
        migratedConnection.close()
    }
}