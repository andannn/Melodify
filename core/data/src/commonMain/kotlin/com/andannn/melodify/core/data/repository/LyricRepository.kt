package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.LyricModel
import kotlinx.coroutines.flow.Flow

interface LyricRepository {
    suspend fun tryGetLyricOrIgnore(
        mediaId: String,
        trackName: String,
        artistName: String,
        albumName: String? = null,
        duration: Long? = null,
    )

    fun getLyricByMediaIdFlow(mediaStoreId: String): Flow<LyricModel?>
}