/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.helper.paging.PagingProvider
import com.andannn.melodify.core.database.helper.paging.PagingProviderFactory
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.syncer.model.AudioData
import com.andannn.melodify.core.syncer.model.MediaDataModel
import com.andannn.melodify.core.syncer.util.extractTagFromAudioFile
import com.andannn.melodify.core.syncer.util.generateHashKey
import com.andannn.melodify.core.syncer.util.scanAllAudioFile
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.toPath

private const val TAG = "MediaLibraryScanner"

internal class LocalMediaLibraryScanner(
    private val mediaLibraryDao: MediaLibraryDao,
    private val userSettingPreferences: UserSettingPreferences,
) : MediaLibraryScanner {
    private val allMediaPagingProvider: PagingProvider<MediaEntity> =
        PagingProviderFactory.allMediaPagingProvider()

    override suspend fun scanAllMedia(): MediaDataModel {
        // 1: Get All media from database
        // 2: Scan all files in library path and generated Key (generate hash from file path and last modify date).
        // 3: Loop through all files in library path and create new media data list. rules:
        //      - If key exist in db, map db entity to AudioData (skip extract metadata).
        //      - If id not exist in db. extract metadata from file and create new AudioData
        // 4: Group album from created AlbumData list
        // 5: Group artist from created AudioData list
        // 6: Group genre from created GenreData list
        // 7: Insert all data to database.

        val allMediaEntity = allMediaPagingProvider.getDataFlow().first()
        val mediaInDb = allMediaEntity.associateBy { it.id }

        val libraryPathSet = userSettingPreferences.userDate.first().libraryPath

// TODO: Scan file in worker thread.
        val audioFiles = scanAllAudioFile(libraryPathSet)

        Napier.d(tag = TAG) { "scanMediaData: ${audioFiles.size} files found" }
        val audioData =
            coroutineScope {
                val tasksDeferredOrResult =
                    audioFiles.map { filePath ->
                        val id = generateHashKey(filePath)
                        if (mediaInDb.containsKey(id)) {
                            Napier.d(tag = TAG) { "skip extract tag from audio file: $filePath" }

                            mediaInDb[id]!!.toAudioData()
                        } else {
                            asyncTaskForExtractTag(filePath)
                        }
                    }

                tasksDeferredOrResult.mapNotNull { taskOrResult ->
                    if (taskOrResult is Deferred<*>) {
                        // wait extract tag from audio file.
                        taskOrResult.await() as AudioData?
                    } else {
                        // found media in db, just return result
                        taskOrResult as AudioData
                    }
                }
            }
        return audioData.mapToMediaData()
    }

    override suspend fun scanMediaByUri(uris: List<String>): MediaDataModel {
        val audios =
            coroutineScope {
                val deferredList =
                    uris.map { uri ->
                        asyncTaskForExtractTag(URI.create(uri).toPath())
                    }
                deferredList.awaitAll().filterNotNull()
            }

        return audios.mapToMediaData()
    }
}

private fun CoroutineScope.asyncTaskForExtractTag(filePath: Path) =
    async(Dispatchers.IO) {
        Napier.d(tag = TAG) { "extract tag from audio file E: $filePath, ${Thread.currentThread().name}" }
        val result =
            extractTagFromAudioFile(filePath)?.copy(
                id = generateHashKey(filePath),
            )
        Napier.d(tag = TAG) { "extract tag from audio file X: $result, ${Thread.currentThread().name}" }
        result
    }

private fun MediaEntity.toAudioData() =
    AudioData(
        id = id,
        sourceUri = sourceUri ?: "",
        title = title ?: "",
        duration = duration,
        modifiedDate = modifiedDate,
        size = size,
        mimeType = mimeType,
        album = album,
        albumId = albumId,
        artist = artist,
        artistId = artistId,
        cdTrackNumber = cdTrackNumber,
        discNumber = discNumber,
        numTracks = numTracks,
        bitrate = bitrate,
        genre = genre,
        genreId = genreId,
        year = year,
        composer = composer,
        cover = cover,
    )
