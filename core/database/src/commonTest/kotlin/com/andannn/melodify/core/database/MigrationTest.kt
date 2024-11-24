package com.andannn.melodify.core.database

import kotlin.test.Test
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import androidx.sqlite.use
import com.andannn.melodify.core.database.entity.CustomTabType.ALL_MUSIC
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

expect fun getMigrationTestHelper(fileName: String): MigrationTestHelper

class MigrationTest {

    private val tempFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("test-${Random.nextInt()}.db")

    @BeforeTest
    fun before() {
        FileSystem.SYSTEM.delete(tempFile)
        FileSystem.SYSTEM.delete("${tempFile}.lck".toPath())
    }

    @AfterTest
    fun after() {
        FileSystem.SYSTEM.delete(tempFile)
        FileSystem.SYSTEM.delete("${tempFile}.lck".toPath())
    }

    @Test
    fun migrate3To4() {
        val migrationTestHelper = getMigrationTestHelper(
            tempFile.toString()
        )
        val newConnection = migrationTestHelper.createDatabase(3)
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(4)
        migratedConnection.close()
    }

    @Test
    fun migrate4To5() {
        val migrationTestHelper = getMigrationTestHelper(
            tempFile.toString()
        )
        val newConnection = migrationTestHelper.createDatabase(4)
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(5)
        migratedConnection.prepare("SELECT * FROM ${Tables.CUSTOM_TAB}").use { stmt ->
            assertTrue { stmt.step() }
            assertEquals(ALL_MUSIC, stmt.getText(1))
        }
        migratedConnection.close()
    }
}