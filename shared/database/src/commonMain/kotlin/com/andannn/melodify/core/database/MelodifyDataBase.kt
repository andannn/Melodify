/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.foreignKeyCheck
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.dao.internal.MediaEntityRawQueryDao
import com.andannn.melodify.core.database.dao.internal.PlayListRawQueryDao
import com.andannn.melodify.core.database.dao.internal.SyncerDao
import com.andannn.melodify.core.database.dao.internal.VideoEntityRawQueryDao
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.AlbumFtsEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.ArtistFtsEntity
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.CustomTabSettingEntity
import com.andannn.melodify.core.database.entity.CustomTabSortRuleEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.MediaFtsEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListItemEntryEntity
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.database.entity.SortOptionJsonConverter
import com.andannn.melodify.core.database.entity.TabEntity
import com.andannn.melodify.core.database.entity.TabPresetDisplaySettingEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.entity.VideoFtsEntity
import com.andannn.melodify.core.database.entity.VideoPlayProgressEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        LyricEntity::class,
        PlayListEntity::class,
        PlayListItemEntryEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        GenreEntity::class,
        AudioEntity::class,
        TabEntity::class,
        AlbumFtsEntity::class,
        ArtistFtsEntity::class,
        MediaFtsEntity::class,
        SearchHistoryEntity::class,
        CustomTabSortRuleEntity::class,
        VideoEntity::class,
        VideoFtsEntity::class,
        VideoPlayProgressEntity::class,
        CustomTabSettingEntity::class,
        TabPresetDisplaySettingEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5, AutoMigration4To5Spec::class),
        AutoMigration(from = 5, to = 6, AutoMigration5To6Spec::class),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, AutoMigration7To8Spec::class),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12, AutoMigration11To12Spec::class),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 14, to = 15, AutoMigration14To15Spec::class),
        AutoMigration(from = 15, to = 16, AutoMigration15To16Spec::class),
        AutoMigration(from = 16, to = 17, AutoMigration16To17Spec::class),
        AutoMigration(from = 18, to = 19),
        AutoMigration(from = 19, to = 20, AutoMigration19To20Spec::class),
        AutoMigration(from = 20, to = 21, AutoMigration20To21Spec::class),
    ],
    version = 21,
)
@TypeConverters(SortOptionJsonConverter::class)
@ConstructedBy(MelodifyDataBaseConstructor::class)
abstract class MelodifyDataBase : RoomDatabase() {
    abstract fun getLyricDao(): LyricDao

    abstract fun getPlayListDao(): PlayListDao

    abstract fun getMediaLibraryDao(): MediaLibraryDao

    abstract fun getUserDataDao(): UserDataDao

    internal abstract fun getMediaEntityRawQueryDao(): MediaEntityRawQueryDao

    internal abstract fun getVideoFlowPagingSource(): VideoEntityRawQueryDao

    internal abstract fun getPlayListRawQueryDao(): PlayListRawQueryDao

