package com.andannn.melodify

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.andannn.melodify.core.data.model.MediaItemModel

val LocalMediaFileDeleteHelper: ProvidableCompositionLocal<MediaFileDeleteHelper> =
    compositionLocalOf { error("MediaFileDeleteHelper") }

interface MediaFileDeleteHelper {
    suspend fun deleteMedias(mediaList: List<MediaItemModel>)
}
