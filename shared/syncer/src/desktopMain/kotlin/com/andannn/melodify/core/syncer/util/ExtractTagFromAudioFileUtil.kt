/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.util

import com.andannn.melodify.core.syncer.model.AudioData
import io.github.aakira.napier.Napier
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

/**
 * Extract tag from audio file
 */
fun extractTagFromAudioFile(filePath: Path): AudioData? {
    return try {
        val file = File(filePath.toString())
        val audioFile: AudioFile
        try {
            audioFile = AudioFileIO.read(file)
        } catch (e: CannotReadException) {
            Napier.e("extractTagFromAudioFIle failed", e)
            return null
        }

        val tag = audioFile.tag
// TODO: cache error and set default for method getFirst
        AudioData(
            // assign id later.
            id = -1,
            // assign id later.
            albumId = -1,
            // assign id later.
            artistId = -1,
            // assign id later.
            genreId = -1,
            sourceUri = filePath.toUri().toString(),
            title = tag.tryGetFirst(FieldKey.TITLE) ?: "",
            duration = audioFile.audioHeader.trackLength,
            modifiedDate = file.lastModified(),
            size = file.length().toInt(),
            mimeType = audioFile.audioHeader.format,
            album = tag.tryGetFirst(FieldKey.ALBUM),
            artist = tag.tryGetFirst(FieldKey.ARTIST),
            cdTrackNumber = tag.tryGetFirst(FieldKey.TRACK)?.toInt(),
            discNumber = tag.tryGetFirst(FieldKey.DISC_NO)?.toInt(),
            numTracks = tag.tryGetFirst(FieldKey.TRACK_TOTAL)?.toInt(),
            bitrate = audioFile.audioHeader.bitRateAsNumber.toInt(),
            genre = tag.tryGetFirst(FieldKey.GENRE),
            year = tag.tryGetFirst(FieldKey.YEAR),
            composer = tag.tryGetFirst(FieldKey.COMPOSER),
            cover = getCoverFileInFolder(filePath),
        )
    } catch (e: Exception) {
        Napier.e("extractTagFromAudioFIle failed", e)
        null
    }
}

private fun Tag.tryGetFirst(key: FieldKey): String? =
    try {
        getFirst(key)
    } catch (e: Exception) {
        null
    }

private fun String.toInt(): Int? = toIntOrNull() ?: split("/").firstOrNull()?.toIntOrNull()

private fun getCoverFileInFolder(filePath: Path): String? {
    return filePath.parent
        ?.let { folder ->
            val imageFiles =
                Files
                    .walk(folder)
                    .filter {
                        Files.isRegularFile(it)
                    }.filter {
                        isImageFile(it.toString())
                    }.toList()

            return imageFiles
                .find { it.nameWithoutExtension.matchAlbumCover() }
                .toString()
        }
}

private fun String.matchAlbumCover() =
    contains("folder", ignoreCase = true) ||
        contains("cover", ignoreCase = true)
