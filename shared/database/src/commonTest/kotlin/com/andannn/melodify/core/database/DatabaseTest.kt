/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.RoomDatabase
import androidx.room.execSQL
import androidx.room.useReaderConnection
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRef
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.database.entity.SortOptionData
import com.andannn.melodify.core.database.entity.SortRuleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal expect fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase>

class DatabaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private lateinit var database: MelodifyDataBase
    private val lyricDao: LyricDao get() = database.getLyricDao()
    private val playListDao: PlayListDao get() = database.getPlayListDao()
    private val libraryDao: MediaLibraryDao get() = database.getMediaLibraryDao()

    private val dummyLyricEntities =
        listOf(
            LyricEntity(
                id = 1,
                name = "name",
                trackName = "trackName",
                artistName = "artistName",
                albumName = "albumName",
                duration = 1.0,
                instrumental = true,
                plainLyrics = "plainLyrics",
                syncedLyrics = "syncedLyrics",
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
                syncedLyrics = "syncedLyrics",
            ),
        )

    @BeforeTest
    fun openDatabase() {
        database = inMemoryDatabaseBuilder().setUpDatabase().build()
    }

    @AfterTest
    fun closeDatabase() {
        database?.close()
    }

    @Test
    @IgnoreAndroidUnitTest
    fun get_lyric_by_media_store_id() =
        testScope.runTest {
            lyricDao.insertLyricOfMedia(mediaStoreId = "99", lyric = dummyLyricEntities[0])

            val lyric = lyricDao.getLyricByMediaIdFlow("99").first()
            assertEquals(dummyLyricEntities[0], lyric)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun get_lyric_by_media_store_id_not_exist() =
        testScope.runTest {
            lyricDao.insertLyricOfMedia(mediaStoreId = "99", lyric = dummyLyricEntities[0])

            val lyric = lyricDao.getLyricByMediaIdFlow("100").first()
            assertEquals(null, lyric)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun insert_play_list() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                    ),
            )

            val playLists = playListDao.getAllPlayListFlow(true).first()
            assertEquals(1, playLists.size)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun get_play_list_order_by_created_date() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                        PlayListEntity(
                            createdDate = 3,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                        PlayListEntity(
                            createdDate = 2,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = false,
                        ),
                    ),
            )
            playListDao.getAllPlayListFlow(true).first().also {
                assertEquals(2, it.size)
            }
            playListDao.getAllPlayListFlow(false).first().also {
                assertEquals(1, it.size)
            }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun insert_play_list_with_media_cross_ref() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 1,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                        ),
                    ),
            )
            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "1",
                            addedDate = 1,
                            artist = "",
                            title = "",
                        ),
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "2",
                            addedDate = 2,
                            artist = "",
                            title = "",
                        ),
                    ),
            )

            val playList = playListDao.getPlayListFlowById(1).first()!!
            assertEquals(2, playList.medias.size)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun insert_same_play_list_with_media_cross_ref() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 1,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                        ),
                    ),
            )

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "1",
                            addedDate = 1,
                            artist = "",
                            title = "",
                        ),
                    ),
            )

            val playList = playListDao.getPlayListFlowById(1).first()!!
            assertEquals(1, playList.medias.size)

            val insertIds =
                playListDao.insertPlayListWithMediaCrossRef(
                    crossRefs =
                        listOf(
                            PlayListWithMediaCrossRef(
                                playListId = 1,
                                mediaStoreId = "1",
                                addedDate = 1,
                                artist = "",
                                title = "",
                            ),
                        ),
                )
            assertEquals(-1, insertIds.first())

            val playList2 = playListDao.getPlayListFlowById(1).first()
            assertEquals(1, playList2!!.medias.size)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun get_play_list_with_media_count() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 1,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                    ),
            )

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "1",
                            addedDate = 1,
                            artist = "",
                            title = "",
                        ),
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "2",
                            addedDate = 2,
                            artist = "",
                            title = "",
                        ),
                    ),
            )

            val playLists = playListDao.getAllPlayListFlow(true).first()
            assertEquals(2, playLists.first().mediaCount)

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "3",
                            addedDate = 3,
                            artist = "",
                            title = "",
                        ),
                    ),
            )
        }

    @Test
    @IgnoreAndroidUnitTest
    fun check_is_media_in_play_list() =
        testScope.runTest {
            val res =
                playListDao.insertPlayListEntities(
                    entities =
                        listOf(
                            PlayListEntity(
                                id = 12,
                                name = "",
                                createdDate = 1L,
                                artworkUri = null,
                            ),
                        ),
                )
            val mediaStoreId = "1"
            assertEquals(
                false,
                playListDao.getIsMediaInPlayListFlow(res.first().toString(), mediaStoreId).first(),
            )

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = res.first(),
                            mediaStoreId = mediaStoreId,
                            addedDate = 1L,
                            artist = "",
                            title = "",
                        ),
                    ),
            )
            assertEquals(
                true,
                playListDao.getIsMediaInPlayListFlow(res.first().toString(), mediaStoreId).first(),
            )
        }

    @Test
    @IgnoreAndroidUnitTest
    fun delete_media_from_play_list() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 1,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                        ),
                    ),
            )
            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "1",
                            addedDate = 1,
                            artist = "",
                            title = "",
                        ),
                    ),
            )

            assertEquals(
                1,
                playListDao
                    .getPlayListFlowById(1)
                    .first()!!
                    .medias.size,
            )

            playListDao.deleteMediaFromPlayList(1, listOf("1"))
            assertEquals(
                0,
                playListDao
                    .getPlayListFlowById(1)
                    .first()!!
                    .medias.size,
            )
        }

    @Test
    @IgnoreAndroidUnitTest
    fun get_duplicate_media_in_play_list() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 1,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                        ),
                    ),
            )
            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListWithMediaCrossRef(
                            playListId = 1,
                            mediaStoreId = "1",
                            addedDate = 1,
                            artist = "",
                            title = "",
                        ),
                    ),
            )

            val res = playListDao.getDuplicateMediaInPlayList(1, listOf("1"))
            assertEquals(listOf("1"), res)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun insert_play_lists_test() =
        testScope.runTest {
            val ids =
                playListDao.insertPlayListEntities(
                    entities =
                        listOf(
                            PlayListEntity(
                                createdDate = 1,
                                artworkUri = null,
                                name = "name",
                            ),
                            PlayListEntity(
                                createdDate = 1,
                                artworkUri = null,
                                name = "name",
                            ),
                        ),
                )

            assertEquals(listOf(1L, 2L), ids)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun delete_playlist_by_id_test() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 1,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                    ),
            )
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 2,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                    ),
            )
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 3,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                            isAudioPlayList = true,
                        ),
                    ),
            )
            playListDao.deletePlayListById(2)
            val playLists = playListDao.getAllPlayListFlow(true).first()
            assertEquals(2, playLists.size)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun cascade_delete_test() =
        testScope.runTest {
            libraryDao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 1,
                            albumId = 2,
                            genreId = 3,
                            artistId = 4,
                            title = "title 1",
                        ),
                    ),
            )
            libraryDao.insertAlbums(
                albums =
                    listOf(
                        AlbumEntity(
                            albumId = 2,
                            title = "album 2",
                        ),
                    ),
            )
            libraryDao.insertGenres(
                genres =
                    listOf(
                        GenreEntity(
                            genreId = 3,
                            name = "genre 3",
                        ),
                    ),
            )
            libraryDao.insertArtists(
                artists =
                    listOf(
                        ArtistEntity(
                            artistId = 4,
                            name = "artist 4",
                        ),
                    ),
            )

            assertEquals(
                1,
                libraryDao.getAllAlbumFlow().first().size,
            )
            libraryDao.deleteAllMedias()
            assertEquals(0, libraryDao.getAllAlbumFlow().first().size)
            assertEquals(0, libraryDao.getAllGenreFlow().first().size)
            assertEquals(0, libraryDao.getAllArtistFlow().first().size)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun update_artist_count_test() =
        testScope.runTest {
            libraryDao.insertArtists(
                artists =
                    listOf(
                        ArtistEntity(
                            artistId = 4,
                            name = "artist 4",
                        ),
                    ),
            )
            assertEquals(0, libraryDao.getArtistByArtistId("4")?.trackCount)
            libraryDao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 1,
                            albumId = 2,
                            genreId = 3,
                            artistId = 4,
                            title = "title 1",
                        ),
                    ),
            )
            assertEquals(1, libraryDao.getArtistByArtistId("4")?.trackCount)
            libraryDao.deleteAllMedias()
            assertEquals(null, libraryDao.getArtistByArtistId("4"))
        }

    @Test
    @IgnoreAndroidUnitTest
    fun update_album_count_test() =
        testScope.runTest {
            libraryDao.insertAlbums(
                albums =
                    listOf(
                        AlbumEntity(
                            albumId = 2,
                            title = "album 2",
                        ),
                    ),
            )
            assertEquals(0, libraryDao.getAlbumByAlbumId("2")?.trackCount)
            libraryDao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 1,
                            albumId = 2,
                            genreId = 3,
                            artistId = 4,
                            title = "title 1",
                        ),
                    ),
            )
            assertEquals(1, libraryDao.getAlbumByAlbumId("2")?.trackCount)
            libraryDao.deleteAllMedias()
            assertEquals(null, libraryDao.getAlbumByAlbumId("2"))
        }

    @Test
    @IgnoreAndroidUnitTest
    fun fts_table_sync_test() =
        testScope.runTest {
            libraryDao.insertDummyData()
            database.verifyFtsTableSync(
                tableName = Tables.LIBRARY_ALBUM,
                ftsTableName = Tables.LIBRARY_FTS_ALBUM,
                matchContentName = AlbumColumns.TITLE,
            )
            database.verifyFtsTableSync(
                tableName = Tables.LIBRARY_ARTIST,
                ftsTableName = Tables.LIBRARY_FTS_ARTIST,
                matchContentName = ArtistColumns.NAME,
            )
            database.verifyFtsTableSync(
                tableName = Tables.LIBRARY_MEDIA,
                ftsTableName = Tables.LIBRARY_FTS_MEDIA,
                matchContentName = MediaColumns.TITLE,
            )
        }

    @Test
    @IgnoreAndroidUnitTest
    fun match_keyword_test() =
        testScope.runTest {
            libraryDao.insertDummyData()
            assertEquals(1, libraryDao.searchAlbum("title 1").size)
            assertEquals(1, libraryDao.searchAlbum("title 1").first().albumId)

            assertEquals(1, libraryDao.searchArtist("title 1").size)
            assertEquals(1, libraryDao.searchArtist("title 1").first().artistId)

            assertEquals(1, libraryDao.searchMedia("title 1").size)
            assertEquals(1, libraryDao.searchMedia("title 1").first().id)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun upsert_search_history_test() =
        testScope.runTest {
            val searchHistoryDao = database.getUserDataDao()
            searchHistoryDao.upsertSearchHistory(
                listOf(
                    SearchHistoryEntity(
                        searchDate = 1,
                        searchText = "test",
                    ),
                ),
            )

            assertEquals(1, searchHistoryDao.getSearchHistories(limit = 10).size)
            assertEquals("test", searchHistoryDao.getSearchHistories(limit = 10).first().searchText)

            searchHistoryDao.upsertSearchHistory(
                listOf(
                    SearchHistoryEntity(
                        searchDate = 2,
                        searchText = "test",
                    ),
                ),
            )

            assertEquals(1, searchHistoryDao.getSearchHistories(limit = 10).size)
            assertEquals(2, searchHistoryDao.getSearchHistories(limit = 10).first().searchDate)
            assertEquals("test", searchHistoryDao.getSearchHistories(limit = 10).first().searchText)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun get_search_histories_order_test() =
        testScope.runTest {
            val searchHistoryDao = database.getUserDataDao()
            searchHistoryDao.upsertSearchHistory(
                listOf(
                    SearchHistoryEntity(
                        searchDate = 2,
                        searchText = "C",
                    ),
                    SearchHistoryEntity(
                        searchDate = 3,
                        searchText = "A",
                    ),
                    SearchHistoryEntity(
                        searchDate = 1,
                        searchText = "B",
                    ),
                ),
            )

            assertEquals(3, searchHistoryDao.getSearchHistories(limit = 10).size)
            assertEquals("A", searchHistoryDao.getSearchHistories(limit = 10).first().searchText)
        }

    @Test
    @IgnoreAndroidUnitTest
    fun sort_media_by_album_test() =
        testScope.runTest {
            val dao = database.getMediaLibraryDao()
            dao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 2,
                            albumId = 2,
                            title = "title a",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        MediaEntity(
                            id = 3,
                            title = "title c",
                            albumId = 4,
                            album = "album b",
                        ),
                        MediaEntity(
                            id = 1,
                            title = "title b",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )
            dao
                .getAllMediaFlow(
                    sort =
                        MediaSorts.buildMethod {
                            add(Sort(MediaColumns.ALBUM, SortOrder.DESCENDING))
                            add(Sort(MediaColumns.CD_TRACK_NUMBER, SortOrder.DESCENDING))
                        },
                ).first()
                .also { mediaList ->
                    assertEquals(3, mediaList[0].id)
                    assertEquals(1, mediaList[1].id)
                    assertEquals(2, mediaList[2].id)
                }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun sort_media_by_title_test() =
        testScope.runTest {
            val dao = database.getMediaLibraryDao()
            dao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 2,
                            albumId = 2,
                            title = "title a",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        MediaEntity(
                            id = 3,
                            title = "title c",
                            albumId = 4,
                            album = "album b",
                        ),
                        MediaEntity(
                            id = 1,
                            title = "title b",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )
            dao
                .getAllMediaFlow(
                    sort =
                        MediaSorts.buildMethod {
                            add(Sort(MediaColumns.TITLE, SortOrder.ASCENDING))
                        },
                ).first()
                .also { mediaList ->
                    assertEquals(2, mediaList[0].id)
                    assertEquals(1, mediaList[1].id)
                    assertEquals(3, mediaList[2].id)
                }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun `is tab exist`() =
        testScope.runTest {
            val dao = database.getUserDataDao()
            dao.insertCustomTab(
                CustomTabEntity(
                    id = 10,
                    name = "name",
                    type = "bbbbb",
                    externalId = "external id",
                ),
            )
            dao.getCustomTabsFlow().first().let {
                println(it)
            }
            dao.isTabExist("external id", "name", "bbbbb").let {
                assertEquals(true, it)
            }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun `swap tab order`() =
        testScope.runTest {
            val dao = database.getUserDataDao()

            val firstId =
                dao.insertCustomTab(
                    CustomTabEntity(
                        name = "A",
                        type = "type",
                        externalId = "a1",
                        sortOrder = 1,
                    ),
                )
            val secondId =
                dao.insertCustomTab(
                    CustomTabEntity(
                        name = "B",
                        type = "type",
                        externalId = "b1",
                        sortOrder = 2,
                    ),
                )
            val thirdId =
                dao.insertCustomTab(
                    CustomTabEntity(
                        name = "C",
                        type = "type",
                        externalId = "c1",
                        sortOrder = 3,
                    ),
                )

            dao.swapTabOrder(firstId!!, firstId)
            dao.getCustomTabsFlow().first().also { tabs ->
                assertEquals(firstId, tabs[1].id)
                assertEquals(secondId, tabs[2].id)
                assertEquals(thirdId, tabs[3].id)
            }

            dao.swapTabOrder(firstId, thirdId!!)
            dao.getCustomTabsFlow().first().also { tabs ->
                assertEquals(secondId, tabs[1].id)
                assertEquals(thirdId, tabs[2].id)
                assertEquals(firstId, tabs[3].id)
            }

            dao.swapTabOrder(firstId, secondId!!)
            dao.getCustomTabsFlow().first().also { tabs ->
                assertEquals(firstId, tabs[1].id)
                assertEquals(secondId, tabs[2].id)
                assertEquals(thirdId, tabs[3].id)
            }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun `insert sort rule`() =
        runTest {
            val dao = database.getUserDataDao()
            dao.insertCustomTab(
                CustomTabEntity(
                    id = 1234,
                    name = "name",
                    type = "bbbbb",
                ),
            )
            dao.upsertSortRuleEntity(
                SortRuleEntity(
                    foreignKey = 1234,
                    primaryGroupSort = SortOptionData(1, false),
                ),
            )
            dao.getDisplaySettingFlowOfTab(1234).first().also {
                assertEquals(
                    SortOptionData(1, false),
                    it?.primaryGroupSort,
                )
            }
            dao.upsertSortRuleEntity(
                SortRuleEntity(
                    foreignKey = 1234,
                    primaryGroupSort = SortOptionData(4, true),
                ),
            )
            dao.getDisplaySettingFlowOfTab(1234).first().also {
                assertEquals(
                    SortOptionData(4, true),
                    it?.primaryGroupSort,
                )
            }

            dao.deleteCustomTab(1234)
            dao.getDisplaySettingFlowOfTab(1234).first().also {
                assertNull(it)
            }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun `get media list where album test`() =
        testScope.runTest {
            val dao = database.getMediaLibraryDao()
            dao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 2,
                            albumId = 2,
                            title = "title a",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        MediaEntity(
                            id = 3,
                            title = "title c",
                            albumId = 3,
                            album = "album b",
                        ),
                        MediaEntity(
                            id = 1,
                            title = "title b",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )
            dao
                .getAllMediaFlow(
                    where =
                        MediaWheres.buildMethod {
                            add(
                                Where(
                                    MediaColumns.ALBUM_ID,
                                    Where.Operator.EQUALS,
                                    3.toString(),
                                ),
                            )
                        },
                ).first()
                .also { mediaList ->
                    assertTrue { mediaList.map { it.id }.contains(3) }
                }
        }

    @Test
    @IgnoreAndroidUnitTest
    fun `get media list where title test`() =
        testScope.runTest {
            val dao = database.getMediaLibraryDao()
            dao.insertMedias(
                audios =
                    listOf(
                        MediaEntity(
                            id = 2,
                            albumId = 2,
                            title = "Aa",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        MediaEntity(
                            id = 3,
                            title = "aA",
                            albumId = 3,
                            album = "album b",
                        ),
                        MediaEntity(
                            id = 4,
                            title = "abijia",
                            albumId = 3,
                            album = "album b",
                        ),
                        MediaEntity(
                            id = 1,
                            title = "啊asdfasf",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )

            suspend fun mediaStartBy(first: String) =
                dao
                    .getAllMediaFlow(
                        where =
                            MediaWheres.buildMethod {
                                add(
                                    Where(
                                        MediaColumns.TITLE,
                                        Where.Operator.GLOB,
                                        "$first*",
                                    ),
                                )
                            },
                    ).first()
            mediaStartBy("a")
                .also { mediaList ->
                    assertEquals(2, mediaList.size)
                    assertTrue { mediaList.map { it.id }.contains(3) }
                    assertTrue { mediaList.map { it.id }.contains(4) }
                }
            mediaStartBy("A")
                .also { mediaList ->
                    assertEquals(1, mediaList.size)
                    assertTrue { mediaList.map { it.id }.contains(2) }
                }
            mediaStartBy("啊")
                .also { mediaList ->
                    assertEquals(1, mediaList.size)
                    assertTrue { mediaList.map { it.id }.contains(1) }
                }
        }

    @Test
    fun `get favorite play list`() =
        testScope.runTest {
            playListDao.insertPlayListEntities(
                listOf(
                    PlayListEntity(
                        id = 1,
                        createdDate = 1,
                        artworkUri = null,
                        name = "name",
                        isFavoritePlayList = true,
                        isAudioPlayList = true,
                    ),
                ),
            )
            playListDao.getFavoritePlayListFlow(isAudio = true).first().let {
                assertEquals(1, it?.id)
            }
            playListDao.getFavoritePlayListFlow(isAudio = false).first().let {
                assertEquals(null, it)
            }
        }
}

private suspend fun MediaLibraryDao.insertDummyData() {
    upsertMedia(
        audios =
            listOf(
                MediaEntity(
                    id = 1,
                    albumId = 2,
                    genreId = 3,
                    artistId = 4,
                    title = "title 1",
                ),
                MediaEntity(
                    id = 2,
                    albumId = 2,
                    genreId = 3,
                    artistId = 4,
                    title = "title 2",
                ),
            ),
        albums =
            listOf(
                AlbumEntity(
                    albumId = 1,
                    title = "title 1",
                ),
                AlbumEntity(
                    albumId = 2,
                    title = "title 2",
                ),
            ),
        genres =
            listOf(
                GenreEntity(
                    genreId = 1,
                    name = "title 1",
                ),
                GenreEntity(
                    genreId = 2,
                    name = "title 2",
                ),
            ),
        artists =
            listOf(
                ArtistEntity(
                    artistId = 1,
                    name = "title 1",
                ),
                ArtistEntity(
                    artistId = 2,
                    name = "title 2",
                ),
            ),
    )
}

private suspend fun MelodifyDataBase.verifyFtsTableSync(
    tableName: String,
    ftsTableName: String,
    matchContentName: String,
) {
    useReaderConnection {
        // Verify insert sync
        it.usePrepared("SELECT rowid, * FROM $ftsTableName") { stmt ->
            stmt.step()
            assertEquals(1, stmt.getLong(0))
            stmt.step()
            assertEquals(2, stmt.getLong(0))
        }

        it.execSQL("UPDATE $tableName SET $matchContentName = 'title changed' WHERE rowid = 1")

        // Verify update sync
        it.usePrepared("SELECT rowid, * FROM $ftsTableName") { stmt ->
            stmt.step()
            assertEquals(1, stmt.getLong(0))
            assertEquals("title changed", stmt.getText(1))
        }

        it.execSQL("DELETE FROM $tableName WHERE rowid = 1")

        // Verify delete sync
        it.usePrepared("SELECT rowid, * FROM $ftsTableName") { stmt ->
            stmt.step()
            assertEquals(2, stmt.getLong(0))
        }
    }
}
