package com.andannn.melodify.core.syncer.model

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
