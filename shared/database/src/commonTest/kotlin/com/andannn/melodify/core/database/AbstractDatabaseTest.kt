/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database

import androidx.room.RoomDatabase
import androidx.room.execSQL
import androidx.room.useReaderConnection
import com.andannn.melodify.core.database.CustomTabType.ALL_VIDEO
import com.andannn.melodify.core.database.dao.LyricDao
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.database.dao.PlayListDao
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.dao.internal.PlayListRawQueryDao
import com.andannn.melodify.core.database.dao.internal.SyncerDao
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.AlbumWithoutTrackCount
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.ArtistWithoutTrackCount
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.CustomTabSettingEntity
import com.andannn.melodify.core.database.entity.CustomTabSortRuleEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.LyricEntity
import com.andannn.melodify.core.database.entity.PlayListEntity
import com.andannn.melodify.core.database.entity.PlayListEntryType
import com.andannn.melodify.core.database.entity.PlayListItemEntryEntity
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.helper.paging.AllMediaPagingProvider
import com.andannn.melodify.core.database.helper.paging.MediaSorts
import com.andannn.melodify.core.database.helper.paging.MediaWheres
import com.andannn.melodify.core.database.helper.paging.PlayListEntrySort
import com.andannn.melodify.core.database.helper.paging.PlayListPagingProvider
import com.andannn.melodify.core.database.helper.paging.Sort
import com.andannn.melodify.core.database.helper.paging.SortOrder
import com.andannn.melodify.core.database.helper.paging.Where
import com.andannn.melodify.core.database.helper.sync.MediaLibrarySyncHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue

abstract class AbstractDatabaseTest {
    private lateinit var database: MelodifyDataBase
    private val lyricDao: LyricDao get() = database.getLyricDao()
    private val playListDao: PlayListDao get() = database.getPlayListDao()
    private val libraryDao: MediaLibraryDao get() = database.getMediaLibraryDao()
    private val syncerDao: SyncerDao get() = database.getSyncerDao()
    private val userDataDao: UserDataDao get() = database.getUserDataDao()
    private val playListRawQueryDao: PlayListRawQueryDao get() = database.getPlayListRawQueryDao()
    private val syncHelper: MediaLibrarySyncHelper
        get() =
            MediaLibrarySyncHelper(
                database,
                libraryDao,
                syncerDao,
            )

