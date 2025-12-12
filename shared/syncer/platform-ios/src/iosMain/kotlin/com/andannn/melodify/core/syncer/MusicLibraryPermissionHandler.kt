/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

interface MusicLibraryPermissionHandler {
    fun requestMusicLibraryAuthorization(completion: (Boolean) -> Unit)

    fun mediaPermissionGranted(): Boolean
}