    internal abstract fun getSyncerDao(): SyncerDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MelodifyDataBaseConstructor : RoomDatabaseConstructor<MelodifyDataBase> {
    override fun initialize(): MelodifyDataBase
}

internal fun <T : RoomDatabase> RoomDatabase.Builder<T>.setUpDatabase() =
    apply {
        setQueryCoroutineContext(Dispatchers.IO)
        addCallback(addTriggerCallback)
        addCallback(addInitialCustomTabsCallback)
        addMigrations(Migration17To18Spec)
    }

internal val addTriggerCallback =
    object : RoomDatabase.Callback() {
        override fun onCreate(connection: SQLiteConnection) {
            super.onCreate(connection)

            // update artist song count when insert or delete media.
            connection.execSQL(
                """
                CREATE TRIGGER update_artist_song_count_on_insert
                AFTER INSERT ON library_media_table
                FOR EACH ROW
                WHEN NEW.media_artist_id IS NOT NULL
                BEGIN
                    UPDATE library_artist_table
                    SET artist_track_count = artist_track_count + 1
                    WHERE "artist_id" = NEW.media_artist_id;
                END;
                """.trimIndent(),
            )

            connection.execSQL(
                """
                CREATE TRIGGER update_artist_song_count_on_delete
                AFTER DELETE ON library_media_table
                FOR EACH ROW
                WHEN OLD.media_artist_id IS NOT NULL
                BEGIN
                    UPDATE library_artist_table
                    SET artist_track_count = artist_track_count - 1
                    WHERE "artist_id" = OLD.media_artist_id;
                END;
                """.trimIndent(),
            )

            // update album song count when insert or delete media.
            connection.execSQL(
                """
                CREATE TRIGGER update_album_song_count_on_insert
                AFTER INSERT ON library_media_table
                FOR EACH ROW
                WHEN NEW.media_album_id IS NOT NULL
                BEGIN
                    UPDATE library_album_table
                    SET album_track_count = album_track_count + 1
                    WHERE album_id = NEW.media_album_id;
                END;
                """.trimIndent(),
            )

            connection.execSQL(
                """
                CREATE TRIGGER update_album_song_count_on_delete
                AFTER DELETE ON library_media_table
                FOR EACH ROW
                WHEN OLD.media_album_id IS NOT NULL
                BEGIN
                    UPDATE library_album_table
                    SET album_track_count = album_track_count - 1
                    WHERE album_id = OLD.media_album_id;
                END;
                """.trimIndent(),
            )
        }
    }

internal val addInitialCustomTabsCallback =
    object : RoomDatabase.Callback() {
        override fun onCreate(connection: SQLiteConnection) {
            connection.execSQL("INSERT INTO custom_tab_table (custom_tab_type, sort_order) VALUES ('all_music', 0)")
        }
    }

class AutoMigration4To5Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            INSERT INTO custom_tab_table (custom_tab_type) VALUES ('all_music')
            """.trimIndent(),
        )
    }
}

internal class AutoMigration5To6Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        createUpdateTrackCountTrigger(connection)
    }

    private fun createUpdateTrackCountTrigger(connection: SQLiteConnection) {
        // delete invalid albums, artists, genres when delete media.
        connection.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS delete_invalid_albums_artists_genres
            AFTER DELETE ON library_media_table
            BEGIN
                DELETE FROM library_album_table WHERE album_id NOT IN (SELECT DISTINCT media_album_id FROM library_media_table WHERE media_album_id IS NOT NULL);
                DELETE FROM library_artist_table WHERE "artist_id" NOT IN (SELECT DISTINCT media_artist_id FROM library_media_table WHERE media_artist_id IS NOT NULL);
                DELETE FROM library_genre_table WHERE genre_id NOT IN (SELECT DISTINCT media_genre_id FROM library_media_table WHERE media_genre_id IS NOT NULL);
            END;
            """.trimIndent(),
        )

        // update artist song count when insert or delete media.
        connection.execSQL(
            """
            CREATE TRIGGER update_artist_song_count_on_insert
            AFTER INSERT ON library_media_table
            FOR EACH ROW
            WHEN NEW.media_artist_id IS NOT NULL
            BEGIN
                UPDATE library_artist_table
                SET artist_track_count = artist_track_count + 1
                WHERE "artist_id" = NEW.media_artist_id;
            END;
            """.trimIndent(),
        )

        connection.execSQL(
            """
            CREATE TRIGGER update_artist_song_count_on_delete
            AFTER DELETE ON library_media_table
            FOR EACH ROW
            WHEN OLD.media_artist_id IS NOT NULL
            BEGIN
                UPDATE library_artist_table
                SET artist_track_count = artist_track_count - 1
                WHERE "artist_id" = OLD.media_artist_id;
            END;
            """.trimIndent(),
        )

        // update album song count when insert or delete media.
        connection.execSQL(
            """
            CREATE TRIGGER update_album_song_count_on_insert
            AFTER INSERT ON library_media_table
            FOR EACH ROW
            WHEN NEW.media_album_id IS NOT NULL
            BEGIN
                UPDATE library_album_table
                SET album_track_count = album_track_count + 1
                WHERE album_id = NEW.media_album_id;
            END;
            """.trimIndent(),
        )

        connection.execSQL(
            """
            CREATE TRIGGER update_album_song_count_on_delete
            AFTER DELETE ON library_media_table
            FOR EACH ROW
            WHEN OLD.media_album_id IS NOT NULL
            BEGIN
                UPDATE library_album_table
                SET album_track_count = album_track_count - 1
                WHERE album_id = OLD.media_album_id;
            END;
            """.trimIndent(),
        )
    }
}

internal class AutoMigration7To8Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            UPDATE custom_tab_table 
            SET sort_order = custom_tab_id
            """.trimIndent(),
        )
    }
}

internal class AutoMigration11To12Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        // Set is_favorite_playlist column to true for favorite play list.
        connection.execSQL(
            """
            UPDATE play_list_table 
            SET is_favorite_playlist = 1
            WHERE play_list_id = 0
            """.trimIndent(),
        )

        connection.execSQL(
            """
            UPDATE play_list_table 
            SET is_audio_playlist = 1
            """.trimIndent(),
        )
    }
}

internal class AutoMigration14To15Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            "DROP TRIGGER IF EXISTS delete_invalid_albums_artists_genres",
        )
    }
}

internal class AutoMigration15To16Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            UPDATE library_album_table
            SET album_track_count = (
                SELECT COUNT(*) FROM library_media_table 
                WHERE library_media_table.media_album_id = library_album_table.album_id
            )
            """.trimIndent(),
        )
        connection.execSQL(
            """
            UPDATE library_artist_table
            SET artist_track_count = (
                SELECT COUNT(*) FROM library_media_table 
                WHERE library_media_table.media_artist_id = library_artist_table."artist_id"
            )
            """.trimIndent(),
        )
    }
}

