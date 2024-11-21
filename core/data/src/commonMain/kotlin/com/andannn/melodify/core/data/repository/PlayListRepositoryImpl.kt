package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.util.allAudioChangedEventFlow
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.entity.PlayListAndMedias
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCount
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock

internal class PlayListRepositoryImpl(
    private val playListDao: PlayListDao,
) : PlayListRepository {

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

    override suspend fun addMusicToPlayList(
        playListId: Long,
        musics: List<AudioItemModel>
    ): List<Long> {
        val insertedIndexList = playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = musics.map {
                PlayListWithMediaCrossRef(
                    playListId = playListId,
                    mediaStoreId = it.id,
                    artist = it.artist,
                    title = it.name,
                    addedDate = Clock.System.now().toEpochMilliseconds(),
                )
            }
        )

        return insertedIndexList
            .mapIndexed { index, insertedIndex ->
                if (insertedIndex == -1L) index.toLong() else null
            }
            .filterNotNull()
            .toList()
    }

    override suspend fun getDuplicatedMediaInPlayList(
        playListId: Long,
        musics: List<AudioItemModel>
    ): List<String> {
        return playListDao.getDuplicateMediaInPlayList(playListId, musics.map { it.id })
    }

    override fun isMediaInFavoritePlayListFlow(mediaStoreId: String) =
        playListDao.getIsMediaInPlayListFlow(
            PlayListDao.FAVORITE_PLAY_LIST_ID.toString(),
            mediaStoreId
        )

    override suspend fun toggleFavoriteMedia(audio: AudioItemModel) {
        val isFavorite = playListDao.getIsMediaInPlayListFlow(
            PlayListDao.FAVORITE_PLAY_LIST_ID.toString(),
            audio.id
        ).first()
        if (isFavorite) {
            removeMusicFromFavoritePlayList(listOf(audio.id))
        } else {
            addMusicToFavoritePlayList(listOf(audio))
        }
    }

    override suspend fun removeMusicFromPlayList(playListId: Long, mediaIdList: List<String>) =
        playListDao.deleteMediaFromPlayList(playListId, mediaIdList)

    override suspend fun createNewPlayList(name: String): Long {
        val ids = playListDao.insertPlayListEntities(
            listOf(
                PlayListEntity(
                    name = name,
                    createdDate = Clock.System.now().toEpochMilliseconds(),
                    artworkUri = null
                )
            )
        )
        return ids.first()
    }

    override suspend fun deletePlayList(playListId: Long) {
        playListDao.deletePlayListById(playListId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAudiosOfPlayListFlow(playListId: Long) =
        combine(
            allAudioChangedEventFlow(), // trigger flow when audio changed.
            playListDao.getPlayListFlowById(playListId).filterNotNull()
        ) { _, playList -> playList }
            .mapLatest(::mapPlayListToAudioList)

    override suspend fun getAudiosOfPlayList(playListId: Long): List<AudioItemModel> =
        coroutineScope {
            val playList = playListDao.getPlayListFlowById(playListId).first()
                ?: return@coroutineScope emptyList()

            ensureActive()

            mapPlayListToAudioList(playList)
        }

    private suspend fun mapPlayListToAudioList(playList: PlayListAndMedias): List<AudioItemModel> {
        return getMediaListFromIds(playList.medias)
    }
}

expect suspend fun getMediaListFromIds(playListItems: List<PlayListWithMediaCrossRef>): List<AudioItemModel>

private fun mapPlayListToAudioList(list: List<PlayListWithMediaCount>): List<PlayListItemModel> {
    return list.map { it.toAppItem() }
}

private fun PlayListWithMediaCount.toAppItem() = PlayListItemModel(
    id = playListEntity.id.toString(),
    name = playListEntity.name,
    artWorkUri = playListEntity.artworkUri ?: "",
    trackCount = mediaCount
)