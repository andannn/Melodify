/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.database.MediaWheres
import com.andannn.melodify.core.database.Sort
import com.andannn.melodify.core.database.Where
import com.andannn.melodify.core.database.entity.MediaColumns

/**
 * group key for media items
 */
sealed interface GroupKey {
    data class Artist(
        val artistId: String,
    ) : GroupKey

    data class Album(
        val albumId: String,
    ) : GroupKey

    data class Genre(
        val genreId: String,
    ) : GroupKey

    data class Year(
        val year: String,
    ) : GroupKey

    data class Title(
        val firstCharacterString: String,
    ) : GroupKey
}

fun AudioItemModel.keyOf(sortOption: SortOption): GroupKey? =
    when (sortOption) {
        is SortOption.Album -> GroupKey.Album(albumId)
        is SortOption.Artist -> GroupKey.Artist(artistId)
        is SortOption.Genre -> GroupKey.Genre(genreId)
        is SortOption.ReleaseYear -> GroupKey.Year(releaseYear)
        is SortOption.Title -> GroupKey.Title(name[0].toString())
        SortOption.NONE -> null
        is SortOption.TrackNum -> error("Not support")
    }

internal fun List<GroupKey>.toWheresMethod() =
    MediaWheres
        .buildMethod {
            this@toWheresMethod.forEach {
                addWhereOption(it)
            }
        }.takeIf { this.isNotEmpty() }

internal fun MutableList<Where>.addWhereOption(where: GroupKey) =
    apply {
        val where =
            when (where) {
                is GroupKey.Album ->
                    Where(
                        MediaColumns.ALBUM_ID,
                        Where.Operator.EQUALS,
                        where.albumId,
                    )
                is GroupKey.Artist ->
                    Where(
                        MediaColumns.ARTIST_ID,
                        Where.Operator.EQUALS,
                        where.artistId,
                    )

                is GroupKey.Genre ->
                    Where(
                        MediaColumns.GENRE_ID,
                        Where.Operator.EQUALS,
                        where.genreId,
                    )
                is GroupKey.Title ->
                    Where(
                        MediaColumns.TITLE,
                        Where.Operator.GLOB,
                        where.firstCharacterString + "*",
                    )
                is GroupKey.Year ->
                    Where(
                        MediaColumns.YEAR,
                        Where.Operator.EQUALS,
                        where.year,
                    )
            }
        add(where)
    }
