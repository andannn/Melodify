/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

data class SortOptionData(
    val type: Int,
    val isAscending: Boolean,
) {
    companion object {
        // Audio
        const val SORT_TYPE_AUDIO_ALBUM = 1
        const val SORT_TYPE_AUDIO_ARTIST = 2
        const val SORT_TYPE_AUDIO_GENRE = 3
        const val SORT_TYPE_AUDIO_TITLE = 4
        const val SORT_TYPE_AUDIO_YEAR = 5
        const val SORT_TYPE_AUDIO_TRACK_NUM = 6

        // Video
        const val SORT_TYPE_VIDEO_BUCKET_NAME = 7
        const val SORT_TYPE_VIDEO_TITLE_NAME = 8

        // PlayList
        const val SORT_TYPE_PLAYLIST_CREATE_DATE = 9
    }
}
