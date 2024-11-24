package com.andannn.melodify.core.syncer.model

data class ArtistData(
    val artistId: Long,
    val artistCoverUri: String,
    val name: String,
    val trackCount: Int = 0,
)