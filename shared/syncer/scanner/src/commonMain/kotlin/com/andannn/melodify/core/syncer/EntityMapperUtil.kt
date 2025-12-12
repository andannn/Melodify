/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.syncer.model.AlbumData
import com.andannn.melodify.core.syncer.model.ArtistData
import com.andannn.melodify.core.syncer.model.AudioData
import com.andannn.melodify.core.syncer.model.GenreData
import com.andannn.melodify.core.syncer.model.MediaDataModel
import kotlin.collections.component1
import kotlin.collections.component2

internal fun List<AudioData>.mapToMediaData(): MediaDataModel {
    val audioData = this
    val albumMap = audioData.groupBy { it.album }

    val albumDataList =
        albumMap.map { (album, audioDataList) ->
            val title = album ?: ""
            val id = audioDataList.firstOrNull()?.albumId ?: title.hashCode().toLong()
            AlbumData(
                albumId = id,
                title = title,
                trackCount = audioDataList.size,
                coverUri = audioDataList.firstOrNull()?.cover,
            )
        }

    val artistMap = audioData.groupBy { it.artist }

    val artistDataList =
        artistMap.map { (artist, audioDataList) ->
            val title = artist ?: "Unknown Artist"
            val id = audioDataList.firstOrNull()?.artistId ?: title.hashCode().toLong()
            ArtistData(
                artistId = id,
                name = title,
                trackCount = audioDataList.size,
                artistCoverUri = audioDataList.firstOrNull()?.cover,
            )
        }

    val genreMap = audioData.groupBy { it.genre }

    val genreDataList =
        genreMap.map { (genre, _) ->
            val title = genre ?: "Unknown Genre"

            GenreData(
                genreId = title.hashCode().toLong(),
                name = title,
            )
        }

    val audioDataListWitId =
        audioData.map { audio ->
            audio.copy(
                albumId = albumDataList.firstOrNull { it.title == audio.album }?.albumId ?: -1,
                artistId = artistDataList.firstOrNull { it.name == audio.artist }?.artistId ?: -1,
                genreId = genreDataList.firstOrNull { it.name == audio.genre }?.genreId ?: -1,
            )
        }

    return MediaDataModel(
        audioData = audioDataListWitId,
        albumData = albumDataList,
        artistData = artistDataList,
        genreData = genreDataList,
    )
}