internal class AutoMigration16To17Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_artist_table_BEFORE_UPDATE BEFORE UPDATE ON `library_artist_table` BEGIN DELETE FROM `library_fts_artist_table` WHERE `docid`=OLD.`rowid`; END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_artist_table_BEFORE_DELETE BEFORE DELETE ON `library_artist_table` BEGIN DELETE FROM `library_fts_artist_table` WHERE `docid`=OLD.`rowid`; END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_artist_table_AFTER_UPDATE AFTER UPDATE ON `library_artist_table` BEGIN INSERT INTO `library_fts_artist_table`(`docid`, `artist_name`) VALUES (NEW.`rowid`, NEW.`artist_name`); END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_artist_table_AFTER_INSERT AFTER INSERT ON `library_artist_table` BEGIN INSERT INTO `library_fts_artist_table`(`docid`, `artist_name`) VALUES (NEW.`rowid`, NEW.`artist_name`); END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_album_table_BEFORE_UPDATE BEFORE UPDATE ON `library_album_table` BEGIN DELETE FROM `library_fts_album_table` WHERE `docid`=OLD.`rowid`; END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_album_table_BEFORE_DELETE BEFORE DELETE ON `library_album_table` BEGIN DELETE FROM `library_fts_album_table` WHERE `docid`=OLD.`rowid`; END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_album_table_AFTER_UPDATE AFTER UPDATE ON `library_album_table` BEGIN INSERT INTO `library_fts_album_table`(`docid`, `album_title`) VALUES (NEW.`rowid`, NEW.`album_title`); END",
        )
        connection.execSQL(
            "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_library_fts_album_table_AFTER_INSERT AFTER INSERT ON `library_album_table` BEGIN INSERT INTO `library_fts_album_table`(`docid`, `album_title`) VALUES (NEW.`rowid`, NEW.`album_title`); END",
        )

        connection.execSQL("INSERT INTO `library_fts_artist_table`(`library_fts_artist_table`) VALUES('rebuild')")
        connection.execSQL("INSERT INTO `library_fts_album_table`(`library_fts_album_table`) VALUES('rebuild')")
    }
}

internal object Migration17To18Spec : Migration(17, 18) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE `lyric_with_audio_cross_ref_table`")

        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `_new_lyric_table` (
                `lyric_id` INTEGER NOT NULL, 
                `media_id` INTEGER NOT NULL DEFAULT 0, 
                `lyric_name` TEXT NOT NULL, 
                `lyric_track_name` TEXT NOT NULL, 
                `lyric_artist_name` TEXT NOT NULL, 
                `lyric_album_name` TEXT NOT NULL, 
                `lyric_duration_name` REAL NOT NULL, 
                `lyric_instrumental` INTEGER NOT NULL, 
                `lyric_plain_lyrics` TEXT NOT NULL, 
                `lyric_synced_lyrics` TEXT NOT NULL, 
                PRIMARY KEY(`lyric_id`), 
                FOREIGN KEY(`media_id`) REFERENCES `library_media_table`(`media_id`) ON UPDATE NO ACTION ON DELETE CASCADE 
            )
            """.trimIndent(),
        )
        connection.execSQL("DROP TABLE `lyric_table`")
        connection.execSQL("ALTER TABLE `_new_lyric_table` RENAME TO `lyric_table`")
        foreignKeyCheck(connection, "lyric_table")
    }
}

@DeleteTable(tableName = "play_list_with_media_cross_ref_table")
@DeleteColumn(tableName = "play_list_table", columnName = "is_audio_playlist")
@DeleteColumn(tableName = "custom_tab_table", columnName = "display_setting")
@RenameTable(fromTableName = "video_tab_setting_table", toTableName = "custom_tab_setting_table")
internal class AutoMigration19To20Spec : AutoMigrationSpec

@RenameColumn(tableName = "custom_tab_setting_table", fromColumnName = "custom_tab_foreign_key", toColumnName = "custom_tab_id")
@RenameColumn(tableName = "custom_tab_setting_table", fromColumnName = "is_show_progress", toColumnName = "is_show_video_progress")
@RenameColumn(tableName = "sort_rule_table", fromColumnName = "custom_tab_foreign_key", toColumnName = "custom_tab_id")
@DeleteColumn(tableName = "sort_rule_table", columnName = "show_track_num")
@DeleteColumn(tableName = "sort_rule_table", columnName = "is_preset")
internal class AutoMigration20To21Spec : AutoMigrationSpec
