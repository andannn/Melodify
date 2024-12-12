package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.syncer.model.MediaDataModel

interface MediaLibraryScanner {
    suspend fun scanAllMedia()
    suspend fun scanMediaByUri(uris: List<String>): MediaDataModel
}