package com.andannn.melodify.core.syncer.model

/**
 * Refresh type.
 */
sealed interface RefreshType {
    data object All : RefreshType

    data class ByUri(val triggerFiles: List<FileChangeEvent>) : RefreshType
}

/**
 * File change type.
 */
enum class FileChangeType {
    CREATE,
    DELETE,
    MODIFY
}

/**
 * File change event.
 *
 * @param fileUri the file uri.
 * @param fileChangeType the file change type.
 */
data class FileChangeEvent(
    val fileUri: String,
    val fileChangeType: FileChangeType
)
