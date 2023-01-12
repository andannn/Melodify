package com.andanana.musicplayer.core.data.repository

import com.andanana.musicplayer.core.model.AlbumInfo
import com.andanana.musicplayer.core.model.ArtistInfo
import com.andanana.musicplayer.core.model.MusicInfo

interface LocalMusicRepository {
    suspend fun getAllMusicInfo(): List<MusicInfo>

    suspend fun getMusicInfoByAlbumId(id: Long): List<MusicInfo>

    suspend fun getMusicInfoByArtistId(id: Int): List<MusicInfo>

    suspend fun getAllAlbumInfo(): List<AlbumInfo>

    suspend fun getAllArtistInfo(): List<ArtistInfo>
}
