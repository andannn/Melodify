/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.CustomTabType.ALL_MUSIC
import com.andannn.melodify.core.database.entity.MediaColumns
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

expect fun getMigrationTestHelper(fileName: String): MigrationTestHelper

class MigrationTest {
    private val tempFile =
        FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("test-${Random.nextInt()}.db")

    @BeforeTest
    fun before() {
        FileSystem.SYSTEM.delete(tempFile)
        FileSystem.SYSTEM.delete("$tempFile.lck".toPath())
    }

    @AfterTest
    fun after() {
        FileSystem.SYSTEM.delete(tempFile)
        FileSystem.SYSTEM.delete("$tempFile.lck".toPath())
    }

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate3To4() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(3)
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(4)
        migratedConnection.close()
    }

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate4To5() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
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

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate5To6() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(5)
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(6)
        migratedConnection.close()
    }

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate6To7SyncMediaTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(6)
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.LIBRARY_MEDIA}(${MediaColumns.ID}, ${MediaColumns.TITLE}) VALUES (1, 'row 1');
            """.trimIndent(),
        )
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.LIBRARY_MEDIA}(${MediaColumns.ID}, ${MediaColumns.TITLE}) VALUES (2, 'row 2');
            """.trimIndent(),
        )
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(7)

        migratedConnection.prepare("SELECT rowid, * FROM ${Tables.LIBRARY_FTS_MEDIA}").use { stm ->
            stm.step()
            assertEquals(1, stm.getLong(0))
            assertEquals("row 1", stm.getText(1))
            stm.step()
            assertEquals(2, stm.getLong(0))
            assertEquals("row 2", stm.getText(1))
        }

        migratedConnection.close()
    }

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate6To7SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(6)
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.LIBRARY_ALBUM}(${AlbumColumns.ID}, ${AlbumColumns.TITLE}, ${AlbumColumns.TRACK_COUNT}) VALUES (1, 'row 1', 10);
            """.trimIndent(),
        )
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.LIBRARY_ALBUM}(${AlbumColumns.ID}, ${AlbumColumns.TITLE}, ${AlbumColumns.TRACK_COUNT}) VALUES (2, 'row 2', 12);
            """.trimIndent(),
        )
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(7)

        migratedConnection.prepare("SELECT rowid, * FROM ${Tables.LIBRARY_FTS_ALBUM}").use { stm ->
            stm.step()
            assertEquals(1, stm.getLong(0))
            assertEquals("row 1", stm.getText(1))
            stm.step()
            assertEquals(2, stm.getLong(0))
            assertEquals("row 2", stm.getText(1))
        }

        migratedConnection.close()
    }

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate6To7SyncArtistTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(6)
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.LIBRARY_ARTIST}(${ArtistColumns.ID}, ${ArtistColumns.NAME}, ${ArtistColumns.TRACK_COUNT}) VALUES (1, 'row 1', 10);
            """.trimIndent(),
        )
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.LIBRARY_ARTIST}(${ArtistColumns.ID}, ${ArtistColumns.NAME}, ${ArtistColumns.TRACK_COUNT}) VALUES (2, 'row 2', 12);
            """.trimIndent(),
        )
        newConnection.close()

        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(7)

        migratedConnection.prepare("SELECT rowid, * FROM ${Tables.LIBRARY_FTS_ARTIST}").use { stm ->
            stm.step()
            assertEquals(1, stm.getLong(0))
            assertEquals("row 1", stm.getText(1))
            stm.step()
            assertEquals(2, stm.getLong(0))
            assertEquals("row 2", stm.getText(1))
        }

        migratedConnection.close()
    }

    @Test
    @IgnoreAndroidUnitTest
    @IgnoreNativeTest
    fun migrate7To8SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(7)
        newConnection.close()
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(8)
        migratedConnection.close()
    }
}
