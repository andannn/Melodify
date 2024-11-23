package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object AlbumColumns {
    const val ID = "album_id"
    const val TITLE = "album_title"
    const val TRACK_COUNT = "album_track_count"
    const val NUMBER_OF_SONGS_FOR_ARTIST = "album_number_of_songs_for_artist"
    const val COVER_URI = "album_cover_uri"
}

@Entity(tableName = Tables.LIBRARY_ALBUM)
class AlbumEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = AlbumColumns.ID)
    val albumId: Long,

    @ColumnInfo(name = AlbumColumns.TITLE)
    val title: String,

    @ColumnInfo(name = AlbumColumns.TRACK_COUNT)
    val trackCount: Int? = null,

    @ColumnInfo(name = AlbumColumns.NUMBER_OF_SONGS_FOR_ARTIST)
    val numberOfSongsForArtist: Int? = null,

    @ColumnInfo(name = AlbumColumns.COVER_URI)
    val coverUri: String? = null,
)