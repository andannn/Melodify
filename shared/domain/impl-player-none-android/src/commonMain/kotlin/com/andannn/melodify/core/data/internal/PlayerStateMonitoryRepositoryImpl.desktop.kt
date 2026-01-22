/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.player.AvPlayerQueuePlayer
import com.andannn.melodify.domain.PlayerStateMonitoryRepository
import com.andannn.melodify.domain.impl.toAppItem
import com.andannn.melodify.domain.impl.toPlayerState
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayMode
import com.andannn.melodify.domain.model.PlayerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

internal class PlayerStateMonitoryRepositoryImpl(
    private val queuePlayer: AvPlayerQueuePlayer,
    private val libraryDao: MediaLibraryDao,
) : PlayerStateMonitoryRepository {
    override fun getCurrentPositionMs(): Long = queuePlayer.currentPositionMs

    override fun observeCurrentDurationMs() =
        queuePlayer
            .observeProgressFactor()
            .map { queuePlayer.currentDurationMs }
            .distinctUntilChanged()

    override fun getPlayingIndexInQueue(): Int = queuePlayer.playingIndexInQueue

    override suspend fun getPlayListQueue(): List<MediaItemModel> = getPlayListQueueStateFlow().first()

    override fun getPlayingMediaStateFlow() =
        queuePlayer
            .observePlayingMedia()
            .map { url ->
                if (url == null) {
                    return@map null
                }
                libraryDao
                    .getMediaByMediaSourceUrl(listOf(url))
                    .firstOrNull()
                    ?.toAppItem()
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPlayListQueueStateFlow(): Flow<List<MediaItemModel>> =
        queuePlayer
            .observePlayListQueue()
            .mapLatest { urls ->
                val idToEntityMap =
                    libraryDao.getMediaByMediaSourceUrl(urls).associateBy { it.sourceUri.toString() }
                urls.mapNotNull { idToEntityMap[it]?.toAppItem() }
            }

    override fun getCurrentPlayMode(): PlayMode = PlayMode.REPEAT_ALL

    override fun observeIsShuffle(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun observePlayMode() = flowOf(PlayMode.REPEAT_ALL)

    override fun observePlayerState(): Flow<PlayerState> =
        queuePlayer
            .observePlayerState()
            .map {
                it.toPlayerState()
            }.distinctUntilChanged()

    override fun observeProgressFactor(): Flow<Float> = queuePlayer.observeProgressFactor()

    override fun observePlayBackEndEvent(): Flow<MediaItemModel> {
        // TODO: support play video
        return flowOf()
    }
}
