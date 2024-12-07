package com.andannn.melodify.core.database

import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal expect fun createInMemoryDatabase(): MelodifyDataBase

class DatabaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    private var _database: MelodifyDataBase? = null
    private val database get() = _database!!
    private val lyricDao: LyricDao get() = database.getLyricDao()
    private val playListDao: PlayListDao get() = database.getPlayListDao()

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

    @BeforeTest
    fun openDatabase() {
        _database = createInMemoryDatabase()
    }

    @AfterTest
    fun closeDatabase() {
        _database?.close()
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

        val playLists = playListDao.getAllPlayListFlow().first()
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
        val playLists = playListDao.getAllPlayListFlow().first()
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
                    addedDate = 1,
                    artist = "",
                    title = ""
                ),
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "2",
                    addedDate = 2,
                    artist = "",
                    title = ""
                )
            )
        )

        val playList = playListDao.getPlayListFlowById(1).first()!!
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
                    addedDate = 1,
                    artist = "",
                    title = ""
                ),
            )
        )

        val playList = playListDao.getPlayListFlowById(1).first()!!
        assertEquals(1, playList.medias.size)

        val insertIds = playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "1",
                    addedDate = 1,
                    artist = "",
                    title = ""
                ),
            )
        )
        assertEquals(-1, insertIds.first())

        val playList2 = playListDao.getPlayListFlowById(1).first()
        assertEquals(1, playList2!!.medias.size)
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
                    addedDate = 1,
                    artist = "",
                    title = ""
                ),
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "2",
                    addedDate = 2,
                    artist = "",
                    title = ""
                )
            )
        )

        val playLists = playListDao.getAllPlayListFlow().first()
        assertEquals(2, playLists.first().mediaCount)

        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = 1,
                    mediaStoreId = "3",
                    addedDate = 3,
                    artist = "",
                    title = ""
                ),
            )
        )
    }

    @Test
    fun check_is_media_in_play_list() = testScope.runTest {
        val res = playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    id = 12,
                    name = "",
                    createdDate = 1L,
                    artworkUri = null
                ),
            )
        )
        val mediaStoreId = "1"
        assertEquals(false, playListDao.getIsMediaInPlayListFlow(res.first().toString(), mediaStoreId).first())

        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs = listOf(
                PlayListWithMediaCrossRef(
                    playListId = res.first(),
                    mediaStoreId = mediaStoreId,
                    addedDate = 1L,
                    artist = "",
                    title = ""
                ),
            )
        )
        assertEquals(true, playListDao.getIsMediaInPlayListFlow(res.first().toString(), mediaStoreId).first())
    }

    @Test
    fun delete_media_from_play_list() = testScope.runTest {
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
                    addedDate = 1,
                    artist = "",
                    title = ""
                ),
            )
        )

        assertEquals(1, playListDao.getPlayListFlowById(1).first()!!.medias.size)

        playListDao.deleteMediaFromPlayList(1, listOf("1"))
        assertEquals(0, playListDao.getPlayListFlowById(1).first()!!.medias.size)
    }

    @Test
    fun get_duplicate_media_in_play_list() = testScope.runTest {
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
                    addedDate = 1,
                    artist = "",
                    title = ""
                ),
            )
        )

        val res = playListDao.getDuplicateMediaInPlayList(1, listOf("1"))
        assertEquals(listOf("1"), res)
    }

    @Test
    fun insert_play_lists_test() = testScope.runTest {
        val ids = playListDao.inertPlayLists(
            entities = listOf(
                PlayListEntity(
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                ),
                PlayListEntity(
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                ),
            )
        )

        assertEquals(listOf(1L, 2L), ids)
    }

    @Test
    fun delete_playlist_by_id_test() = testScope.runTest {
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
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    id = 2,
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                )
            )
        )
        playListDao.insertPlayListEntities(
            entities = listOf(
                PlayListEntity(
                    id = 3,
                    createdDate = 1,
                    artworkUri = null,
                    name = "name"
                )
            )
        )
        playListDao.deletePlayListById(2)
        val playLists = playListDao.getAllPlayListFlow().first()
        assertEquals(2, playLists.size)
        assertEquals(listOf(1L, 3L), playLists.map { it.playListEntity.id })
    }
}