package com.andannn.melodify.core.database

import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal expect val dummyDatabase: MelodifyDataBase

class DatabaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val lyricDao: LyricDao = dummyDatabase.getLyricDao()
    private val playListDao: PlayListDao = dummyDatabase.getPlayListDao()

    private val dummyLyricEntities = listOf(
        LyricEntity(
            id = 1,
            name = "name",
            trackName = "trackName",
            artistName = "artistName",
            albumName = "albumName",
            duration = 1.0,
            instrumental = true,
            plainLyrics = "plainLyrics",
            syncedLyrics = "syncedLyrics"
        ),
        LyricEntity(
            id = 2,
            name = "name",
            trackName = "trackName",
            artistName = "artistName",
            albumName = "albumName",
            duration = 1.0,
            instrumental = true,
            plainLyrics = "plainLyrics",
            syncedLyrics = "syncedLyrics"
        )
    )

    @AfterTest
    fun closeDatabase() {
        dummyDatabase.close()
    }

    @Test
    fun get_lyric_by_media_store_id() = testScope.runTest {
        lyricDao.insertLyricOfMedia(mediaStoreId = "99", lyric = dummyLyricEntities[0])

        val lyric = lyricDao.getLyricByMediaIdFlow("99").first()
        assertEquals(dummyLyricEntities[0], lyric)
    }

    @Test
    fun get_lyric_by_media_store_id_not_exist() = testScope.runTest {
        lyricDao.insertLyricOfMedia(mediaStoreId = "99", lyric = dummyLyricEntities[0])

        val lyric = lyricDao.getLyricByMediaIdFlow("100").first()
        assertEquals(null, lyric)
    }

    @Test
    fun insert_play_list() = testScope.runTest {
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                )
            )
        )

        val playLists = playListDao.getPlayListFlow().first()
        assertEquals(1, playLists.size)
        assertEquals(1, playLists.first().playListEntity.id)
    }

    @Test
    fun get_play_list_order_by_created_date() = testScope.runTest {
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                ),
                PlayListEntity(
                    createdDate = 3,
                    artworkUri = null,
                    name = "name"
                ),
                PlayListEntity(
                    createdDate = 2,
                    artworkUri = null,
                    name = "name"
                ),
            )
        )
        val playLists = playListDao.getPlayListFlow().first()
        assertEquals(3, playLists.size)
        assertEquals(listOf(2L, 3L, 1L), playLists.map { it.playListEntity.id })
    }

    @Test
    fun insert_play_list_with_media_cross_ref() = testScope.runTest {
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    id = 1,
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                )
            )
        )
        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "1",
                    addedDate = 1
                ),
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "2",
                    addedDate = 2
                )
            )
        )

        val playList = playListDao.getPlayListFlow(1).first()
        assertEquals(2, playList.medias.size)
    }

    @Test
    fun insert_same_play_list_with_media_cross_ref() = testScope.runTest {
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    id = 1,
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                )
            )
        )

        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "1",
                    addedDate = 1
                ),
            )
        )

        val playList = playListDao.getPlayListFlow(1).first()
        assertEquals(1, playList.medias.size)

        val insertIds = playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "1",
                    addedDate = 1
                ),
            )
        )
        assertEquals(-1, insertIds.first())

        val playList2 = playListDao.getPlayListFlow(1).first()
        assertEquals(1, playList2.medias.size)
    }

    @Test
    fun get_play_list_with_media_count() = testScope.runTest {
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    id = 1,
                    createdDate = 1,
                    artworkUri = null,
                    name = "name",
                )
            )
        )

        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "1",
                    addedDate = 1
                ),
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "2",
                    addedDate = 2
                )
            )
        )

        val playLists = playListDao.getPlayListFlow().first()
        assertEquals(2, playLists.first().mediaCount)

        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "3",
                    addedDate = 3
                ),
            )
        )
    }
}