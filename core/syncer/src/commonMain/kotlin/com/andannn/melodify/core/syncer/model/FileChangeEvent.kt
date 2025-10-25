/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.model

/**
 * Refresh type.
 */
sealed interface RefreshType {
    data object All : RefreshType

    data class ByUri(
        val triggerFiles: List<FileChangeEvent>,
    ) : RefreshType
}

/**
 * File change type.
 */
enum class FileChangeType {
    DELETE,
    MODIFY,
}

/**
 * File change event.
 *
 * @param fileUri the file uri.
 * @param fileChangeType the file change type.
 */
data class FileChangeEvent(
    val fileUri: String,
    val fileChangeType: FileChangeType,
)
