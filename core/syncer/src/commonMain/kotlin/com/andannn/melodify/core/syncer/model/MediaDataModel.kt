package com.andannn.melodify.core.syncer.model

data class MediaDataModel(
    val audioData: List<AudioData>,
    val albumData: List<AlbumData>,
    val artistData: List<ArtistData>,
    val genreData: List<GenreData>,
)