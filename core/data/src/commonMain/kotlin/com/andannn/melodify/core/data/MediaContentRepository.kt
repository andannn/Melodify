package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.database.PlayListDao
import kotlinx.coroutines.flow.Flow

interface MediaContentRepository {
    /**
     * Return flow of all media items
     */
    fun getAllMediaItemsFlow(): Flow<List<AudioItemModel>>

    /**
     * Return flow of all albums
     */
    fun getAllAlbumsFlow(): Flow<List<AlbumItemModel>>

    /**
     * Return flow of all artists
     */
    fun getAllArtistFlow(): Flow<List<ArtistItemModel>>

    /**
     * Return flow of all genres
     */
    fun getAllGenreFlow(): Flow<List<GenreItemModel>>

    /**
     * Return flow of all playLists
     */
    fun getAllPlayListFlow(): Flow<List<PlayListItemModel>>

    /**
     * Return flow of audios of album
     */
    fun getAudiosOfAlbumFlow(albumId: String): Flow<List<AudioItemModel>>

    /**
     * Return audios of artist
     */
    suspend fun getAudiosOfAlbum(albumId: String): List<AudioItemModel>

    /**
     * Return flow of audios of artist
     */
    fun getAudiosOfArtistFlow(artistId: String): Flow<List<AudioItemModel>>

    /**
     * Return audios of artist
     */
    suspend fun getAudiosOfArtist(artistId: String): List<AudioItemModel>

    /**
     * Return flow of audios of genre
     */
    fun getAudiosOfGenreFlow(genreId: String): Flow<List<AudioItemModel>>

    /**
     * Return audios of genre
     */
    suspend fun getAudiosOfGenre(genreId: String): List<AudioItemModel>

    /**
     * Return flow of audios of playList
     */
    fun getAudiosOfPlayListFlow(playListId: Long): Flow<List<AudioItemModel>>

    /**
     * Return audios of playList
     */
    suspend fun getAudiosOfPlayList(playListId: Long): List<AudioItemModel>

    /**
     * Return flow of album by albumId
     */
    fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumItemModel?>

    /**
     * Return flow of artist by artistId
     */
    fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistItemModel?>

    /**
     * Return flow of genre by genreId
     */
    fun getGenreByGenreIdFlow(genreId: String): Flow<GenreItemModel?>

    /**
     * Return album by albumId
     */
    suspend fun getAlbumByAlbumId(albumId: String): AlbumItemModel?

    /**
     * Return artist by artistId
     */
    suspend fun getArtistByArtistId(artistId: String): ArtistItemModel?

    /**
     * Return genre by genreId
     */
    suspend fun getGenreByGenreId(genreId: String): GenreItemModel?

    /**
     * Return playList by playListId
     */
    suspend fun getPlayListById(playListId: Long): PlayListItemModel?

    /**
     * Add musics to favorite playList
     */
    suspend fun addMusicToFavoritePlayList(musics: List<String>) =
        addMusicToPlayList(PlayListDao.FAVORITE_PLAY_LIST_ID, musics)

    /**
     * Add musics to playList
     *
     * return index of musics that already exist
     */
    suspend fun addMusicToPlayList(playListId: Long, musics: List<String>): List<Long>

    /**
     * Return flow of whether [mediaStoreId] is in favorite playList
     */
    fun isMediaInFavoritePlayListFlow(mediaStoreId: String): Flow<Boolean>

    /**
     * Toggle favorite media
     */
    suspend fun toggleFavoriteMedia(mediaId: String)

    /**
     * Remove musics from favorite playList
     */
    suspend fun removeMusicFromFavoritePlayList(mediaIdList: List<String>) =
        removeMusicFromPlayList(PlayListDao.FAVORITE_PLAY_LIST_ID, mediaIdList)

    /**
     * Remove musics from playList
     */
    suspend fun removeMusicFromPlayList(playListId: Long, mediaIdList: List<String>)
}