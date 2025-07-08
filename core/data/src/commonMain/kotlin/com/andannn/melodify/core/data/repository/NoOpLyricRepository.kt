package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.LyricModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class NoOpLyricRepository : LyricRepository {
    override suspend fun tryGetLyricOrIgnore(
        mediaId: String,
        trackName: String,
        artistName: String,
        albumName: String?,
        duration: Long?,
    ) {}

    override fun getLyricByMediaIdFlow(mediaStoreId: String): Flow<LyricModel?> = flowOf()
}
