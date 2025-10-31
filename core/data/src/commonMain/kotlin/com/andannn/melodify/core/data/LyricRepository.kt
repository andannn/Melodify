/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

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
