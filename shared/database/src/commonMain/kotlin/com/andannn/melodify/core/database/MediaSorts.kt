/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

data class MediaSorts(
    val sorts: List<Sort>,
) {
    companion object Companion {
        fun buildMethod(builderAction: MutableList<Sort>.() -> Unit) =
            MediaSorts(
                buildList(builderAction),
            )
    }
}

internal fun MediaSorts.toSortString(): String =
    if (sorts.isNotEmpty()) {
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
