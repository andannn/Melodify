/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.CustomTabColumns
import com.andannn.melodify.core.database.entity.CustomTabType.ALL_MUSIC
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.PlayListColumns
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractMigrationTest {
    private val tempFile =
        FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("test-${Random.nextInt()}.db")

    abstract fun getMigrationTestHelper(fileName: String): MigrationTestHelper

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
    @IgnoreNativeTest
    fun migrate7To8SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(7)
        newConnection.execSQL(
            """
                         INSERT INTO ${Tables.CUSTOM_TAB} 
            (${CustomTabColumns.ID}, ${CustomTabColumns.TYPE}, ${CustomTabColumns.NAME}, ${CustomTabColumns.EXTERNAL_ID})
            VALUES (4, 'album_detail', 'Album1', 'A01')
            """.trimIndent(),
        )
        newConnection.close()
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(8)
        migratedConnection
            .prepare("SELECT ${CustomTabColumns.ID}, ${CustomTabColumns.SORT_ORDER} FROM ${Tables.CUSTOM_TAB}")
            .use { stm ->
                stm.step()
                assertEquals(4, stm.getInt(0))
                assertEquals(4, stm.getInt(1))
            }
        migratedConnection.close()
    }

    @Test
    @IgnoreNativeTest
    fun migrate8To9SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(8)
        newConnection.execSQL(
            """
            INSERT INTO ${Tables.CUSTOM_TAB} 
            (${CustomTabColumns.ID}, ${CustomTabColumns.TYPE}, ${CustomTabColumns.NAME}, ${CustomTabColumns.EXTERNAL_ID}, ${CustomTabColumns.DISPLAY_SETTING})
            VALUES (4, 'album_detail', 'Album1', 'A01', '{"primary_group_sort":{"type":"com.andannn.melodify.domain.model.SortOption.Album","ascending":true},"content_sort":{"type":"com.andannn.melodify.domain.model.SortOption.TrackNum","ascending":true},"show_track_num":true,"is_preset":false}')
            """.trimIndent(),
        )
        newConnection.close()
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(9)
        migratedConnection.close()
    }

    @Test
    @IgnoreNativeTest
    fun migrate9To10SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(9)
        newConnection.close()
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(10)
        migratedConnection.close()
    }

    @Test
    @IgnoreNativeTest
    fun migrate10To11SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(10)
        newConnection.close()
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(11)
        migratedConnection.close()
    }

    @Test
    @IgnoreNativeTest
    fun migrate11To12SyncAlbumTableTest() {
        val migrationTestHelper =
            getMigrationTestHelper(
                tempFile.toString(),
            )
        val newConnection = migrationTestHelper.createDatabase(11)
        newConnection.execSQL(
            """
                INSERT OR IGNORE INTO play_list_table (play_list_id, play_list_created_date, play_list_name, play_list_artwork_uri)
                VALUES (0, 0, 'My Favorite Songs', '');
                """,
        )
        newConnection.close()
        val migratedConnection =
            migrationTestHelper.runMigrationsAndValidate(12)
        migratedConnection
            .prepare("SELECT ${PlayListColumns.IS_FAVORITE_PLAYLIST}, ${PlayListColumns.IS_AUDIO_PLAYLIST} FROM ${Tables.PLAY_LIST}")
            .use { stm ->
                stm.step()
                assertEquals(true, stm.getBoolean(0))
                assertEquals(true, stm.getBoolean(1))
            }
        migratedConnection.close()
    }
}
