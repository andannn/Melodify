package com.andannn.melodify.core.library.mediastore.model

data class AlbumData(
    val albumId: Long,
    val title: String,
    val trackCount: Int? = null,
    val numberOfSongsForArtist: Int? = null,
)