package com.andannn.melodify.core.database

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

internal fun MediaWheres.toWhereString(): String = "WHERE " + wheres.joinToString(separator = " AND ")

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
