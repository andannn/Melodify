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
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.dao.PlayListDao.Companion.FAVORITE_PLAY_LIST_ID
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
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
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
    const val LIBRARY_MEDIA = "library_media_table"
    const val LIBRARY_FTS_MEDIA = "library_fts_media_table"
    const val LIBRARY_ALBUM = "library_album_table"
    const val LIBRARY_FTS_ALBUM = "library_fts_album_table"
    const val LIBRARY_ARTIST = "library_artist_table"
    const val LIBRARY_FTS_ARTIST = "library_fts_artist_table"
    const val LIBRARY_GENRE = "library_genre_table"
    const val CUSTOM_TAB = "custom_tab_table"
    const val SEARCH_HISTORY = "search_history_table"
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
    ],
    autoMigrations = [
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5, AutoMigration4To5Spec::class),
        AutoMigration(from = 5, to = 6, AutoMigration5To6Spec::class),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, AutoMigration7To8Spec::class),
    ],
    version = 8,
)
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
        addCallback(addFavoritePlayListCallback)
        addCallback(addInitialCustomTabsCallback)
    }

internal val addTriggerCallback =
    object : RoomDatabase.Callback() {
        override fun onCreate(connection: SQLiteConnection) {
            super.onCreate(connection)

            createUpdateTrackCountTrigger(connection)
        }
    }

internal val addFavoritePlayListCallback =
    object : RoomDatabase.Callback() {
        override fun onCreate(connection: SQLiteConnection) {
            // Insert a default play list.
            connection.execSQL(
                """
                INSERT OR IGNORE INTO play_list_table (play_list_id, play_list_created_date, play_list_name, play_list_artwork_uri)
                VALUES ($FAVORITE_PLAY_LIST_ID, 0, 'My Favorite Songs', '');
                """.trimIndent(),
            )
        }
    }

internal val addInitialCustomTabsCallback =
    object : RoomDatabase.Callback() {
        override fun onCreate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                INSERT INTO custom_tab_table (custom_tab_type, sort_order) VALUES ('all_music', 0)
                """.trimIndent(),
            )
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

// Trigger is created by Room Automatically.
// class AutoMigration6To7Spec : AutoMigrationSpec {
//    override fun onPostMigrate(connection: SQLiteConnection) {
// //        createUpdateFtsTableTrigger(connection)
//
//        // Sync library to FTS table.
// //        syncLibraryData(connection)
//    }
//
//    private fun syncLibraryData(connection: SQLiteConnection) {
//        connection.execSQL(
//            """
//            INSERT INTO ${Tables.LIBRARY_FTS_MEDIA}(rowid, ${MediaColumns.TITLE})
//            SELECT ${MediaColumns.ID}, ${MediaColumns.TITLE} FROM ${Tables.LIBRARY_MEDIA}
//        """.trimIndent()
//        )
//
//        connection.execSQL(
//            """
//            INSERT INTO ${Tables.LIBRARY_FTS_ALBUM}(rowid, ${AlbumColumns.TITLE})
//            SELECT ${AlbumColumns.ID}, ${AlbumColumns.TITLE} FROM ${Tables.LIBRARY_ALBUM}
//        """.trimIndent()
//        )
//    }
// }
//
// private fun createUpdateFtsTableTrigger(connection: SQLiteConnection) {
//    connection.createFtsTableTrigger(
//        tableName = Tables.LIBRARY_ALBUM,
//        ftsTableName = Tables.LIBRARY_FTS_ALBUM,
//        deleteRowString = {
//            "DELETE FROM ${Tables.LIBRARY_FTS_ALBUM} WHERE rowid=old.${AlbumColumns.ID};"
//        },
//        insertRowString = {
//            "INSERT INTO ${Tables.LIBRARY_FTS_ALBUM}(rowid, ${AlbumColumns.TITLE}) VALUES (new.${AlbumColumns.ID}, new.${AlbumColumns.TITLE});"
//        }
//    )
//    connection.createFtsTableTrigger(
//        tableName = Tables.LIBRARY_ARTIST,
//        ftsTableName = Tables.LIBRARY_FTS_ARTIST,
//        deleteRowString = {
//            "DELETE FROM ${Tables.LIBRARY_FTS_ARTIST} WHERE rowid=old.${ArtistColumns.ID};"
//        },
//        insertRowString = {
//            "INSERT INTO ${Tables.LIBRARY_FTS_ARTIST}(rowid, ${ArtistColumns.NAME}) VALUES (new.${ArtistColumns.ID}, new.${ArtistColumns.NAME});"
//        }
//    )
//    connection.createFtsTableTrigger(
//        tableName = Tables.LIBRARY_MEDIA,
//        ftsTableName = Tables.LIBRARY_FTS_MEDIA,
//        deleteRowString = {
//            "DELETE FROM ${Tables.LIBRARY_FTS_MEDIA} WHERE rowid=old.${MediaColumns.ID};"
//        },
//        insertRowString = {
//            "INSERT INTO ${Tables.LIBRARY_FTS_MEDIA}(rowid, ${MediaColumns.TITLE}) VALUES (new.${MediaColumns.ID}, new.${MediaColumns.TITLE});"
//        }
//    )
// }
//
// private fun SQLiteConnection.createFtsTableTrigger(
//    tableName: String,
//    ftsTableName: String,
//    deleteRowString: () -> String,
//    insertRowString: () -> String
// ) {
//    execSQL(
//        """
//            CREATE TRIGGER IF NOT EXISTS delete_${ftsTableName}_before_insert BEFORE UPDATE ON $tableName BEGIN
//              ${deleteRowString()}
//            END;
//        """.trimIndent()
//    )
//    execSQL(
//        """
//            CREATE TRIGGER IF NOT EXISTS insert_${ftsTableName}_after_update AFTER UPDATE ON $tableName BEGIN
//              ${insertRowString()}
//            END;
//        """.trimIndent()
//    )
//    execSQL(
//        """
//            CREATE TRIGGER IF NOT EXISTS delete_${ftsTableName}_before_delete BEFORE DELETE ON $tableName BEGIN
//              ${deleteRowString()}
//            END;
//        """.trimIndent()
//    )
//    execSQL(
//        """
//            CREATE TRIGGER IF NOT EXISTS insert_${ftsTableName}_after_insert AFTER INSERT ON $tableName BEGIN
//              ${insertRowString()}
//            END;
//        """.trimIndent()
//    )
// }
