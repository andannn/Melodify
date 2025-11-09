/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import com.andannn.melodify.core.data.model.MediaItemModel

class MediaFileDeleteHelperImpl : MediaFileDeleteHelper {
    override suspend fun deleteMedias(mediaList: List<MediaItemModel>): MediaFileDeleteHelper.Result = MediaFileDeleteHelper.Result.Success
}
