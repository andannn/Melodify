/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.network.LrclibService
import com.andannn.melodify.domain.LyricRepository
import com.andannn.melodify.domain.impl.toLyricEntity
import com.andannn.melodify.domain.impl.toLyricModel
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

private const val TAG = "LyricRepository"

internal class LyricRepositoryImpl(
    private val lyricDao: LyricDao,
    private val lyricLocalDataSource: LrclibService,
) : LyricRepository {
    override fun getLyricByMediaIdFlow(
        mediaId: String,
        trackName: String,
        artistName: String,
        albumName: String?,
        duration: Long?,
    ): Flow<LyricRepository.State> =
        flow {
            val lyric = lyricDao.getLyricByMediaIdFlow(mediaId).first()
            if (lyric != null) {
                emit(LyricRepository.State.Success(lyric.toLyricModel()))
                return@flow
            }

            try {
                emit(LyricRepository.State.Loading)
                val lyricData =
                    lyricLocalDataSource.getLyric(
                        trackName = trackName,
                        artistName = artistName,
                        albumName = albumName,
                        duration = duration,
                    )
                lyricDao.insertLyricOfMedia(
                    mediaStoreId = mediaId,
                    lyric = lyricData.toLyricEntity(),
                )
                val result = lyricDao.getLyricByMediaIdFlow(mediaId).first()?.toLyricModel()
                emit(LyricRepository.State.Success(result ?: error("Lyric not found")))
            } catch (e: ResponseException) {
                Napier.d(tag = TAG) { "Error getting lyric: ${e.message}" }
                emit(LyricRepository.State.Error(e))
            }
        }
}
