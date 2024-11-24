package com.andannn.melodify.core.syncer.util

import com.andannn.melodify.core.syncer.model.AudioData
import io.github.aakira.napier.Napier
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.Collections.replaceAll

/**
 * Extract tag from audio file
 */
fun extractTagFromAudioFile(filePath: String): AudioData? {
    return try {
        val file = File(filePath)

        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tag
        AudioData(
            id = -1, // assign id later.
            albumId = -1, // assign id later.
            artistId = -1, // assign id later.
            genreId = -1,  // assign id later.
            sourceUri = filePath,
            title = tag.getFirst(FieldKey.TITLE),
            duration = audioFile.audioHeader.trackLength,
            modifiedDate = file.lastModified(),
            size = file.length().toInt(),
            mimeType = audioFile.audioHeader.format,
            album = tag.getFirst(FieldKey.ALBUM),
            artist = tag.getFirst(FieldKey.ARTIST),
            cdTrackNumber = tag.getFirst(FieldKey.TRACK).toInt(),
            discNumber = tag.getFirst(FieldKey.DISC_NO).toInt(),
            numTracks = tag.getFirst(FieldKey.TRACK_TOTAL).toInt(),
            bitrate = audioFile.audioHeader.bitRateAsNumber.toInt(),
            genre = tag.getFirst(FieldKey.GENRE),
            year = tag.getFirst(FieldKey.YEAR),
            composer = tag.getFirst(FieldKey.COMPOSER),
            // TODO implement later
//            cover = tag.getFirst(FieldKey.COVER_ART),
        )
    } catch (e: Exception) {
        Napier.e("extractTagFromAudioFIle failed", e)
        null
    }
}

private fun String.toInt(): Int? {
    return toIntOrNull() ?: split("/").firstOrNull()?.toIntOrNull()
}