    private val allMediaPagingProvider get() = AllMediaPagingProvider(database.getMediaEntityRawQueryDao())
    private val dummyLyricEntities =
        listOf(
            LyricEntity(
                id = 1,
                mediaId = 1,
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
                mediaId = 2,
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

    abstract fun inMemoryDatabaseBuilder(): RoomDatabase.Builder<MelodifyDataBase>

    @BeforeTest
    fun openDatabase() {
        database = inMemoryDatabaseBuilder().setUpDatabase().build()
    }

    @AfterTest
    fun closeDatabase() {
        database?.close()
    }

    @Test
    fun get_lyric_by_media_store_id() =
        runTest {
            syncerDao.upsertMedias(
                audios = listOf(AudioEntity(id = 1, title = "dummy")),
            )
            lyricDao.insertLyricEntities(listOf(dummyLyricEntities[0]))

            val lyric = lyricDao.getLyricByMediaIdFlow("1").first()
            assertEquals(dummyLyricEntities[0], lyric)
        }

    @Test
    fun get_lyric_by_media_store_id_not_exist() =
        runTest {
            syncerDao.upsertMedias(
                audios = listOf(AudioEntity(id = 1, title = "dummy")),
            )
            lyricDao.insertLyricEntities(entities = listOf(dummyLyricEntities[0]))

            val lyric = lyricDao.getLyricByMediaIdFlow("100").first()
            assertEquals(null, lyric)
        }

    @Test
    fun lyric_deleted_cascade_when_media_deleted() =
        runTest {
            syncerDao.upsertMedias(
                audios = listOf(AudioEntity(id = 1, title = "dummy")),
            )
            lyricDao.insertLyricEntities(entities = listOf(dummyLyricEntities[0]))
            assertEquals(dummyLyricEntities[0], lyricDao.getLyricByMediaIdFlow("1").first())
            syncerDao.deleteMediasByIds(listOf(1))
            assertEquals(null, lyricDao.getLyricByMediaIdFlow("1").first())
        }

    @Test
    fun insert_play_list() =
        runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
                        ),
                    ),
            )

            val playLists = playListDao.getAllPlayListFlow().first()
            assertEquals(1, playLists.size)
        }

    @Test
    fun get_play_list_order_by_created_date() =
        runTest {
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            createdDate = 1,
                            artworkUri = null,
                            name = "name1",
                        ),
                        PlayListEntity(
                            createdDate = 3,
                            artworkUri = null,
                            name = "name3",
                        ),
                        PlayListEntity(
                            createdDate = 2,
                            artworkUri = null,
                            name = "name2",
                        ),
                    ),
            )
            playListDao
                .getAllPlayListFlow()
                .first()
                .also {
                    assertEquals(3, it.size)
                }.iterator()
                .also {
                    assertEquals("name3", it.next().playListEntity.name)
                    assertEquals("name2", it.next().playListEntity.name)
                    assertEquals("name1", it.next().playListEntity.name)
                }
        }

    @Test
    fun get_play_list_with_media_count() =
        runTest {
            val playListId =
                playListDao
                    .insertPlayListEntities(
                        entities =
                            listOf(
                                PlayListEntity(
                                    createdDate = 1,
                                    artworkUri = null,
                                    name = "name",
                                ),
                            ),
                    ).first()
            val mediaId =
                syncerDao.upsertMedias(listOf(AudioEntity(id = 1, title = "dummy"))).first()
            val mediaId2 =
                syncerDao.upsertMedias(listOf(AudioEntity(id = 2, title = "dummy2"))).first()

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            audioId = mediaId,
                            entryType = PlayListEntryType.AUDIO,
                            addedDate = 1,
                        ),
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            audioId = mediaId2,
                            entryType = PlayListEntryType.AUDIO,
                            addedDate = 2,
                        ),
                    ),
            )

            val playLists = playListDao.getAllPlayListFlow().first()
            assertEquals(2, playLists.first().mediaCount)
        }

    @Test
    fun check_is_audio_in_play_list() =
        runTest {
            val playListId =
                playListDao
                    .insertPlayListEntities(
                        listOf(
                            PlayListEntity(
                                name = "",
                                createdDate = 1L,
                                artworkUri = null,
                            ),
                        ),
                    ).first()
            val mediaId = syncerDao.upsertMedias(listOf(AudioEntity(title = "dummy"))).first()
            assertEquals(
                false,
                playListDao
                    .getIsMediaInPlayListFlow(
                        playListId,
                        entryType = PlayListEntryType.AUDIO,
                        mediaId,
                    ).first(),
            )

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            audioId = mediaId,
                            entryType = PlayListEntryType.AUDIO,
                            addedDate = 1L,
                        ),
                    ),
            )
            assertEquals(
                true,
                playListDao
                    .getIsMediaInPlayListFlow(
                        playListId,
                        entryType = PlayListEntryType.AUDIO,
                        mediaId,
                    ).first(),
            )
        }

    @Test
    fun check_is_video_in_play_list() =
        runTest {
            val playListId =
                playListDao
                    .insertPlayListEntities(
                        listOf(
                            PlayListEntity(
                                name = "",
                                createdDate = 1L,
                                artworkUri = null,
                            ),
                        ),
                    ).first()
            val mediaId = syncerDao.upsertVideos(listOf(VideoEntity(title = "dummy"))).first()
            assertEquals(
                false,
                playListDao
                    .getIsMediaInPlayListFlow(
                        playListId,
                        entryType = PlayListEntryType.VIDEO,
                        mediaId,
                    ).first(),
            )

            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            videoId = mediaId,
                            entryType = PlayListEntryType.AUDIO,
                            addedDate = 1L,
                        ),
                    ),
            )
            assertEquals(
                true,
                playListDao
                    .getIsMediaInPlayListFlow(
                        playListId,
                        entryType = PlayListEntryType.VIDEO,
                        mediaId,
                    ).first(),
            )
        }

    @Test
    fun `can only set one of audio_id and video_id `() =
        runTest {
            assertFails("Only one of audioId or videoId can be set") {
                PlayListItemEntryEntity(
                    playListId = 1,
                    audioId = 1,
                    videoId = 1,
                    entryType = PlayListEntryType.AUDIO,
                    addedDate = 1,
                )
            }
            assertFails("Either audioId or videoId must be set") {
                PlayListItemEntryEntity(
                    playListId = 1,
                    entryType = PlayListEntryType.AUDIO,
                    addedDate = 1,
                )
            }
        }

    @Test
    fun delete_media_from_play_list() =
        runTest {
            val playListId =
                playListDao
                    .insertPlayListEntities(
                        entities =
                            listOf(
                                PlayListEntity(
                                    id = 1,
                                    createdDate = 1,
                                    artworkUri = null,
                                    name = "name",
                                ),
                            ),
                    ).first()
            val mediaId = syncerDao.upsertMedias(listOf(AudioEntity(title = "dummy"))).first()
            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            audioId = mediaId,
                            entryType = PlayListEntryType.AUDIO,
                            addedDate = 1,
                        ),
                    ),
            )

            assertEquals(
                1,
                playListDao
                    .getPlayListFlowById(playListId)
                    .first()!!
                    .mediaCount,
            )

            playListDao.deleteMediaFromPlayList(
                playListId,
                mediaId,
                entryType = PlayListEntryType.AUDIO,
            )
            assertEquals(
                0,
                playListDao
                    .getPlayListFlowById(playListId)
                    .first()!!
                    .mediaCount,
            )
        }

    @Test
    fun delete_video_from_play_list() =
        runTest {
            val playListId =
                playListDao
                    .insertPlayListEntities(
                        entities =
                            listOf(
                                PlayListEntity(
                                    id = 1,
                                    createdDate = 1,
                                    artworkUri = null,
                                    name = "name",
                                ),
                            ),
                    ).first()
            val mediaId = syncerDao.upsertVideos(listOf(VideoEntity(title = "dummy"))).first()
            playListDao.insertPlayListWithMediaCrossRef(
                crossRefs =
                    listOf(
                        PlayListItemEntryEntity(
                            playListId = playListId,
                            videoId = mediaId,
                            entryType = PlayListEntryType.VIDEO,
                            addedDate = 1,
                        ),
                    ),
            )

            assertEquals(
                1,
                playListDao
                    .getPlayListFlowById(playListId)
                    .first()!!
                    .mediaCount,
            )

            playListDao.deleteMediaFromPlayList(
                playListId,
                mediaId,
                entryType = PlayListEntryType.VIDEO,
            )
            assertEquals(
                0,
                playListDao
                    .getPlayListFlowById(playListId)
                    .first()!!
                    .mediaCount,
            )
        }

    @Test
    fun insert_play_lists_test() =
        runTest {
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
    fun delete_playlist_by_id_test() =
        runTest {
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
            playListDao.insertPlayListEntities(
                entities =
                    listOf(
                        PlayListEntity(
                            id = 2,
                            createdDate = 1,
                            artworkUri = null,
                            name = "name",
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
                        ),
                    ),
            )
            playListDao.deletePlayListById(2)
            val playLists = playListDao.getAllPlayListFlow().first()
            assertEquals(2, playLists.size)
        }

    @Test
    fun delete_test() =
        runTest {
            syncerDao.upsertAlbumsWithoutTrackCount(
                albums =
                    listOf(
                        AlbumWithoutTrackCount(
                            albumId = 2,
                            title = "album 2",
                        ),
                    ),
            )
            syncerDao.upsertGenres(
                genres =
                    listOf(
                        GenreEntity(
                            genreId = 3,
                            name = "genre 3",
                        ),
                    ),
            )
            syncerDao.upsertArtistWithoutTrackCount(
                artists =
                    listOf(
                        ArtistWithoutTrackCount(
                            artistId = 4,
                            name = "artist 4",
                        ),
                    ),
            )
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 1,
                            albumId = 2,
                            genreId = 3,
                            artistId = 4,
                            title = "title 1",
                        ),
                    ),
            )

            assertEquals(
                1,
                libraryDao.getAllAlbumFlow().first().size,
            )
            assertEquals(
                listOf(2L),
                libraryDao.getAllAlbumID(),
            )
            assertEquals(
                listOf(3L),
                syncerDao.getAllGenreID(),
            )
            assertEquals(
                listOf(4L),
                syncerDao.getAllArtistID(),
            )
            assertEquals(
                listOf(1L),
                syncerDao.getAllMediaID(),
            )
            syncerDao.deleteMediasByIds(listOf(1L))
            libraryDao.getAllAlbumFlow().first().also { items ->
                assertEquals(1, items.size)
                assertEquals(0, items.first().trackCount)
            }
            libraryDao.getAllGenreFlow().first().also { items ->
                assertEquals(1, items.size)
            }
            libraryDao.getAllArtistFlow().first().also { items ->
                assertEquals(1, items.size)
                assertEquals(0, items.first().trackCount)
            }
        }

    @Test
    fun update_artist_count_test() =
        runTest {
            syncerDao.upsertArtistWithoutTrackCount(
                artists =
                    listOf(
                        ArtistWithoutTrackCount(
                            artistId = 4,
                            name = "artist 4",
                        ),
                    ),
            )
            assertEquals(0, libraryDao.getArtistByArtistId(4)?.trackCount)
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 1,
                            albumId = 2,
                            genreId = 3,
                            artistId = 4,
                            title = "title 1",
                        ),
                    ),
            )
            assertEquals(1, libraryDao.getArtistByArtistId(4)?.trackCount)
            syncerDao.deleteMediasByIds(listOf(1L))
            assertEquals(0, libraryDao.getArtistByArtistId(4)?.trackCount)
        }

    @Test
    fun update_album_count_test() =
        runTest {
            syncerDao.upsertAlbumsWithoutTrackCount(
                albums =
                    listOf(
                        AlbumWithoutTrackCount(
                            albumId = 2,
                            title = "album 2",
                        ),
                    ),
            )
            assertEquals(0, libraryDao.getAlbumByAlbumId(2)?.trackCount)
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 1,
                            albumId = 2,
                            genreId = 3,
                            artistId = 4,
                            title = "title 1",
                        ),
                    ),
            )
            assertEquals(1, libraryDao.getAlbumByAlbumId(2)?.trackCount)
            syncerDao.deleteMediasByIds(listOf(1L))
            assertEquals(0, libraryDao.getAlbumByAlbumId(2)?.trackCount)
        }

    @Test
    fun fts_table_sync_test() =
        runTest {
            syncHelper.insertDummyData()
            database.verifyFtsTableSync(
                tableName = "library_album_table",
                ftsTableName = "library_fts_album_table",
                matchContentName = "album_title",
            )
            database.verifyFtsTableSync(
                tableName = "library_artist_table",
                ftsTableName = "library_fts_artist_table",
                matchContentName = "artist_name",
            )
            database.verifyFtsTableSync(
                tableName = "library_media_table",
                ftsTableName = "library_fts_media_table",
                matchContentName = "media_title",
            )
        }

    @Test
    fun match_keyword_test() =
        runTest {
            syncHelper.insertDummyData()
            assertEquals(1, libraryDao.searchAlbum("title 1").size)
            assertEquals(1, libraryDao.searchAlbum("title 1").first().albumId)

            assertEquals(1, libraryDao.searchArtist("title 1").size)
            assertEquals(1, libraryDao.searchArtist("title 1").first().artistId)

            assertEquals(1, libraryDao.searchMedia("title 1").size)
            assertEquals(1, libraryDao.searchMedia("title 1").first().id)
        }

    @Test
    fun upsert_search_history_test() =
        runTest {
            val searchHistoryDao = userDataDao
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
    fun get_search_histories_order_test() =
        runTest {
            val searchHistoryDao = userDataDao
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
    fun sort_media_by_album_test() =
        runTest {
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 2,
                            albumId = 2,
                            title = "title a",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        AudioEntity(
                            id = 3,
                            title = "title c",
                            albumId = 4,
                            album = "album b",
                        ),
                        AudioEntity(
                            id = 1,
                            title = "title b",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )
            allMediaPagingProvider
                .getDataFlow(
                    sort =
                        MediaSorts.buildMethod {
                            add(Sort("media_album", SortOrder.DESCENDING))
                            add(Sort("media_cd_track_number", SortOrder.DESCENDING))
                        },
                ).first()
                .also { mediaList ->
                    assertEquals(3, mediaList[0].id)
                    assertEquals(1, mediaList[1].id)
                    assertEquals(2, mediaList[2].id)
                }
        }

    @Test
    fun sort_media_by_title_test() =
        runTest {
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 2,
                            albumId = 2,
                            title = "title a",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        AudioEntity(
                            id = 3,
                            title = "title c",
                            albumId = 4,
                            album = "album b",
                        ),
                        AudioEntity(
                            id = 1,
                            title = "title b",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )
            allMediaPagingProvider
                .getDataFlow(
                    sort =
                        MediaSorts.buildMethod {
                            add(Sort("media_title", SortOrder.ASCENDING))
                        },
                ).first()
                .also { mediaList ->
                    assertEquals(2, mediaList[0].id)
                    assertEquals(1, mediaList[1].id)
                    assertEquals(3, mediaList[2].id)
                }
        }

    @Test
    fun `is tab exist`() =
        runTest {
            val dao = userDataDao
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
    fun `swap tab order`() =
        runTest {
            val dao = userDataDao

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
    fun `insert sort rule`() =
        runTest {
            val dao = userDataDao
            dao.insertCustomTab(
                CustomTabEntity(
                    id = 1234,
                    name = "name",
                    type = "bbbbb",
                ),
            )
            dao.upsertSortRuleEntity(
                CustomTabSortRuleEntity(
                    foreignKey = 1234,
                    primaryGroupSort = SortOptionData(1, false),
                ),
            )
            dao.getSortRuleFlowOfTab(1234).first().also {
                assertEquals(
                    SortOptionData(1, false),
                    it?.primaryGroupSort,
                )
            }
            dao.upsertSortRuleEntity(
                CustomTabSortRuleEntity(
                    foreignKey = 1234,
                    primaryGroupSort = SortOptionData(4, true),
                ),
            )
            dao.getSortRuleFlowOfTab(1234).first().also {
                assertEquals(
                    SortOptionData(4, true),
                    it?.primaryGroupSort,
                )
            }

            dao.deleteCustomTab(1234)
            dao.getSortRuleFlowOfTab(1234).first().also {
                assertNull(it)
            }
        }

    @Test
    fun `get media list where album test`() =
        runTest {
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 2,
                            albumId = 2,
                            title = "title a",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        AudioEntity(
                            id = 3,
                            title = "title c",
                            albumId = 3,
                            album = "album b",
                        ),
                        AudioEntity(
                            id = 1,
                            title = "title b",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )
            allMediaPagingProvider
                .getDataFlow(
                    where =
                        MediaWheres.buildMethod {
                            add(
                                Where(
                                    "media_album_id",
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
    fun `get media list where title test`() =
        runTest {
            syncerDao.upsertMedias(
                audios =
                    listOf(
                        AudioEntity(
                            id = 2,
                            albumId = 2,
                            title = "Aa",
                            album = "album a",
                            cdTrackNumber = 1,
                        ),
                        AudioEntity(
                            id = 3,
                            title = "aA",
                            albumId = 3,
                            album = "album b",
                        ),
                        AudioEntity(
                            id = 4,
                            title = "abijia",
                            albumId = 3,
                            album = "album b",
                        ),
                        AudioEntity(
                            id = 1,
                            title = "啊asdfasf",
                            albumId = 2,
                            album = "album a",
                            cdTrackNumber = 2,
                        ),
                    ),
            )

            suspend fun mediaStartBy(first: String) =
                allMediaPagingProvider
                    .getDataFlow(
                        where =
                            MediaWheres.buildMethod {
                                add(
                                    Where(
                                        "media_title",
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
        runTest {
            val playListId =
                playListDao
                    .insertPlayListEntities(
                        listOf(
                            PlayListEntity(
                                id = 1,
                                createdDate = 1,
                                artworkUri = null,
                                name = "name",
                                isFavoritePlayList = true,
                            ),
                        ),
                    ).first()
            playListDao.getFavoritePlayListFlow().first().let {
                assertEquals(playListId, it?.id)
            }
        }

    @Test
    fun `update video progress failed when video not exist`() =
        runTest {
            assertFails {
                userDataDao.savePlayProgress(
                    videoId = 1L,
                    100L,
                )
            }
        }

    @Test
    fun `update video progress`() =
        runTest {
            syncerDao.upsertVideos(
                listOf(VideoEntity(id = 1, title = "title", duration = 1000)),
            )
            userDataDao.savePlayProgress(videoId = 1L, 100L)
            userDataDao.getPlayProgressFlow(1L).first().also {
                assertEquals(100, it?.progressMs)
            }
            userDataDao.savePlayProgress(videoId = 1L, 101L)
            userDataDao.getPlayProgressFlow(1L).first().also {
                assertEquals(101, it?.progressMs)
            }
        }

    @Test
    fun `can not mark video as watched when progress record not exist`() =
        runTest {
            syncerDao.upsertVideos(
                listOf(VideoEntity(id = 1, title = "title", duration = 1000)),
            )
            assertEquals(0, userDataDao.markVideoAsWatched(1L))
        }

    @Test
    fun `mark video as watched success`() =
        runTest {
            syncerDao.upsertVideos(
                listOf(VideoEntity(id = 1, title = "title", duration = 1000)),
            )
            userDataDao.savePlayProgress(videoId = 1L, 100L)
            assertEquals(1, userDataDao.markVideoAsWatched(1L))
        }

    @Test
    fun `set is show video progress setting failed when no video tab setting`() =
        runTest {
            assertFails {
                userDataDao.upsertTabSettingEntity(
                    CustomTabSettingEntity(
                        customTabId = 10,
                        isShowVideoProgress = true,
                    ),
                )
            }
        }

    @Test
    fun `progress record is deleted when video deleted`() =
        runTest {
            syncerDao.upsertVideos(
                listOf(VideoEntity(id = 1, title = "title", duration = 1000)),
            )
            userDataDao.savePlayProgress(videoId = 1L, 100L)
            userDataDao.getPlayProgressFlow(1L).first().also {
                assertEquals(100L, it?.progressMs)
            }
            syncerDao.deleteVideoByIds(listOf(1))
            userDataDao.getPlayProgressFlow(1L).first().also {
                assertEquals(null, it)
            }
        }

    @Test
    fun `progress record will not be deleted when video updated`() =
        runTest {
            syncerDao.upsertVideos(
                listOf(VideoEntity(id = 1, title = "title", duration = 1000)),
            )
            userDataDao.savePlayProgress(videoId = 1L, 100L)
            userDataDao.getPlayProgressFlow(1L).first().also {
                assertEquals(100L, it?.progressMs)
            }
            syncerDao.upsertVideos(
                listOf(VideoEntity(id = 1, title = "title", duration = 1000)),
            )
            userDataDao.getPlayProgressFlow(1L).first().also {
                assertEquals(100L, it?.progressMs)
            }
        }

    @Test
    fun `set is show video progress setting success`() =
        runTest {
            userDataDao
                .insertCustomTab(CustomTabEntity(id = 10, name = "name", type = ALL_VIDEO))
            val settingId =
                userDataDao.upsertTabSettingEntity(
                    CustomTabSettingEntity(
                        customTabId = 10,
                        isShowVideoProgress = true,
                    ),
                )
            userDataDao.getCustomTabSettingFlow(10).first().also {
                assertEquals(true, it?.isShowVideoProgress)
            }
            userDataDao.upsertTabSettingEntity(
                CustomTabSettingEntity(
                    id = settingId,
                    customTabId = 10,
                    isShowVideoProgress = false,
                ),
            )
            userDataDao.getCustomTabSettingFlow(10).first().also {
                assertEquals(false, it?.isShowVideoProgress)
            }
        }

    @Test
    fun `VideoTabSettingEntity is deleted when tab is deleted`() =
        runTest {
            database
                .getUserDataDao()
                .insertCustomTab(CustomTabEntity(id = 10, name = "name", type = ALL_VIDEO))
            userDataDao.upsertTabSettingEntity(
                CustomTabSettingEntity(
                    customTabId = 10,
                    isShowVideoProgress = true,
                ),
            )
            userDataDao.deleteCustomTab(10)
            userDataDao.getCustomTabSettingFlow(10).first().also {
                assertEquals(null, it)
            }
        }

    @Test
    fun `sync media library test`() =
        runTest {
            syncHelper.insertDummyData()
            repeat(2) {
                syncHelper.syncMediaLibrary(
                    audios =
                        listOf(
                            AudioEntity(
                                id = 100,
                                albumId = 400,
                                genreId = 500,
                                artistId = 600,
                            ),
                        ),
                    videos = listOf(VideoEntity(id = 300)),
                    albums = listOf(AlbumEntity(albumId = 400, title = "new_album")),
                    genres = listOf(GenreEntity(genreId = 500)),
                    artists = listOf(ArtistEntity(artistId = 600, name = "new_artist")),
                )
                assertEquals(1, syncerDao.getAllArtistID().size)
                assertEquals(1, libraryDao.getArtistByArtistId(600)?.trackCount)
                assertEquals(1, syncerDao.getAllGenreID().size)
                assertEquals(1, libraryDao.getAllAlbumID().size)
                assertEquals(1, libraryDao.getAlbumByAlbumId(400)?.trackCount)
                assertEquals(1, syncerDao.getAllMediaID().size)
                assertEquals(1, syncerDao.getAllVideoID().size)
                assertEquals(100, syncerDao.getAllMediaID().first())
                assertEquals(400, libraryDao.getAllAlbumID().first())
                assertEquals(300, syncerDao.getAllVideoID().first())
                assertEquals(500, syncerDao.getAllGenreID().first())
                assertEquals(600, syncerDao.getAllArtistID().first())
            }
        }

    @Test
    fun `sync media library insert delete CallBack event test`() =
        runTest {
            syncHelper.syncMediaLibrary(
                audios = listOf(AudioEntity(id = 100, title = "Test")),
                onInsert = { type, names ->
                    assertEquals("Test", names[0])
                },
            )
            syncHelper.syncMediaLibrary(
                onDelete = { type, names ->
                    assertEquals("Test", names[0])
                },
            )
        }

    @Test
    fun `sync artist library insert delete CallBack event test`() =
        runTest {
            syncHelper.syncMediaLibrary(
                artists = listOf(ArtistEntity(artistId = 100, name = "Test")),
                onInsert = { type, names ->
                    assertEquals("Test", names[0])
                },
            )
            syncHelper.syncMediaLibrary(
                onDelete = { type, names ->
                    assertEquals("Test", names[0])
                },
            )
        }

    @Test
    fun `sync album library insert delete CallBack event test`() =
        runTest {
            syncHelper.syncMediaLibrary(
                albums = listOf(AlbumEntity(albumId = 100, title = "Test")),
                onInsert = { type, names ->
                    assertEquals("Test", names[0])
                },
            )
            syncHelper.syncMediaLibrary(
                onDelete = { type, names ->
                    assertEquals("Test", names[0])
                },
            )
        }

    @Test
    fun `delete orphan album test`() =
        runTest {
            val dao = database.getMediaLibraryDao()
            syncHelper.insertDummyData()

            assertEquals(2, dao.getAllAlbumID().size)
            dao.deleteOrphanAlbums()
            assertEquals(1, dao.getAllAlbumID().size)
            syncerDao.deleteMediasByIds(listOf(1, 2))
            dao.deleteOrphanAlbums()
            assertEquals(0, dao.getAllAlbumID().size)
        }

    @Test
    fun `delete orphan artist test`() =
        runTest {
            val dao = database.getMediaLibraryDao()
            syncHelper.insertDummyData()

            assertEquals(2, syncerDao.getAllArtistID().size)
            dao.deleteOrphanArtists()
            assertEquals(0, syncerDao.getAllArtistID().size)
        }

    @Test
    fun `delete orphan genre test`() =
        runTest {
            val dao = database.getMediaLibraryDao()
            syncHelper.insertDummyData()

            assertEquals(2, syncerDao.getAllGenreID().size)
            dao.deleteOrphanGenres()
            assertEquals(0, syncerDao.getAllGenreID().size)
        }

    @Test
    fun `insert and update test`() =
        runTest {
            syncerDao
                .upsertArtistWithoutTrackCount(listOf(ArtistWithoutTrackCount(10, "test")))
                .also {
                    assertEquals(1, it.size)
                    assertEquals(10, it.first())
                }
            syncerDao
                .upsertArtistWithoutTrackCount(listOf(ArtistWithoutTrackCount(10, "test")))
                .also {
                    assertEquals(1, it.size)
                    assertEquals(-1, it.first())
                }
        }

    @Test
    fun `search library content test`() =
        runTest {
            syncHelper.insertDummyData()
            libraryDao.searchContentByKeyword("title").also {
                assertEquals(2, it.count { it.contentType == MediaType.VIDEO })
                assertEquals(2, it.count { it.contentType == MediaType.ARTIST })
                assertEquals(2, it.count { it.contentType == MediaType.ARTIST })
                assertEquals(2, it.count { it.contentType == MediaType.MEDIA })
            }
        }

    private suspend fun insertDummyPlayListInfo(): Long {
        syncHelper.insertDummyData()
        val playListId =
            playListDao
                .insertPlayListEntities(
                    entities =
                        listOf(
                            PlayListEntity(
                                id = 1,
                                name = "playlist 1",
                                createdDate = 1,
                                artworkUri = null,
                            ),
                        ),
                ).first()
        playListDao.insertPlayListWithMediaCrossRef(
            crossRefs =
                listOf(
                    PlayListItemEntryEntity(
                        playListId = playListId,
                        videoId = 5,
                        addedDate = 3,
                    ),
                    PlayListItemEntryEntity(
                        playListId = playListId,
                        videoId = 6,
                        addedDate = 4,
                    ),
                    PlayListItemEntryEntity(
                        playListId = playListId,
                        audioId = 1,
                        addedDate = 2,
                    ),
                    PlayListItemEntryEntity(
                        playListId = playListId,
                        audioId = 2,
                        addedDate = 1,
                    ),
                ),
        )
        return playListId
    }

    @Test
    fun `play list paging content provider test`() =
        runTest {
            val playListId = insertDummyPlayListInfo()
            PlayListPagingProvider(playListId, playListRawQueryDao)
                .getDataFlow()
                .first()
                .also {
                    assertEquals(4, it.size)
                }
        }

    @Test
    fun `play list paging content order by create date`() =
        runTest {
            val playListId = insertDummyPlayListInfo()
            PlayListPagingProvider(playListId, playListRawQueryDao)
                .getDataFlow(
                    sort =
                        MediaSorts.buildMethod {
                            add(PlayListEntrySort.buildCreateDateSort(ascending = false))
                        },
                ).first()
                .iterator()
                .also {
                    assertEquals(6, it.next().video_id)
                    assertEquals(5, it.next().video_id)
                    assertEquals(1, it.next().audio_id)
                    assertEquals(2, it.next().audio_id)
                }
        }
}

private suspend fun MediaLibrarySyncHelper.insertDummyData() {
    upsertMedia(
        videos =
            listOf(
                VideoEntity(
                    id = 5,
                    title = "title 1",
                ),
                VideoEntity(
                    id = 6,
                    title = "title 2",
                ),
            ),
        audios =
            listOf(
                AudioEntity(
                    id = 1,
                    albumId = 2,
                    genreId = 3,
                    artistId = 4,
                    title = "title 1",
                ),
                AudioEntity(
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
