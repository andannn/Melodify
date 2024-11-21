package com.andannn.melodify.core.library.mediastore

import com.andannn.melodify.core.library.mediastore.model.AlbumData
import com.andannn.melodify.core.library.mediastore.model.ArtistData
import com.andannn.melodify.core.library.mediastore.model.AudioData
import com.andannn.melodify.core.library.mediastore.model.GenreData
import com.andannn.melodify.core.library.mediastore.model.MediaDataModel

interface MediaLibrary {
    suspend fun getAllMusicData(): List<AudioData>

    suspend fun getAllAlbumData(): List<AlbumData>

    suspend fun getAllArtistData(): List<ArtistData>

    suspend fun getAllGenreData(): List<GenreData>

    suspend fun getGenreById(id: Long): GenreData?

    suspend fun getArtistById(id: Long): ArtistData?

    suspend fun getAlbumById(id: Long): AlbumData?

    suspend fun getAudioInAlbum(id: Long): List<AudioData>

    suspend fun getAudioOfArtist(id: Long): List<AudioData>

    suspend fun getAudioOfGenre(id: Long): List<AudioData>

    suspend fun getAudioByIds(mediaStoreIds: List<String>): List<AudioData>

    suspend fun getMediaData(): MediaDataModel
}