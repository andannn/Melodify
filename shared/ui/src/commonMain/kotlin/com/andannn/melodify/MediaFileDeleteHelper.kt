/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify

import com.andannn.melodify.core.data.model.MediaItemModel

interface MediaFileDeleteHelper {
    /**
     * Delete all [mediaList].
     *
     * @return true if all files are deleted successfully, false otherwise.
     */
    suspend fun deleteMedias(mediaList: List<MediaItemModel>): Result

    enum class Result {
        Success,
        Failed,
        Denied,
    }
}
