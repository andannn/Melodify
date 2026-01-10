/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.CustomTabColumns
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.GenreColumns
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.LyricWithAudioCrossRef
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListColumns
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.database.entity.SortOptionJsonConverter
import com.andannn.melodify.core.database.entity.SortRuleEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.entity.VideoPlayProgressEntity
import com.andannn.melodify.core.database.entity.VideoTabSettingEntity
import com.andannn.melodify.core.database.entity.fts.AlbumFtsEntity
import com.andannn.melodify.core.database.entity.fts.ArtistFtsEntity
import com.andannn.melodify.core.database.entity.fts.MediaFtsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal object Tables {
    const val LYRIC = "lyric_table"
    const val LYRIC_WITH_AUDIO_CROSS_REF = "lyric_with_audio_cross_ref_table"
    const val PLAY_LIST = "play_list_table"
    const val PLAY_LIST_WITH_MEDIA_CROSS_REF = "play_list_with_media_cross_ref_table"
    const val LIBRARY_VIDEO = "library_video_table"
    const val LIBRARY_MEDIA = "library_media_table"
    const val LIBRARY_FTS_MEDIA = "library_fts_media_table"
    const val LIBRARY_ALBUM = "library_album_table"
    const val LIBRARY_FTS_ALBUM = "library_fts_album_table"
    const val LIBRARY_ARTIST = "library_artist_table"
    const val LIBRARY_FTS_ARTIST = "library_fts_artist_table"
    const val LIBRARY_GENRE = "library_genre_table"
    const val CUSTOM_TAB = "custom_tab_table"
    const val SEARCH_HISTORY = "search_history_table"
    const val SORT_RULE = "sort_rule_table"
    const val VIDEO_PLAY_PROGRESS = "video_play_progress_table"
    const val VIDEO_TAB_SETTING = "video_tab_setting_table"
}

@Database(
    entities = [
        LyricEntity::class,
        LyricWithAudioCrossRef::class,
        PlayListEntity::class,
        PlayListWithMediaCrossRef::class,
        AlbumEntity::class,
        ArtistEntity::class,
        GenreEntity::class,
        MediaEntity::class,
        CustomTabEntity::class,
        AlbumFtsEntity::class,
        ArtistFtsEntity::class,
        MediaFtsEntity::class,
        SearchHistoryEntity::class,
        SortRuleEntity::class,
        VideoEntity::class,
        VideoPlayProgressEntity::class,
        VideoTabSettingEntity::class,
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
    ],
    version = 15,
)
@TypeConverters(SortOptionJsonConverter::class)
@ConstructedBy(MelodifyDataBaseConstructor::class)
abstract class MelodifyDataBase : RoomDatabase() {
    abstract fun getLyricDao(): LyricDao

    abstract fun getPlayListDao(): PlayListDao

    abstract fun getMediaLibraryDao(): MediaLibraryDao

