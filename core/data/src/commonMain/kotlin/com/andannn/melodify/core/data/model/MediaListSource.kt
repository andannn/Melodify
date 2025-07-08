/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

enum class MediaListSource {
    ALBUM,
    GENRE,
    PLAY_LIST,
    ARTIST,
    ;

    fun toNavArg() = this.name

    companion object {
        fun fromString(string: String) =
            entries.firstOrNull {
                it.name == string
            }
    }
}
