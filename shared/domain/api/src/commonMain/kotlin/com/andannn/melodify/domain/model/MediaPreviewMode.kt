/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain.model

enum class MediaPreviewMode {
    GRID_PREVIEW,
    LIST_PREVIEW,
    ;

    fun next(): MediaPreviewMode =
        when (this) {
            GRID_PREVIEW -> LIST_PREVIEW
            LIST_PREVIEW -> GRID_PREVIEW
        }
}
