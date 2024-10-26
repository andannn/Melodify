package com.andannn.melodify.core.database

import kotlin.test.BeforeTest
import com.andannn.melodify.core.database.entity.LyricEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal expect val dummyDatabase: MelodifyDataBase

class DatabaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private lateinit var lyricDao: LyricDao

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
    fun setUpDatabase() {

        lyricDao = dummyDatabase.getLyricDao()
    }

    @BeforeTest
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
}