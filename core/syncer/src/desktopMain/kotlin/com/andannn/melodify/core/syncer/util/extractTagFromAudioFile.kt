package com.andannn.melodify.core.syncer.util

import com.andannn.melodify.core.syncer.model.AudioData
import io.github.aakira.napier.Napier
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension

/**
 * Extract tag from audio file
 */
fun extractTagFromAudioFile(filePath: String): AudioData? {
    return try {
        val file = File(filePath)

        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tag
// TODO: cache error and set default for method getFirst
        AudioData(
            id = -1, // assign id later.
            albumId = -1, // assign id later.
            artistId = -1, // assign id later.
            genreId = -1,  // assign id later.
            sourceUri = convertAbsoluteFilePathToFileUri(filePath),
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

private fun Tag.tryGetFirst(key: FieldKey): String? {
    return try {
        getFirst(key)
    } catch (e: Exception) {
        null
    }
}

private fun String.toInt(): Int? {
    return toIntOrNull() ?: split("/").firstOrNull()?.toIntOrNull()
}

private fun getCoverFileInFolder(filePath: String): String? {
    return Path(filePath).parent
        ?.let { folder ->
            val imageFiles = Files.walk(folder)
                .filter {
                    Files.isRegularFile(it)
                }
                .filter {
                    isImageFile(it.toString())
                }
                .toList()

            return imageFiles
                .find { it.nameWithoutExtension.matchAlbumCover() }
                .toString()
        }
}

private fun String.matchAlbumCover() = contains("folder", ignoreCase = true) ||
            contains("cover", ignoreCase = true)