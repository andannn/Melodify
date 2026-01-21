/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.syncer.model.AudioData

interface MPMediaScanner {
    fun loadAllLocalSongs(): List<AudioData>
}

fun persistIdOfUri(uri: String) = uri.substringAfter("ios://mpmedia/cover/").toLongOrNull()

fun String.isCustomMpLibraryUri() = startsWith("ios://mpmedia/cover/")

fun createCustomArtworkUri(persistentID: Long): String = "ios://mpmedia/cover/$persistentID"
