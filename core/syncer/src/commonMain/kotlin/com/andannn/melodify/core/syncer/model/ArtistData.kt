package com.andannn.melodify.core.syncer.model

data class ArtistData(
    val artistId: Long,
    val name: String,
    val artistCoverUri: String? = null,
    val trackCount: Int = 0,
)