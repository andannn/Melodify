package com.andannn.melodify.core.syncer

interface MediaLibraryScanner {
    suspend fun scanMediaDataAndSyncDatabase()
}