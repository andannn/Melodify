/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.paging.PagingSource
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.VideoEntity
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
    fun albumMediaPagingProvider(albumId: String): PagingProvider<MediaEntity> =
        getKoin().get<AlbumMediaPagingProvider> { parametersOf(albumId) }

    fun allMediaPagingProvider(): PagingProvider<MediaEntity> = getKoin().get<AllMediaPagingProvider>()

    fun artistMediaPagingProvider(artistId: String): PagingProvider<MediaEntity> =
        getKoin().get<ArtistMediaPagingProvider> { parametersOf(artistId) }

    fun genreMediaPagingProvider(genreId: String): PagingProvider<MediaEntity> =
        getKoin().get<GenreMediaPagingProvider> { parametersOf(genreId) }

    fun allVideoPagingProvider(): PagingProvider<VideoEntity> = getKoin().get<AllVideoPagingProvider>()

    fun bucketVideoPagingProvider(bucketId: String): PagingProvider<VideoEntity> =
        getKoin().get<BucketVideoPagingProvider> { parametersOf(bucketId) }
}
