//package com.andannn.melodify.core.database
//
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import com.andannn.melodify.core.database.entity.LyricEntity
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.runBlocking
//
//class DatabaseTest {
//    private lateinit var dataBase: MelodifyDataBase
//    private lateinit var lyricDao: LyricDao
//
//    private val dummyLyricEntities = listOf(
//        LyricEntity(
//            id = 1,
//            name = "name",
//            trackName = "trackName",
//            artistName = "artistName",
//            albumName = "albumName",
//            duration = 1.0,
//            instrumental = true,
//            plainLyrics = "plainLyrics",
//            syncedLyrics = "syncedLyrics"
//        ),
//        LyricEntity(
//            id = 2,
//            name = "name",
//            trackName = "trackName",
//            artistName = "artistName",
//            albumName = "albumName",
//            duration = 1.0,
//            instrumental = true,
//            plainLyrics = "plainLyrics",
//            syncedLyrics = "syncedLyrics"
//        )
//    )
//
//    @BeforeTest
//    fun setUpDatabase() {
//        dataBase = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            MelodifyDataBase::class.java
//        ).allowMainThreadQueries().build()
//
//        lyricDao = dataBase.getLyricDao()
//    }
//
//    @BeforeTest
//    fun closeDatabase() {
//        dataBase.close()
//    }
//
//    @Test
//    fun `get lyric by media store id`() = runBlocking {
//        lyricDao.insertLyricOfMedia(mediaStoreId = 99, lyric = dummyLyricEntities[0])
//
//        val lyric = lyricDao.getLyricByMediaStoreIdFlow(99).first()
//        assertEquals(dummyLyricEntities[0], lyric)
//    }
//
//    @Test
//    fun `get lyric by media store id not exist`() = runBlocking {
//        lyricDao.insertLyricOfMedia(mediaStoreId = 99, lyric = dummyLyricEntities[0])
//
//        val lyric = lyricDao.getLyricByMediaStoreIdFlow(100).first()
//        assertEquals(null, lyric)
//    }
//}