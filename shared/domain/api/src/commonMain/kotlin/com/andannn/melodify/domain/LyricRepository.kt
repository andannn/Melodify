/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import com.andannn.melodify.domain.model.LyricModel
import kotlinx.coroutines.flow.Flow

interface LyricRepository {
    fun getLyricByMediaIdFlow(
        mediaId: String,
        trackName: String,
        artistName: String,
        albumName: String? = null,
        duration: Long? = null,
    ): Flow<State>

    sealed interface State {
        data object Loading : State

        data class Success(
            val model: LyricModel,
        ) : State

        data class Error(
            val throwable: Throwable,
        ) : State
    }
}
