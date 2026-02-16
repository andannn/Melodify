/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.helper.paging

import androidx.room.RoomRawQuery
import com.andannn.melodify.core.database.helper.paging.MediaWheres.Companion.buildMethod

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

internal fun MediaWheres?.appendOrCreateWith(builder: () -> List<Where>) =
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

internal fun buildMediaRawQuery(
    wheres: MediaWheres?,
    sort: MediaSorts?,
): RoomRawQuery {
    val sql =
        "SELECT * FROM library_media_table ${wheres.toWhereString()} ${sort.toSortString()}"
    return RoomRawQuery(sql)
}

internal fun buildVideoRawQuery(
    wheres: MediaWheres?,
    sort: MediaSorts?,
): RoomRawQuery {
    val sql =
        "SELECT * FROM library_video_table ${wheres.toWhereString()} ${sort.toSortString()}"
    return RoomRawQuery(sql)
}

object MediaEntityWhere {
    fun albumIdWhere(albumId: Long) =
        Where(
            "media_album_id",
            "=",
            albumId.toString(),
        )

    fun artistIdWhere(artist: Long) =
        Where(
            "media_artist_id",
            "=",
            artist.toString(),
        )

    fun genreIdWhere(genreId: Long) =
        Where(
            "media_genre_id",
            "=",
            genreId.toString(),
        )

    fun releaseYearWhere(year: String) =
        Where(
            "media_year",
            Where.Operator.EQUALS,
            year,
        )

    fun titleWhere(firstCharacterString: String) =
        Where(
            "media_title",
            Where.Operator.GLOB,
            "$firstCharacterString*",
        )

    internal fun audioNotDeletedWhere() =
        Where(
            "deleted",
            "IS NOT",
            "1",
        )
}

object VideoEntityWhere {
    internal fun videoNotDeletedWhere() =
        Where(
            "video_deleted",
            "IS NOT",
            "1",
        )

    fun titleWhere(firstCharacterString: String) =
        Where(
            "video_title",
            Where.Operator.GLOB,
            "$firstCharacterString*",
        )

    fun bucketIdWhere(bucketId: Long) =
        Where(
            "video_bucket_id",
            "=",
            bucketId.toString(),
        )
}
