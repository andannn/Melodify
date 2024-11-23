package com.andannn.melodify.core.library.mediastore.model

data class MediaDataModel(
    val audioData: List<AudioData>,
    val albumData: List<AlbumData>,
    val artistData: List<ArtistData>,
    val genreData: List<GenreData>,
)