    abstract fun getUserDataDao(): UserDataDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MelodifyDataBaseConstructor : RoomDatabaseConstructor<MelodifyDataBase> {
    override fun initialize(): MelodifyDataBase
}

fun <T : RoomDatabase> RoomDatabase.Builder<T>.setUpDatabase() =
    apply {
        setQueryCoroutineContext(Dispatchers.IO)
        addCallback(addTriggerCallback)
        addCallback(addInitialCustomTabsCallback)
    }

internal val addTriggerCallback =
    object : RoomDatabase.Callback() {
        override fun onCreate(connection: SQLiteConnection) {
            super.onCreate(connection)

            // update artist song count when insert or delete media.
            connection.execSQL(
                """
                CREATE TRIGGER update_artist_song_count_on_insert
                AFTER INSERT ON ${Tables.LIBRARY_MEDIA}
                FOR EACH ROW
                WHEN NEW.${MediaColumns.ARTIST_ID} IS NOT NULL
                BEGIN
                    UPDATE ${Tables.LIBRARY_ARTIST}
                    SET ${ArtistColumns.TRACK_COUNT} = ${ArtistColumns.TRACK_COUNT} + 1
                    WHERE ${ArtistColumns.ID} = NEW.${MediaColumns.ARTIST_ID};
                END;
                """.trimIndent(),
            )

            connection.execSQL(
                """
                CREATE TRIGGER update_artist_song_count_on_delete
                AFTER DELETE ON ${Tables.LIBRARY_MEDIA}
                FOR EACH ROW
                WHEN OLD.${MediaColumns.ARTIST_ID} IS NOT NULL
                BEGIN
                    UPDATE ${Tables.LIBRARY_ARTIST}
                    SET ${ArtistColumns.TRACK_COUNT} = ${ArtistColumns.TRACK_COUNT} - 1
                    WHERE ${ArtistColumns.ID} = OLD.${MediaColumns.ARTIST_ID};
                END;
                """.trimIndent(),
            )

            // update album song count when insert or delete media.
            connection.execSQL(
                """
                CREATE TRIGGER update_album_song_count_on_insert
                AFTER INSERT ON ${Tables.LIBRARY_MEDIA}
                FOR EACH ROW
                WHEN NEW.${MediaColumns.ALBUM_ID} IS NOT NULL
                BEGIN
                    UPDATE ${Tables.LIBRARY_ALBUM}
                    SET ${AlbumColumns.TRACK_COUNT} = ${AlbumColumns.TRACK_COUNT} + 1
                    WHERE ${AlbumColumns.ID} = NEW.${MediaColumns.ALBUM_ID};
                END;
                """.trimIndent(),
            )

            connection.execSQL(
                """
                CREATE TRIGGER update_album_song_count_on_delete
                AFTER DELETE ON ${Tables.LIBRARY_MEDIA}
                FOR EACH ROW
                WHEN OLD.${MediaColumns.ALBUM_ID} IS NOT NULL
                BEGIN
                    UPDATE ${Tables.LIBRARY_ALBUM}
                    SET ${AlbumColumns.TRACK_COUNT} = ${AlbumColumns.TRACK_COUNT} - 1
                    WHERE ${AlbumColumns.ID} = OLD.${MediaColumns.ALBUM_ID};
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

class AutoMigration5To6Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        createUpdateTrackCountTrigger(connection)
    }

    private fun createUpdateTrackCountTrigger(connection: SQLiteConnection) {
        // delete invalid albums, artists, genres when delete media.
        connection.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS delete_invalid_albums_artists_genres
            AFTER DELETE ON ${Tables.LIBRARY_MEDIA}
            BEGIN
                DELETE FROM ${Tables.LIBRARY_ALBUM} WHERE ${AlbumColumns.ID} NOT IN (SELECT DISTINCT ${MediaColumns.ALBUM_ID} FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ALBUM_ID} IS NOT NULL);
                DELETE FROM ${Tables.LIBRARY_ARTIST} WHERE ${ArtistColumns.ID} NOT IN (SELECT DISTINCT ${MediaColumns.ARTIST_ID} FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ARTIST_ID} IS NOT NULL);
                DELETE FROM ${Tables.LIBRARY_GENRE} WHERE ${GenreColumns.ID} NOT IN (SELECT DISTINCT ${MediaColumns.GENRE_ID} FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.GENRE_ID} IS NOT NULL);
            END;
            """.trimIndent(),
        )

        // update artist song count when insert or delete media.
        connection.execSQL(
            """
            CREATE TRIGGER update_artist_song_count_on_insert
            AFTER INSERT ON ${Tables.LIBRARY_MEDIA}
            FOR EACH ROW
            WHEN NEW.${MediaColumns.ARTIST_ID} IS NOT NULL
            BEGIN
                UPDATE ${Tables.LIBRARY_ARTIST}
                SET ${ArtistColumns.TRACK_COUNT} = ${ArtistColumns.TRACK_COUNT} + 1
                WHERE ${ArtistColumns.ID} = NEW.${MediaColumns.ARTIST_ID};
            END;
            """.trimIndent(),
        )

        connection.execSQL(
            """
            CREATE TRIGGER update_artist_song_count_on_delete
            AFTER DELETE ON ${Tables.LIBRARY_MEDIA}
            FOR EACH ROW
            WHEN OLD.${MediaColumns.ARTIST_ID} IS NOT NULL
            BEGIN
                UPDATE ${Tables.LIBRARY_ARTIST}
                SET ${ArtistColumns.TRACK_COUNT} = ${ArtistColumns.TRACK_COUNT} - 1
                WHERE ${ArtistColumns.ID} = OLD.${MediaColumns.ARTIST_ID};
            END;
            """.trimIndent(),
        )

        // update album song count when insert or delete media.
        connection.execSQL(
            """
            CREATE TRIGGER update_album_song_count_on_insert
            AFTER INSERT ON ${Tables.LIBRARY_MEDIA}
            FOR EACH ROW
            WHEN NEW.${MediaColumns.ALBUM_ID} IS NOT NULL
            BEGIN
                UPDATE ${Tables.LIBRARY_ALBUM}
                SET ${AlbumColumns.TRACK_COUNT} = ${AlbumColumns.TRACK_COUNT} + 1
                WHERE ${AlbumColumns.ID} = NEW.${MediaColumns.ALBUM_ID};
            END;
            """.trimIndent(),
        )

        connection.execSQL(
            """
            CREATE TRIGGER update_album_song_count_on_delete
            AFTER DELETE ON ${Tables.LIBRARY_MEDIA}
            FOR EACH ROW
            WHEN OLD.${MediaColumns.ALBUM_ID} IS NOT NULL
            BEGIN
                UPDATE ${Tables.LIBRARY_ALBUM}
                SET ${AlbumColumns.TRACK_COUNT} = ${AlbumColumns.TRACK_COUNT} - 1
                WHERE ${AlbumColumns.ID} = OLD.${MediaColumns.ALBUM_ID};
            END;
            """.trimIndent(),
        )
    }
}

class AutoMigration7To8Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            UPDATE ${Tables.CUSTOM_TAB} 
            SET ${CustomTabColumns.SORT_ORDER} = ${CustomTabColumns.ID}
            """.trimIndent(),
        )
    }
}

class AutoMigration11To12Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        // Set is_favorite_playlist column to true for favorite play list.
        connection.execSQL(
            """
            UPDATE ${Tables.PLAY_LIST} 
            SET ${PlayListColumns.IS_FAVORITE_PLAYLIST} = 1
            WHERE ${PlayListColumns.ID} = 0
            """.trimIndent(),
        )

        connection.execSQL(
            """
            UPDATE ${Tables.PLAY_LIST} 
            SET ${PlayListColumns.IS_AUDIO_PLAYLIST} = 1
            """.trimIndent(),
        )
    }
}

class AutoMigration14To15Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            "DROP TRIGGER IF EXISTS delete_invalid_albums_artists_genres",
        )
    }
}
