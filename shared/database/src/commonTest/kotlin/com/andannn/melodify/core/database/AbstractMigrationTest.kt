/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.entity.TabType.ALL_MUSIC
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
            migratedConnection.prepare("SELECT * FROM custom_tab_table").use { stmt ->
                assertTrue { stmt.step() }
                assertEquals(ALL_MUSIC, stmt.getText(1))
            }
            migratedConnection.close()
        }

    @Test
    fun migrate5To6Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(5)
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(6)
            migratedConnection.close()
        }

    @Test
    fun migrate6To7Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(6)
            newConnection.execSQL(
                """
                INSERT INTO library_media_table(media_id, media_title) VALUES (1, 'row 1');
                """.trimIndent(),
            )
            newConnection.execSQL(
                """
                INSERT INTO library_media_table(media_id, media_title) VALUES (2, 'row 2');
                """.trimIndent(),
            )
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(7)

            migratedConnection
                .prepare("SELECT rowid, * FROM library_fts_media_table")
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
                INSERT INTO library_album_table(album_id, album_title, album_track_count) VALUES (1, 'row 1', 10);
                """.trimIndent(),
            )
            newConnection.execSQL(
                """
                INSERT INTO library_album_table(album_id, album_title, album_track_count) VALUES (2, 'row 2', 12);
                """.trimIndent(),
            )
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(7)

            migratedConnection
                .prepare("SELECT rowid, * FROM library_fts_album_table")
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
                INSERT INTO library_artist_table(artist_id, artist_name, artist_track_count) VALUES (1, 'row 1', 10);
                """.trimIndent(),
            )
            newConnection.execSQL(
                """
                INSERT INTO library_artist_table(artist_id, artist_name, artist_track_count) VALUES (2, 'row 2', 12);
                """.trimIndent(),
            )
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(7)

            migratedConnection
                .prepare("SELECT rowid, * FROM library_fts_artist_table")
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
    fun migrate7To8Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(7)
            newConnection.execSQL(
                """
                             INSERT INTO custom_tab_table 
                (custom_tab_id, custom_tab_type, custom_tab_name, custom_tab_external_id)
                VALUES (4, 'album_detail', 'Album1', 'A01')
                """.trimIndent(),
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(8)
            migratedConnection
                .prepare("SELECT custom_tab_id, sort_order FROM custom_tab_table")
                .use { stm ->
                    stm.step()
                    assertEquals(4, stm.getInt(0))
                    assertEquals(4, stm.getInt(1))
                }
            migratedConnection.close()
        }

    @Test
    fun migrate8To9Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(8)
            newConnection.execSQL(
                """
                INSERT INTO custom_tab_table 
                (custom_tab_id, custom_tab_type, custom_tab_name, custom_tab_external_id, display_setting)
                VALUES (4, 'album_detail', 'Album1', 'A01', '{"primary_group_sort":{"type":"com.andannn.melodify.domain.model.SortOption.Album","ascending":true},"content_sort":{"type":"com.andannn.melodify.domain.model.SortOption.TrackNum","ascending":true},"show_track_num":true,"is_preset":false}')
                """.trimIndent(),
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(9)
            migratedConnection.close()
        }

    @Test
    fun migrate9To10Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(9)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(10)
            migratedConnection.close()
        }

    @Test
    fun migrate10To11Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(10)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(11)
            migratedConnection.close()
        }

    @Test
    fun migrate11To12Test(): Unit =
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
                .prepare("SELECT is_favorite_playlist, is_audio_playlist FROM play_list_table")
                .use { stm ->
                    stm.step()
                    assertEquals(true, stm.getBoolean(0))
                    assertEquals(true, stm.getBoolean(1))
                }
            migratedConnection.close()
        }

    @Test
    fun migrate12To13Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(12)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(13)
            migratedConnection.close()
        }

    @Test
    fun migrate13To14Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(13)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(14)
            migratedConnection.close()
        }

    @Test
    fun migrate15To16Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(15)
            val mediaSql = """
            INSERT INTO library_media_table (
                media_id, 
                media_album_id, 
                media_artist_id,
                media_title
            ) VALUES (?, 1, 10, ?)
        """
            listOf(
                100L to "Song A",
                101L to "Song B",
            ).forEach {
                newConnection
                    .prepare(mediaSql)
                    .apply {
                        bindLong(1, it.first)
                        bindText(2, it.second)
                    }.use { it.step() }
            }
            newConnection.execSQL(
                """
            INSERT INTO library_album_table (
                album_id, 
                album_title, 
                album_track_count
            ) VALUES (1, 'Test Album', 0)
        """,
            )
            newConnection.execSQL(
                """
            INSERT INTO library_artist_table (
                artist_id, 
                artist_name, 
                artist_track_count
            ) VALUES (10, 'Test Artist', 0)
        """,
            )
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(16)

            migratedConnection
                .prepare("SELECT album_track_count FROM library_album_table")
                .use {
                    it.step()
                    assertEquals(2, it.getInt(0))
                }
            migratedConnection
                .prepare("SELECT artist_track_count FROM library_artist_table")
                .use {
                    it.step()
                    assertEquals(2, it.getInt(0))
                }
            migratedConnection.close()
        }

    @Test
    fun migrate16To17Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(16)
            newConnection.execSQL(
                """
            INSERT INTO library_artist_table (
                artist_id, 
                artist_name, 
                artist_track_count
            ) VALUES (10, 'Test Artist', 0)
        """,
            )
            newConnection.execSQL(
                """
            INSERT INTO library_album_table (
                album_id, 
                album_title, 
                album_track_count
            ) VALUES (1, 'Test Album', 0)
        """,
            )
            newConnection.close()

            val migratedConnection =
                helper.runMigrationsAndValidate(17)
            migratedConnection.execSQL(
                """
            INSERT INTO library_artist_table (
                artist_id, 
                artist_name, 
                artist_track_count
            ) VALUES (11, 'Test Artist 2', 0)
        """,
            )
            migratedConnection.execSQL(
                """
            INSERT INTO library_album_table (
                album_id, 
                album_title, 
                album_track_count
            ) VALUES (2, 'Test Album 2', 0)
        """,
            )
            migratedConnection
                .prepare(
                    "SELECT album_title FROM library_fts_album_table",
                ).use {
                    it.step()
                    assertEquals("Test Album", it.getText(0))
                    it.step()
                    assertEquals("Test Album 2", it.getText(0))
                    assertEquals(false, it.step())
                }
            migratedConnection
                .prepare(
                    "SELECT artist_name FROM library_fts_artist_table",
                ).use {
                    it.step()
                    assertEquals("Test Artist", it.getText(0))
                    it.step()
                    assertEquals("Test Artist 2", it.getText(0))
                    assertEquals(false, it.step())
                }
            migratedConnection.close()
        }

    @Test
    fun migrate17To18Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(17)
            newConnection.execSQL(
                "INSERT INTO `lyric_table` (`lyric_id`, `lyric_name`, `lyric_track_name`, `lyric_artist_name`, `lyric_album_name`, `lyric_duration_name`, `lyric_instrumental`, `lyric_plain_lyrics`, `lyric_synced_lyrics`) VALUES (1, 'example.lrc', 'Example Track', 'Example Artist', 'Example Album', 240.5, 0, 'Example Plain Lyrics', '[00:00.00]Example Synced Lyrics');",
            )
            newConnection.execSQL(
                "INSERT INTO `lyric_with_audio_cross_ref_table` (`lyric_with_audio_cross_ref_media_store_id`, `lyric_with_audio_cross_ref_lyric_id`) VALUES ('123', 1);",
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(18, migrations = listOf(Migration17To18Spec))
            migratedConnection.close()
        }

    @Test
    fun migrate18To19Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(18)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(19, migrations = listOf())
            migratedConnection.close()
        }

    @Test
    fun migrate19To20Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(19)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(20, migrations = listOf())
            migratedConnection.close()
        }

    @Test
    fun migrate20To21Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(20)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(21, migrations = listOf())
            migratedConnection.close()
        }

    @Test
    fun migrate21To22Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(21)
            newConnection.execSQL(
                """
                INSERT INTO play_list_table (play_list_id, play_list_created_date, play_list_name, play_list_artwork_uri)
                VALUES (0, 0, 'My Favorite Songs', '');
                """.trimIndent(),
            )
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(22, migrations = listOf())
            migratedConnection
                .prepare(
                    """
                    SELECT EXISTS (SELECT 1 FROM play_list_fts_table WHERE play_list_name = 'My Favorite Songs')
                    """.trimIndent(),
                ).use {
                    it.step()
                    assertEquals(true, it.getInt(0) == 1)
                }
            migratedConnection.close()
        }

    @Test
    fun migrate22To23Test(): Unit =
        helper.let { helper ->
            val newConnection = helper.createDatabase(22)
            newConnection.close()
            val migratedConnection =
                helper.runMigrationsAndValidate(23, migrations = listOf())
            migratedConnection.close()
        }
}
