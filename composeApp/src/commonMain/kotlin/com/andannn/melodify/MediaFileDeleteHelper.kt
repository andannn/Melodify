/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.andannn.melodify.core.data.model.AudioItemModel

val LocalMediaFileDeleteHelper: ProvidableCompositionLocal<MediaFileDeleteHelper> =
    compositionLocalOf { error("MediaFileDeleteHelper") }

interface MediaFileDeleteHelper {
    suspend fun deleteMedias(mediaList: List<AudioItemModel>)
}
