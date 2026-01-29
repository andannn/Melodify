/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

data class MediaSorts(
    val sorts: List<Sort>,
) {
    companion object Companion {
        fun buildMethod(builderAction: MutableList<Sort>.() -> Unit) =
            MediaSorts(
                buildList(builderAction),
            )
    }

    override fun toString(): String = toSortString()
}

internal fun MediaSorts?.toSortString(): String =
    if (this != null && sorts.isNotEmpty()) {
        "ORDER BY " + sorts.joinToString(separator = ", ")
    } else {
        ""
    }

data class Sort(
    val column: String,
    val order: SortOrder,
) {
    override fun toString(): String = "$column ${order.value}"
}

enum class SortOrder(
    val value: String,
) {
    ASCENDING("ASC"),
    DESCENDING("DESC"),
}

object MediaEntitySort {
    fun buildAlbumSort(ascending: Boolean) = Sort("media_album", ascending.toOrder())

    fun buildArtistSort(ascending: Boolean) = Sort("media_artist", ascending.toOrder())

    fun buildTitleSort(ascending: Boolean) = Sort("media_title", ascending.toOrder())

    fun buildTrackNumSort(ascending: Boolean) = Sort("media_cd_track_number", ascending.toOrder())

    fun buildGenreSort(ascending: Boolean) = Sort("media_genre", ascending.toOrder())

    fun buildReleaseYearSort(ascending: Boolean) = Sort("media_year", ascending.toOrder())
}

object VideoEntitySort {
    fun buildBucketSort(ascending: Boolean) = Sort("video_bucket_display_name", ascending.toOrder())

    fun buildTitleSort(ascending: Boolean) = Sort("video_title", ascending.toOrder())
}

internal fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
