/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import com.andannn.melodify.core.data.model.AudioItemModel

class MediaFileDeleteHelperImpl : MediaFileDeleteHelper {
    override suspend fun deleteMedias(mediaList: List<AudioItemModel>): MediaFileDeleteHelper.Result = MediaFileDeleteHelper.Result.Success
}
