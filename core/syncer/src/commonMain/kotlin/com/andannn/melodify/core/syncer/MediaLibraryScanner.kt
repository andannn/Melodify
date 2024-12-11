package com.andannn.melodify.core.syncer

interface MediaLibraryScanner {
    suspend fun scanAllMedia()
    suspend fun scanMediaByUri(uris: List<String>)
}