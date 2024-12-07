package com.andannn.melodify.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.dao.PlayListDao.Companion.FAVORITE_PLAY_LIST_ID
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.CustomTabColumns
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.LyricWithAudioCrossRef
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef

internal object Tables {
    const val LYRIC = "lyric_table"
    const val LYRIC_WITH_AUDIO_CROSS_REF = "lyric_with_audio_cross_ref_table"
    const val PLAY_LIST = "play_list_table"
    const val PLAY_LIST_WITH_MEDIA_CROSS_REF = "play_list_with_media_cross_ref_table"
    const val LIBRARY_MEDIA = "library_media_table"
    const val LIBRARY_ALBUM = "library_album_table"
    const val LIBRARY_ARTIST = "library_artist_table"
    const val LIBRARY_GENRE = "library_genre_table"
    const val CUSTOM_TAB = "custom_tab_table"
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
        CustomTabEntity::class
    ],
    autoMigrations = [
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5, AutoMigration4To5Spec::class),
    ],
    version = 5,
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

internal val addFavoritePlayListCallback = object : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        // Insert a default play list.
        connection.execSQL(
            """
            INSERT OR IGNORE INTO play_list_table (play_list_id, play_list_created_date, play_list_name, play_list_artwork_uri)
            VALUES ($FAVORITE_PLAY_LIST_ID, 0, 'My Favorite Songs', '');
        """.trimIndent()
        )
    }
}

internal val addInitialCustomTabsCallback = object : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            INSERT INTO custom_tab_table (custom_tab_type) VALUES ('all_music')
         """.trimIndent()
        )
    }
}

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS play_list_table (
                play_list_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                play_list_created_date INTEGER NOT NULL,
                play_list_name TEXT NOT NULL,
                play_list_artwork_uri TEXT
            );
        """.trimIndent()
        )

        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS play_list_with_media_cross_ref_table (
                play_list_with_media_cross_ref_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                play_list_with_media_cross_ref_play_list_id INTEGER NOT NULL,
                play_list_with_media_cross_ref_media_store_id TEXT NOT NULL,
                play_list_with_media_cross_ref_added_date INTEGER NOT NULL
            );
        """.trimIndent()
        )

        connection.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS index_play_list_with_media_cross_ref_table_play_list_with_media_cross_ref_play_list_id_play_list_with_media_cross_ref_media_store_id ON play_list_with_media_cross_ref_table (play_list_with_media_cross_ref_play_list_id, play_list_with_media_cross_ref_media_store_id);
        """.trimIndent()
        )
    }
}

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE play_list_with_media_cross_ref_table ADD COLUMN play_list_with_media_cross_ref_song_artist TEXT NOT NULL DEFAULT '';          
        """.trimIndent()
        )

        connection.execSQL(
            """
            ALTER TABLE play_list_with_media_cross_ref_table ADD COLUMN play_list_with_media_cross_ref_song_title TEXT NOT NULL DEFAULT '';          
        """.trimIndent()
        )
    }
}

class AutoMigration4To5Spec : AutoMigrationSpec {
    override fun onPostMigrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            INSERT INTO custom_tab_table (custom_tab_type) VALUES ('all_music')
         """.trimIndent()
        )
    }
}