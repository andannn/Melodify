package com.andannn.melodify

import com.andannn.melodify.domain.MediaFileDeleteHelper
import com.andannn.melodify.domain.model.MediaItemModel

class MediaFileDeleteHelperImpl : MediaFileDeleteHelper {
    override suspend fun deleteMedias(mediaList: List<MediaItemModel>): MediaFileDeleteHelper.Result = MediaFileDeleteHelper.Result.Success
}
