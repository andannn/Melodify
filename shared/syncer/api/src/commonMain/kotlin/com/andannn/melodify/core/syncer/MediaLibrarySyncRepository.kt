package com.andannn.melodify.core.syncer

import kotlinx.coroutines.flow.Flow

interface MediaLibrarySyncRepository {
    enum class ContentType {
        MEDIA,
        ARTIST,
        ALBUM,
        GENRE,
        VIDEO,
    }

    enum class Status {
        START,
        COMPLETED,
        ERROR,
    }

    data class SyncState(
        val status: Status = Status.START,
        val syncInfoMap: Map<ContentType, SyncInfo> = emptyMap(),
    )

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

    fun startSync()

    fun cancelCurrentSync()

    fun lastSyncStatusFlow(): Flow<SyncState>
}
