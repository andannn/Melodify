package com.andannn.melodify.core.database.di

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.andannn.melodify.core.database.LyricDao
import com.andannn.melodify.core.database.MelodifyDataBase
import com.andannn.melodify.core.database.PlayListDao
import com.andannn.melodify.core.database.PlayListDao.Companion.FAVORITE_PLAY_LIST_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val databaseBuilder: Module

val databaseModule = module {
    includes(
        databaseBuilder,
        module {
            single<LyricDao> { get<MelodifyDataBase>().getLyricDao() }
            single<PlayListDao> { get<MelodifyDataBase>().getPlayListDao() }
        },
        module {
            single<MelodifyDataBase> {
                get<RoomDatabase.Builder<MelodifyDataBase>>()
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .addMigrations(
                        MIGRATION_1_2
                    )
                    .addCallback(addFavoritePlayListCallback)
                    .build()
            }
        },
    )
}

private val addFavoritePlayListCallback = object : RoomDatabase.Callback() {
    override fun onOpen(connection: SQLiteConnection) {
        // Insert a default play list.
        connection.execSQL("""
            INSERT OR IGNORE INTO play_list_table (play_list_id, play_list_created_date, play_list_name, play_list_artwork_uri)
            VALUES (${FAVORITE_PLAY_LIST_ID}, 0, 'My Favorite Songs', '');
        """.trimIndent())
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS play_list_table (
                play_list_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                play_list_created_date INTEGER NOT NULL,
                play_list_name TEXT NOT NULL,
                play_list_artwork_uri TEXT
            );
        """.trimIndent())

        connection.execSQL("""
            CREATE TABLE IF NOT EXISTS play_list_with_media_cross_ref_table (
                play_list_with_media_cross_ref_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                play_list_with_media_cross_ref_play_list_id INTEGER NOT NULL,
                play_list_with_media_cross_ref_media_store_id TEXT NOT NULL,
                play_list_with_media_cross_ref_added_date INTEGER NOT NULL
            );
        """.trimIndent())

        connection.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_play_list_with_media_cross_ref_table_play_list_with_media_cross_ref_play_list_id_play_list_with_media_cross_ref_media_store_id ON play_list_with_media_cross_ref_table (play_list_with_media_cross_ref_play_list_id, play_list_with_media_cross_ref_media_store_id);
        """.trimIndent())
    }
}
