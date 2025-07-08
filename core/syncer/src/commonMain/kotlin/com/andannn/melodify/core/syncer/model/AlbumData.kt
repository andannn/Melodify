/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.model

data class AlbumData(
    val albumId: Long,
    val title: String,
    val trackCount: Int? = null,
    val numberOfSongsForArtist: Int? = null,
    val coverUri: String? = null,
)
