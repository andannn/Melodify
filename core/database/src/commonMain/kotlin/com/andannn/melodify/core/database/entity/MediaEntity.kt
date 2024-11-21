package com.andannn.melodify.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.andannn.melodify.core.database.Tables

internal object MediaColumns {
    const val ID = "media_id"
    const val TITLE = "media_title"
    const val DURATION = "media_duration"
    const val MODIFIED_DATE = "media_modified_date"
    const val SIZE = "media_size"
    const val MIME_TYPE = "media_mime_type"
    const val ALBUM = "media_album"
    const val ALBUM_ID = "media_album_id"
    const val ARTIST = "media_artist"
    const val ARTIST_ID = "media_artist_id"
    const val CD_TRACK_NUMBER = "media_cd_track_number"
    const val DISC_NUMBER = "media_disc_number"
    const val NUM_TRACKS = "media_num_tracks"
    const val BITRATE = "media_bitrate"
    const val GENRE = "media_genre"
    const val GENRE_ID = "media_genre_id"
    const val YEAR = "media_year"
    const val TRACK = "media_track"
    const val COMPOSER = "media_composer"
}

@Entity(
    tableName = Tables.LIBRARY_MEDIA,
)
data class MediaEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = MediaColumns.ID)
    val id: Long,

    @ColumnInfo(name = MediaColumns.TITLE)
    val title: String,

    @ColumnInfo(name = MediaColumns.DURATION)
    val duration: Int? = null,

    @ColumnInfo(name = MediaColumns.MODIFIED_DATE)
    val modifiedDate: Long? = null,

    @ColumnInfo(name = MediaColumns.SIZE)
    val size: Int? = null,

    @ColumnInfo(name = MediaColumns.MIME_TYPE)
    val mimeType: String? = null,

    @ColumnInfo(name = MediaColumns.ALBUM)
    val album: String? = null,

    @ColumnInfo(name = MediaColumns.ALBUM_ID)
    val albumId: Long? = null,

    @ColumnInfo(name = MediaColumns.ARTIST)
    val artist: String? = null,

    @ColumnInfo(name = MediaColumns.ARTIST_ID)
    val artistId: Long? = null,

    @ColumnInfo(name = MediaColumns.CD_TRACK_NUMBER)
    val cdTrackNumber: Int? = null,

    @ColumnInfo(name = MediaColumns.DISC_NUMBER)
    val discNumber: Int? = null,

    @ColumnInfo(name = MediaColumns.NUM_TRACKS)
    val numTracks: Int? = null,

    @ColumnInfo(name = MediaColumns.BITRATE)
    val bitrate: Int? = null,

    @ColumnInfo(name = MediaColumns.GENRE)
    val genre: String? = null,

    @ColumnInfo(name = MediaColumns.GENRE_ID)
    val genreId: Long? = null,

    @ColumnInfo(name = MediaColumns.YEAR)
    val year: String? = null,

    @ColumnInfo(name = MediaColumns.TRACK)
    val track: String? = null,

    @ColumnInfo(name = MediaColumns.COMPOSER)
    val composer: String? = null,
)