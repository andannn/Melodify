/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.syncer.model.MediaDataModel

internal class MPLibraryMediaScanner(
    private val mpMediaScanner: MPMediaScanner,
) : MediaLibraryScanner {
    override suspend fun scanAllMedia(): MediaDataModel = mpMediaScanner.loadAllLocalSongs().mapToMediaData()

    override suspend fun scanMediaByUri(uris: List<String>): MediaDataModel = MediaDataModel.emptyModel()
}
