package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.database.dao.MediaSortType
import com.andannn.melodify.core.database.dao.Sort
import com.andannn.melodify.core.database.dao.SortMethod
import com.andannn.melodify.core.database.dao.SortOrder

sealed interface GroupSort {
    sealed class Album(
        open val albumAscending: Boolean,
    ) : GroupSort {
        data class TrackNumber(
            val trackNumAscending: Boolean,
            override val albumAscending: Boolean,
        ) : Album(albumAscending)
    }

    data class Title(
        val titleAscending: Boolean,
    ) : GroupSort
}

internal fun GroupSort.toSortMethod(): SortMethod =
    when (this) {
        is GroupSort.Album ->
            when (this) {
                is GroupSort.Album.TrackNumber -> buildSortMethod()
            }

        is GroupSort.Title -> buildSortMethod()
    }

private fun GroupSort.Album.TrackNumber.buildSortMethod() =
    SortMethod.buildMethod {
        add(Sort(MediaSortType.Album, albumAscending.toOrder()))
        add(Sort(MediaSortType.TrackNum, trackNumAscending.toOrder()))
    }

private fun GroupSort.Title.buildSortMethod() =
    SortMethod.buildMethod {
        add(Sort(MediaSortType.Title, titleAscending.toOrder()))
    }

private fun Boolean.toOrder() = if (this) SortOrder.ASCENDING else SortOrder.DESCENDING
