/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.model

data class VideoData constructor(
    val id: Long,
    val sourceUri: String,
    val title: String?,
    val displayName: String?,
    val mimeType: String?,
    val size: Long,
    val duration: Long,
    val data: String?,
    val relativePath: String?,
    val bucketId: Long?,
    val bucketDisplayName: String?,
    val volumeName: String?,
    val width: Int?,
    val height: Int?,
    val orientation: Int?,
    val ownerPackageName: String?,
    val album: String?,
    val artist: String?,
    val dateAdded: Long?,
    val dateModified: Long?,
)
