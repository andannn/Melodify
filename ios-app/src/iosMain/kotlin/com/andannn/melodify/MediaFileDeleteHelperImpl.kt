package com.andannn.melodify

import com.andannn.melodify.core.data.MediaFileDeleteHelper
import com.andannn.melodify.core.data.model.MediaItemModel

class MediaFileDeleteHelperImpl : MediaFileDeleteHelper {
    override suspend fun deleteMedias(mediaList: List<MediaItemModel>): MediaFileDeleteHelper.Result = MediaFileDeleteHelper.Result.Success
}
