/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.scanner.impl.siren

import com.andannn.melodify.core.network.ServerException
import com.andannn.melodify.core.network.service.siren.MonsterSirenService
import com.andannn.melodify.core.network.service.siren.model.Album
import com.andannn.melodify.core.network.service.siren.model.Song
import com.andannn.melodify.core.syncer.MediaLibraryScanner
import com.andannn.melodify.core.syncer.mapToMediaData
import com.andannn.melodify.core.syncer.model.AudioData
import com.andannn.melodify.core.syncer.model.MediaDataModel
import io.github.aakira.napier.Napier

private const val TAG = "MonsterSirenScanner"

internal class MonsterSirenScanner(
    private val service: MonsterSirenService,
) : MediaLibraryScanner {
    override suspend fun scanAllMedia(): MediaDataModel =
        try {
            val albums: List<Album> = service.getAlbums().getOrThrow()

            val allAudios =
                albums.fold(mutableListOf<AudioData>()) { acc, album ->
                    val albumDetail = service.getAlbumDetail(album.cid).getOrThrow()

                    acc.apply {
                        addAll(
                            albumDetail.songs.mapIndexed { index, song ->
                                song.toAudioData(album, index)
                            },
                        )
                    }
                }
            allAudios.mapToMediaData()
        } catch (serviceException: ServerException) {
            Napier.d(tag = TAG) { "ServerException: ${serviceException.message}" }
            throw serviceException
        }

    override suspend fun scanMediaByUri(uris: List<String>): MediaDataModel {
        TODO("Not yet implemented")
    }
}

private fun Song.toAudioData(
    album: Album,
    trackIndex: Int,
): AudioData =
    AudioData(
        id = cid.toLong(),
        sourceUri = "https://monster-siren.hypergryph.com/api/song/$cid",
        title = name,
        album = album.name,
        albumId = album.cid.toLong(),
        artistId = artistes.firstOrNull()?.hashCode()?.toLong(),
        artist = artistes.firstOrNull(),
        cdTrackNumber = trackIndex + 1,
        genre = "V.A.",
        genreId = 0,
        cover = album.coverUrl,
    )
