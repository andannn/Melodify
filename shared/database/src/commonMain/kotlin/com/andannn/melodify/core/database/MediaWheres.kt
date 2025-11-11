/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import com.andannn.melodify.core.database.MediaWheres.Companion.buildMethod
import kotlin.apply

data class MediaWheres(
    val wheres: List<Where>,
) {
    companion object {
        fun buildMethod(builderAction: MutableList<Where>.() -> Unit) =
            MediaWheres(
                buildList(builderAction),
            )
    }
}

fun MediaWheres?.appendOrCreateWith(builder: () -> List<Where>) =
    this.let { old ->
        buildMethod {
            if (old != null) {
                addAll(old.wheres)
            }
            addAll(builder())
        }
    }

internal fun MediaWheres?.toWhereString(): String =
    if (this != null && wheres.isNotEmpty()) {
        "WHERE " + wheres.joinToString(separator = " AND ")
    } else {
        ""
    }

data class Where(
    val column: String,
    val operator: String,
    val value: String,
) {
    override fun toString(): String = "$column $operator '$value'"

    object Operator {
        const val EQUALS = "="
        const val GLOB = "GLOB"
    }
}
