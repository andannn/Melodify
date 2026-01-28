package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

internal object AlbumColumns {
    const val ID = "album_id"
    const val TITLE = "album_title"
    const val TRACK_COUNT = "album_track_count"
}

@Entity(tableName = "library_album_table")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "album_id")
    val albumId: Long,
    @ColumnInfo(name = "album_title")
    val title: String,
    @ColumnInfo(name = "album_track_count", defaultValue = "0")
    val trackCount: Int = 0,
    @ColumnInfo(name = "album_number_of_songs_for_artist")
    val numberOfSongsForArtist: Int? = null,
    @ColumnInfo(name = "album_cover_uri")
    val coverUri: String? = null,
)

fun AlbumEntity.toAlbumWithoutTrackCount() = AlbumWithoutTrackCount(albumId, title, coverUri)

data class AlbumWithoutTrackCount(
    @ColumnInfo(name = "album_id")
    val albumId: Long,
    @ColumnInfo(name = "album_title")
    val title: String,
    @ColumnInfo(name = "album_cover_uri")
    val coverUri: String? = null,
)
