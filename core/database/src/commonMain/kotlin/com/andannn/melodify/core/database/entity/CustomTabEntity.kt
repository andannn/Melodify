package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object CustomTabColumns {
    const val ID = "custom_tab_id"
    const val NAME = "custom_tab_name"
    const val TYPE = "custom_tab_type"
    const val EXTERNAL_ID = "custom_tab_external_id"
    const val CREATED_DATE = "custom_tab_created_date"
}

object CustomTabType {
    const val ALL_MUSIC = "all_music"
    const val ALL_ALBUM = "all_album"
    const val ALL_PLAYLIST = "all_playlist"
    const val ALL_GENRE = "all_genre"
    const val ALL_ARTIST = "all_artist"
    const val ALBUM_DETAIL = "album_detail"
    const val ARTIST_DETAIL = "artist_detail"
    const val GENRE_DETAIL = "genre_detail"
    const val PLAYLIST_DETAIL = "playlist_detail"
}

@Entity(tableName = Tables.CUSTOM_TAB)
data class CustomTabEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CustomTabColumns.ID)
    val id: Long = 0,

    /**
     *  values of [CustomTabType]
     */
    @ColumnInfo(name = CustomTabColumns.TYPE)
    val type: String,

    @ColumnInfo(name = CustomTabColumns.NAME)
    val name: String? = null,

    @ColumnInfo(name = CustomTabColumns.EXTERNAL_ID)
    val externalId: String? = null,
)