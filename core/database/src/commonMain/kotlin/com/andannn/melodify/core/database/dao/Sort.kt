package com.andannn.melodify.core.database.dao

import com.andannn.melodify.core.database.entity.MediaColumns

data class SortMethod(
    val sorts: List<Sort>,
) {
    companion object {
        fun buildMethod(builderAction: MutableList<Sort>.() -> Unit) =
            SortMethod(
                buildList(builderAction),
            )
    }
}

internal fun SortMethod.toSortString(): String = "ORDER BY " + sorts.joinToString(separator = ", ")

data class Sort(
    val type: MediaSortType,
    val order: SortOrder,
) {
    override fun toString(): String = "${type.value} ${order.value}"
}

enum class SortOrder(
    val value: String,
) {
    ASCENDING("ASC"),
    DESCENDING("DESC"),
}

sealed class MediaSortType(
    val value: String,
) {
    object Title : MediaSortType(MediaColumns.TITLE)

    object Artist : MediaSortType(MediaColumns.ARTIST)

    object Album : MediaSortType(MediaColumns.ALBUM)

    object TrackNum : MediaSortType(MediaColumns.CD_TRACK_NUMBER)
}
