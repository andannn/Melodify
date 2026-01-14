/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import kotlinx.coroutines.flow.StateFlow

data class SyncState(
    val syncStatus: SyncStatus = SyncStatus.START,
    val syncInfoMap: Map<ContentType, SyncInfo> = emptyMap(),
)

enum class ContentType {
    MEDIA,
    ARTIST,
    ALBUM,
    GENRE,
    VIDEO,
}

enum class SyncStatus {
    START,
    COMPLETED,
    ERROR,
}

data class SyncInfo(
    val progress: Progress? = null,
    val insertDeleteInfo: List<Info> = emptyList(),
) {
    data class Progress(
        val progress: Int,
        val total: Int,
    )

    data class Info(
        val isInsert: Boolean,
        val item: String,
    )

    fun addNewInfo(info: Info) =
        copy(
            insertDeleteInfo = insertDeleteInfo + info,
        )
}

interface MediaLibrarySyncRepository {
    fun startSync()

    fun cancelCurrentSync()

    fun lastSyncStatusFlow(): StateFlow<SyncState?>
}
