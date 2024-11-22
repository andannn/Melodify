package com.andannn.melodify.core.library.mediastore.model

data class AudioData(
    val id: Long,
    val title: String = "",
    val duration: Int = -1,
    val modifiedDate: Long = -1,
    val size: Int = -1,
    val mimeType: String = "",
    val album: String? = null,
    val albumId: Long? = null,
    val artist: String? = null,
    val artistId: Long? = null,
    val cdTrackNumber: Int? = null,
    val discNumber: Int? = null,
    val numTracks: Int? = null,
    val bitrate: Int? = null,
    val genre: String? = null,
    val genreId: Long? = null,
    val year: String? = null,
    val track: String? = null,
    val composer: String? = null,
    val cover: String? = null
)