/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.paging.PagingSource
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.model.AudioVideoMergedResult
import kotlinx.coroutines.flow.Flow
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

interface PagingProvider<E : Any> {
    fun getDataFlow(
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): Flow<List<E>>

    fun getPagingSource(
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): PagingSource<Int, E>
}

object PagingProviderFactory {
    fun albumMediaPagingProvider(albumId: String): PagingProvider<AudioEntity> =
        getKoin().get<MediaPagingProvider> {
            parametersOf(listOf(MediaEntityWhere.albumIdWhere(albumId)))
        }

    fun allMediaPagingProvider(): PagingProvider<AudioEntity> =
        getKoin().get<MediaPagingProvider> {
            parametersOf(emptyList<Where>())
        }

    fun artistMediaPagingProvider(artistId: String): PagingProvider<AudioEntity> =
        getKoin().get<MediaPagingProvider> {
            parametersOf(listOf(MediaEntityWhere.artistIdWhere(artistId)))
        }

    fun genreMediaPagingProvider(genreId: String): PagingProvider<AudioEntity> =
        getKoin().get<MediaPagingProvider> {
            parametersOf(listOf(MediaEntityWhere.genreIdWhere(genreId)))
        }

    fun allVideoPagingProvider(): PagingProvider<VideoEntity> =
        getKoin().get<VideoPagingProvider> {
            parametersOf(emptyList<Where>())
        }

    fun bucketVideoPagingProvider(bucketId: String): PagingProvider<VideoEntity> =
        getKoin().get<VideoPagingProvider> {
            parametersOf(listOf(VideoEntityWhere.bucketIdWhere(bucketId)))
        }

    fun playListPagingProvider(playListId: Long): PagingProvider<AudioVideoMergedResult> =
        getKoin().get<PlayListPagingProvider> { parametersOf(playListId) }
}
