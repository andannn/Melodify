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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractMigrationTest {
    companion object {
        const val TEST_DB = "migration-test"
    }

    abstract val helper: MigrationTestHelper

    @Test
    fun migrate3To4(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(3)
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(4)
            migratedConnection.close()
        }

    @Test
    fun migrate4To5(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(4)
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(5)
            migratedConnection.prepare("SELECT * FROM ${Tables.CUSTOM_TAB}").use { stmt ->
                assertTrue { stmt.step() }
                assertEquals(ALL_MUSIC, stmt.getText(1))
            }
            migratedConnection.close()
        }

    @Test
    fun migrate5To6(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(5)
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(6)
            migratedConnection.close()
        }

    @Test
    fun migrate6To7SyncMediaTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(6)
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
                helper.runMigrationsAndValidate(7)

            migratedConnection
                .prepare("SELECT rowid, * FROM ${Tables.LIBRARY_FTS_MEDIA}")
                .use { stm ->
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
    fun migrate6To7SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(6)
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
                helper.runMigrationsAndValidate(7)

            migratedConnection
                .prepare("SELECT rowid, * FROM ${Tables.LIBRARY_FTS_ALBUM}")
                .use { stm ->
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
    fun migrate6To7SyncArtistTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(6)
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
                helper.runMigrationsAndValidate(7)

            migratedConnection
                .prepare("SELECT rowid, * FROM ${Tables.LIBRARY_FTS_ARTIST}")
                .use { stm ->
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
    fun migrate7To8SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(7)
            newConnection.execSQL(
                """
                             INSERT INTO ${Tables.CUSTOM_TAB} 
                (${CustomTabColumns.ID}, ${CustomTabColumns.TYPE}, ${CustomTabColumns.NAME}, ${CustomTabColumns.EXTERNAL_ID})
                VALUES (4, 'album_detail', 'Album1', 'A01')
                """.trimIndent(),
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(8)
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
    fun migrate8To9SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(8)
            newConnection.execSQL(
                """
                INSERT INTO ${Tables.CUSTOM_TAB} 
                (${CustomTabColumns.ID}, ${CustomTabColumns.TYPE}, ${CustomTabColumns.NAME}, ${CustomTabColumns.EXTERNAL_ID}, ${CustomTabColumns.DISPLAY_SETTING})
                VALUES (4, 'album_detail', 'Album1', 'A01', '{"primary_group_sort":{"type":"com.andannn.melodify.domain.model.SortOption.Album","ascending":true},"content_sort":{"type":"com.andannn.melodify.domain.model.SortOption.TrackNum","ascending":true},"show_track_num":true,"is_preset":false}')
                """.trimIndent(),
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(9)
            migratedConnection.close()
        }

    @Test
    fun migrate9To10SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(9)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(10)
            migratedConnection.close()
        }

    @Test
    fun migrate10To11SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(10)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(11)
            migratedConnection.close()
        }

    @Test
    fun migrate11To12SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(11)
            newConnection.execSQL(
                """
                INSERT OR IGNORE INTO play_list_table (play_list_id, play_list_created_date, play_list_name, play_list_artwork_uri)
                VALUES (0, 0, 'My Favorite Songs', '');
                """,
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(12)
            migratedConnection
                .prepare("SELECT ${PlayListColumns.IS_FAVORITE_PLAYLIST}, ${PlayListColumns.IS_AUDIO_PLAYLIST} FROM ${Tables.PLAY_LIST}")
                .use { stm ->
                    stm.step()
                    assertEquals(true, stm.getBoolean(0))
                    assertEquals(true, stm.getBoolean(1))
                }
            migratedConnection.close()
        }

    @Test
    fun migrate12To13SyncAlbumTableTest(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(12)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(13)
            migratedConnection.close()
        }
}
