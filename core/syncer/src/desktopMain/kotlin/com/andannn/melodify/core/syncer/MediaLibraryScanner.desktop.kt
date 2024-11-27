package com.andannn.melodify.core.syncer

import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.syncer.model.AlbumData
import com.andannn.melodify.core.syncer.model.ArtistData
import com.andannn.melodify.core.syncer.model.AudioData
import com.andannn.melodify.core.syncer.model.GenreData
import com.andannn.melodify.core.syncer.model.MediaDataModel
import com.andannn.melodify.core.syncer.util.extractTagFromAudioFile
import com.andannn.melodify.core.syncer.util.scanAllLibraryAudioFile
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

private const val TAG = "MediaLibraryScanner"

class MediaLibraryScannerImpl(
    private val mediaLibraryDao: MediaLibraryDao,
    private val userSettingPreferences: UserSettingPreferences,
) : MediaLibraryScanner {
    override suspend fun scanMediaData(): MediaDataModel {

        // 1: Get All media from database
        // 2: Scan all files in library path and generated Key (generate hash from file path and last modify date).
        // 3: Loop through all files in library path and create new media data list. rules:
        //      - If key exist in db, map db entity to AudioData (skip extract metadata).
        //      - If id not exist in db. extract metadata from file and create new AudioData
        // 4: Group album from created AudioData list
        // 5: Group artist from created AudioData list
        // 6: Group genre from created AudioData list

        val allMediaEntity = mediaLibraryDao.getAllMediaFlow().first()
        val mediaInDb = allMediaEntity.associateBy { it.id }

// TODO Get Path from DataStore after implement library path setting feature.
//        val libraryPathSet = userSettingPreferences.userDate.first().libraryPath
        val libraryPathSet = setOf(
            "/Volumes/PS2000/Music"
        )

        val audioFileWithLastModifyDateList = scanAllLibraryAudioFile(libraryPathSet)

        Napier.d(tag = TAG) { "scanMediaData: ${audioFileWithLastModifyDateList.size} files found" }
        val audioData = coroutineScope {
            val tasksDeferredOrResult = audioFileWithLastModifyDateList.map { fileWithKey ->
                if (mediaInDb.containsKey(fileWithKey.key)) {
                    Napier.d(tag = TAG) { "skip extract tag from audio file: ${fileWithKey.path}" }

                    mediaInDb[fileWithKey.key]!!.toAudioData()
                } else {
                    async(Dispatchers.IO) {
                        Napier.d(tag = TAG) { "extract tag from audio file E: ${fileWithKey.path}, ${Thread.currentThread().name}" }
                        val result = extractTagFromAudioFile(fileWithKey.path)?.copy(
                            id = fileWithKey.key
                        )
                        Napier.d(tag = TAG) { "extract tag from audio file X: ${result}, ${Thread.currentThread().name}" }
                        result
                    }
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

        val albumMap = audioData.groupBy { it.album }

        val albumDataList = albumMap.map { (album, audioDataList) ->
            val title = album ?: ""
            AlbumData(
                albumId = title.hashCode().toLong(),
                title = title,
                trackCount = audioDataList.size,
                coverUri = audioDataList.firstOrNull()?.cover,
            )
        }


        val artistMap = audioData.groupBy { it.artist }

        val artistDataList = artistMap.map { (artist, audioDataList) ->
            val title = artist ?: ""
            ArtistData(
                artistId = title.hashCode().toLong(),
                name = title,
                trackCount = audioDataList.size,
                artistCoverUri = audioDataList.firstOrNull()?.cover,
            )
        }

        val genreMap = audioData.groupBy { it.genre }

        val genreDataList = genreMap.map { (genre, _) ->
            val title = genre ?: ""

            GenreData(
                genreId = title.hashCode().toLong(),
                name = title,
            )
        }

        val audioDataListWitId = audioData.map { audio ->
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


}

private fun MediaEntity.toAudioData() = AudioData(
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
