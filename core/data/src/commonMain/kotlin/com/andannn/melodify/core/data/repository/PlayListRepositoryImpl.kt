package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.database.PlayListDao
import com.andannn.melodify.core.database.entity.PlayListAndMedias
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

internal class PlayListRepositoryImpl(
    private val playListDao: PlayListDao,
): PlayListRepository {

    override fun getAllPlayListFlow(): Flow<List<PlayListItemModel>> =
        playListDao
            .getAllPlayListFlow()
            .map(::mapPlayListToAudioList)

    override suspend fun getPlayListById(playListId: Long) =
        playListDao.getPlayList(playListId)?.let {
            PlayListItemModel(
                id = it.playList.id.toString(),
                name = it.playList.name,
                artWorkUri = it.playList.artworkUri ?: "",
                trackCount = it.medias.size
            )
        }

    override suspend fun addMusicToPlayList(playListId: Long, musics: List<String>): List<Long> {
        val insertedIndexList = playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = musics.map {
                PlayListWithMediaCrossRef(
                    playListId = playListId,
                    mediaStoreId = it,
                    addedDate = 0
                )
            }
        )

        return insertedIndexList
            .filterTo(mutableListOf()) { it == -1L }
            .toList()
    }

    override fun isMediaInFavoritePlayListFlow(mediaStoreId: String) =
        playListDao.getIsMediaInPlayListFlow(
            PlayListDao.FAVORITE_PLAY_LIST_ID.toString(),
            mediaStoreId
        )

    override suspend fun toggleFavoriteMedia(mediaId: String) {
        val isFavorite = playListDao.getIsMediaInPlayListFlow(
            PlayListDao.FAVORITE_PLAY_LIST_ID.toString(),
            mediaId
        ).first()
        if (isFavorite) {
            removeMusicFromFavoritePlayList(listOf(mediaId))
        } else {
            addMusicToFavoritePlayList(listOf(mediaId))
        }
    }

    override suspend fun removeMusicFromPlayList(playListId: Long, mediaIdList: List<String>) =
        playListDao.deleteMediaFromPlayList(playListId, mediaIdList)

    override fun getAudiosOfPlayListFlow(playListId: Long): Flow<List<AudioItemModel>> {
        return playListDao.getPlayListFlowById(playListId)
            .mapLatest(::mapPlayListToAudioList)
    }

    override suspend fun getAudiosOfPlayList(playListId: Long): List<AudioItemModel> =
        coroutineScope {
            val playList = playListDao.getPlayListFlowById(playListId).first()

            ensureActive()

            mapPlayListToAudioList(playList)
        }

    private suspend fun mapPlayListToAudioList(playList: PlayListAndMedias): List<AudioItemModel> {
        val mediaStoreIds = playList.medias.map { it.mediaStoreId }
        return getMediaListFromIds(mediaStoreIds)
    }
}

expect suspend fun getMediaListFromIds(mediaStoreIds: List<String>): List<AudioItemModel>

private fun mapPlayListToAudioList(list: List<PlayListWithMediaCount>): List<PlayListItemModel> {
    return list.map { it.toAppItem() }
}

private fun PlayListWithMediaCount.toAppItem() = PlayListItemModel(
    id = playListEntity.id.toString(),
    name = playListEntity.name,
    artWorkUri = playListEntity.artworkUri ?: "",
    trackCount = mediaCount
)