/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import com.andannn.melodify.core.database.MediaSorts
import com.andannn.melodify.core.database.MediaWheres
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.Where
import com.andannn.melodify.core.database.appendOrCreateWith
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.GenreColumns
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity
import com.andannn.melodify.core.database.entity.VideoColumns
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.toSortString
import com.andannn.melodify.core.database.toWhereString
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_CHUNK_SIZE = 500

object MediaType {
    const val MEDIA = 0
    const val ALBUM = 1
    const val ARTIST = 2
    const val GENRE = 3
    const val VIDEO = 4
}

@Dao
interface MediaLibraryDao {
    @Upsert
    suspend fun upsertAlbums(albums: List<AlbumEntity>)

    @Query("UPDATE ${Tables.LIBRARY_MEDIA} SET ${MediaColumns.DELETED} = 1 WHERE ${MediaColumns.ID} IN (:ids)")
    suspend fun markMediaAsDeleted(ids: List<String>)

    @Query("UPDATE ${Tables.LIBRARY_VIDEO} SET ${VideoColumns.DELETED} = 1 WHERE ${VideoColumns.ID} IN (:ids)")
    suspend fun markVideoAsDeleted(ids: List<String>)

    @Upsert
    suspend fun upsertArtists(artists: List<ArtistEntity>)

    @Upsert
    suspend fun upsertGenres(genres: List<GenreEntity>)

    @Upsert
    suspend fun upsertMedias(audios: List<MediaEntity>)

    @Upsert
    suspend fun upsertVideos(audios: List<VideoEntity>)

    @Query("DELETE FROM ${Tables.LIBRARY_ALBUM} WHERE ${AlbumColumns.ID} IN (:ids)")
    suspend fun deleteAlbumsByIds(ids: List<Long>)

    @Query(
        """
        DELETE FROM ${Tables.LIBRARY_ALBUM}
        WHERE ${AlbumColumns.ID} NOT IN (
            SELECT DISTINCT ${MediaColumns.ALBUM_ID} 
            FROM ${Tables.LIBRARY_MEDIA} 
            WHERE ${MediaColumns.ALBUM_ID} IS NOT NULL
        )
    """,
    )
    suspend fun deleteOrphanAlbums()

    @Query(
        """
        DELETE FROM ${Tables.LIBRARY_ARTIST}
        WHERE ${ArtistColumns.ID} NOT IN (
            SELECT DISTINCT ${MediaColumns.ARTIST_ID} 
            FROM ${Tables.LIBRARY_MEDIA} 
            WHERE ${MediaColumns.ARTIST_ID} IS NOT NULL
        )
    """,
    )
    suspend fun deleteOrphanArtists()

    @Query(
        """
        DELETE FROM ${Tables.LIBRARY_GENRE}
        WHERE ${GenreColumns.ID} NOT IN (
            SELECT DISTINCT ${MediaColumns.GENRE_ID} 
            FROM ${Tables.LIBRARY_MEDIA} 
            WHERE ${MediaColumns.GENRE_ID} IS NOT NULL
        )
    """,
    )
    suspend fun deleteOrphanGenres()

    @Query("DELETE FROM ${Tables.LIBRARY_ARTIST} WHERE ${ArtistColumns.ID} IN (:ids)")
    suspend fun deleteArtistsByIds(ids: List<Long>)

    @Query("DELETE FROM ${Tables.LIBRARY_GENRE} WHERE ${GenreColumns.ID} IN (:ids)")
    suspend fun deleteGenreByIds(ids: List<Long>)

    @Query("DELETE FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ID} IN (:ids)")
    suspend fun deleteMediasByIds(ids: List<Long>)

    @Query("DELETE FROM ${Tables.LIBRARY_VIDEO} WHERE ${VideoColumns.ID} IN (:ids)")
    suspend fun deleteVideoByIds(ids: List<Long>)

    @Query("SELECT ${AlbumColumns.ID} FROM ${Tables.LIBRARY_ALBUM}")
    suspend fun getAllAlbumID(): List<Long>

    @Query("SELECT * FROM ${Tables.LIBRARY_ALBUM}")
    fun getAllAlbumFlow(): Flow<List<AlbumEntity>>

    @Query("SELECT ${GenreColumns.ID} FROM ${Tables.LIBRARY_GENRE}")
    suspend fun getAllGenreID(): List<Long>

    @Query("SELECT * FROM ${Tables.LIBRARY_GENRE}")
    fun getAllGenreFlow(): Flow<List<GenreEntity>>

    @Query("SELECT ${ArtistColumns.ID} FROM ${Tables.LIBRARY_ARTIST}")
    suspend fun getAllArtistID(): List<Long>

    @Query("SELECT * FROM ${Tables.LIBRARY_ARTIST}")
    fun getAllArtistFlow(): Flow<List<ArtistEntity>>

    @Query("SELECT ${MediaColumns.ID} FROM ${Tables.LIBRARY_MEDIA}")
    suspend fun getAllMediaID(): List<Long>

    @Query("SELECT ${VideoColumns.ID} FROM ${Tables.LIBRARY_VIDEO}")
    suspend fun getAllVideoID(): List<Long>

    @RawQuery(observedEntities = [MediaEntity::class])
    fun getMediaFlowRaw(rawQuery: RoomRawQuery): Flow<List<MediaEntity>>

    @RawQuery(observedEntities = [MediaEntity::class])
    fun getMediaFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, MediaEntity>

    @RawQuery(observedEntities = [VideoEntity::class])
    fun getVideoFlowPagingSource(rawQuery: RoomRawQuery): PagingSource<Int, VideoEntity>

    @RawQuery(observedEntities = [VideoEntity::class])
    fun getVideoFlowRaw(rawQuery: RoomRawQuery): Flow<List<VideoEntity>>

    fun getAllMediaFlow(
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): Flow<List<MediaEntity>> =
        getMediaFlowRaw(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getAllVideoPagingSource(
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): PagingSource<Int, VideoEntity> =
        getVideoFlowPagingSource(
            buildVideoRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            videoNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getAllVideoFlow(
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): Flow<List<VideoEntity>> =
        getVideoFlowRaw(
            buildVideoRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            videoNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getVideoBucketFlow(
        bucketId: String,
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): Flow<List<VideoEntity>> =
        getVideoFlowRaw(
            buildVideoRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            bucketIdWhere(bucketId),
                            videoNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getVideoBucketPagingSource(
        bucketId: String,
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): PagingSource<Int, VideoEntity> =
        getVideoFlowPagingSource(
            buildVideoRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            bucketIdWhere(bucketId),
                            videoNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getAllMediaPagingSource(
        where: MediaWheres? = null,
        sort: MediaSorts? = null,
    ): PagingSource<Int, MediaEntity> =
        getMediaFlowPagingSource(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    private fun buildMediaRawQuery(
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): RoomRawQuery {
        val sql =
            "SELECT * FROM ${Tables.LIBRARY_MEDIA} ${wheres.toWhereString()} ${sort.toSortString()}"
        return RoomRawQuery(sql)
    }

    private fun buildVideoRawQuery(
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): RoomRawQuery {
        val sql =
            "SELECT * FROM ${Tables.LIBRARY_VIDEO} ${wheres.toWhereString()} ${sort.toSortString()}"
        return RoomRawQuery(sql)
    }

    fun getMediasByAlbumIdFlow(
        albumId: String,
        where: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<MediaEntity>> =
        getMediaFlowRaw(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            albumIdWhere(albumId),
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getMediasPagingSourceByAlbumId(
        albumId: String,
        where: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, MediaEntity> =
        getMediaFlowPagingSource(
            buildMediaRawQuery(
                wheres =
                    where.appendOrCreateWith {
                        listOf(
                            albumIdWhere(albumId),
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getMediasByArtistIdFlow(
        artistId: String,
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<MediaEntity>> =
        getMediaFlowRaw(
            buildMediaRawQuery(
                wheres =
                    wheres.appendOrCreateWith {
                        listOf(
                            artistIdWhere(artistId),
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getMediasPagingSourceByArtistId(
        artistId: String,
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, MediaEntity> =
        getMediaFlowPagingSource(
            buildMediaRawQuery(
                wheres =
                    wheres.appendOrCreateWith {
                        listOf(
                            artistIdWhere(artistId),
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getMediasByGenreIdFlow(
        genreId: String,
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): Flow<List<MediaEntity>> =
        getMediaFlowRaw(
            buildMediaRawQuery(
                wheres =
                    wheres.appendOrCreateWith {
                        listOf(
                            genreIdWhere(genreId),
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    fun getMediasPagingSourceByGenreId(
        genreId: String,
        wheres: MediaWheres?,
        sort: MediaSorts?,
    ): PagingSource<Int, MediaEntity> =
        getMediaFlowPagingSource(
            buildMediaRawQuery(
                wheres =
                    wheres.appendOrCreateWith {
                        listOf(
                            genreIdWhere(genreId),
                            audioNotDeletedWhere(),
                        )
                    },
                sort = sort,
            ),
        )

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.GENRE_ID} = :genreId")
    fun getMediasByGenreIdFlow(genreId: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM ${Tables.LIBRARY_ALBUM} WHERE ${AlbumColumns.ID} = :albumId")
    fun getAlbumByAlbumIdFlow(albumId: String): Flow<AlbumEntity?>

    @Query("SELECT * FROM ${Tables.LIBRARY_ALBUM} WHERE ${AlbumColumns.ID} = :albumId")
    suspend fun getAlbumByAlbumId(albumId: String): AlbumEntity?

    @Query("SELECT * FROM ${Tables.LIBRARY_ARTIST} WHERE ${ArtistColumns.ID} = :artistId")
    fun getArtistByArtistIdFlow(artistId: String): Flow<ArtistEntity?>

    @Query("SELECT * FROM ${Tables.LIBRARY_ARTIST} WHERE ${ArtistColumns.ID} = :artistId")
    suspend fun getArtistByArtistId(artistId: String): ArtistEntity?

    @Query("SELECT * FROM ${Tables.LIBRARY_GENRE} WHERE ${GenreColumns.ID} = :genreId")
    fun getGenreByGenreIdFlow(genreId: String): Flow<GenreEntity?>

    @Query("SELECT * FROM ${Tables.LIBRARY_GENRE} WHERE ${GenreColumns.ID} = :genreId")
    suspend fun getGenreByGenreId(genreId: String): GenreEntity?

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ID} IN (:mediaIds)")
    suspend fun getMediaByMediaIds(mediaIds: List<String>): List<MediaEntity>

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.SOURCE_URI} IN (:sources)")
    suspend fun getMediaByMediaSourceUrl(sources: List<String>): List<MediaEntity>

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ID} IN (:mediaIds)")
    fun getMediaByMediaIdsFlow(mediaIds: List<String>): Flow<List<MediaEntity>>

    @Query("DELETE FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.SOURCE_URI} IN (:uris)")
    suspend fun deleteMediaByUri(uris: List<String>)

    @Query("DELETE FROM ${Tables.LIBRARY_VIDEO} WHERE ${VideoColumns.SOURCE_URI} IN (:uris)")
    suspend fun deleteVideoByUri(uris: List<String>)

    @Transaction
    suspend fun deleteMediaByUris(uris: List<String>) {
        deleteMediaByUri(uris)
        deleteVideoByUri(uris)

        deleteOrphanAlbums()
        deleteOrphanGenres()
        deleteOrphanArtists()
    }

    @Query(
        """
        SELECT * FROM ${Tables.LIBRARY_ALBUM} 
        WHERE ${AlbumColumns.ID} IN (
            SELECT rowid FROM ${Tables.LIBRARY_FTS_ALBUM} 
            WHERE ${Tables.LIBRARY_FTS_ALBUM} MATCH :keyword
        )
    """,
    )
    suspend fun searchAlbum(keyword: String): List<AlbumEntity>

    @Query(
        """
        SELECT * FROM ${Tables.LIBRARY_MEDIA} 
        WHERE ${MediaColumns.ID} IN (
            SELECT rowid FROM ${Tables.LIBRARY_FTS_MEDIA} 
            WHERE ${Tables.LIBRARY_FTS_MEDIA} MATCH :keyword
        )
    """,
    )
    suspend fun searchMedia(keyword: String): List<MediaEntity>

    @Query(
        """
        SELECT * FROM ${Tables.LIBRARY_ARTIST} 
        WHERE ${ArtistColumns.ID} IN (
            SELECT rowid FROM ${Tables.LIBRARY_FTS_ARTIST} 
            WHERE ${Tables.LIBRARY_FTS_ARTIST} MATCH :keyword
        )
    """,
    )
    suspend fun searchArtist(keyword: String): List<ArtistEntity>

    private fun bucketIdWhere(bucketId: String) =
        Where(
            VideoColumns.BUCKET_ID,
            "=",
            bucketId,
        )

    private fun videoNotDeletedWhere() =
        Where(
            VideoColumns.DELETED,
            "IS NOT",
            "1",
        )

    private fun audioNotDeletedWhere() =
        Where(
            MediaColumns.DELETED,
            "IS NOT",
            "1",
        )

    private fun albumIdWhere(albumId: String) =
        Where(
            MediaColumns.ALBUM_ID,
            "=",
            albumId,
        )

    private fun artistIdWhere(artist: String) =
        Where(
            MediaColumns.ARTIST_ID,
            "=",
            artist,
        )

    private fun genreIdWhere(genreId: String) =
        Where(
            MediaColumns.GENRE_ID,
            "=",
            genreId,
        )

    @Transaction
    suspend fun upsertMedia(
        albums: List<AlbumEntity>,
        artists: List<ArtistEntity>,
        genres: List<GenreEntity>,
        audios: List<MediaEntity>,
        videos: List<VideoEntity> = emptyList(),
    ) {
        upsertAlbums(albums)
        upsertArtists(artists)
        upsertGenres(genres)
        upsertMedias(audios)
        upsertVideos(videos)
    }

    @Transaction
    suspend fun syncMediaLibrary(
        albums: List<AlbumEntity> = emptyList(),
        artists: List<ArtistEntity> = emptyList(),
        genres: List<GenreEntity> = emptyList(),
        audios: List<MediaEntity> = emptyList(),
        videos: List<VideoEntity> = emptyList(),
        onStep: (type: Int, inserted: Int, total: Int) -> Unit = { _, _, _ -> },
    ) {
        syncAlbum(
            albums,
            onStep = { inserted, total ->
                onStep(MediaType.ALBUM, inserted, total)
            },
        )
        syncArtist(
            artists,
            onStep = { inserted, total ->
                onStep(MediaType.ARTIST, inserted, total)
            },
        )
        syncGenre(
            genres,
            onStep = { inserted, total ->
                onStep(MediaType.GENRE, inserted, total)
            },
        )
        syncMedia(
            audios,
            onStep = { inserted, total ->
                onStep(MediaType.MEDIA, inserted, total)
            },
        )
        syncVideo(
            videos,
            onStep = { inserted, total ->
                onStep(MediaType.VIDEO, inserted, total)
            },
        )

        deleteOrphanAlbums()
        deleteOrphanGenres()
        deleteOrphanArtists()
    }

    private suspend fun syncMedia(
        audios: List<MediaEntity>,
        onStep: (inserted: Int, total: Int) -> Unit,
    ) {
        syncTable(
            newItems = audios,
            idSelector = { it.id },
            fetchLocalIdsDao = { getAllMediaID() },
            deleteDao = { deleteMediasByIds(it) },
            upsertDao = { upsertMedias(it) },
            onStep = onStep,
        )
    }

    private suspend fun syncVideo(
        audios: List<VideoEntity>,
        onStep: (inserted: Int, total: Int) -> Unit,
    ) {
        syncTable(
            newItems = audios,
            idSelector = { it.id },
            fetchLocalIdsDao = { getAllVideoID() },
            deleteDao = { deleteVideoByIds(it) },
            upsertDao = { upsertVideos(it) },
            onStep = onStep,
        )
    }

    private suspend fun syncAlbum(
        albums: List<AlbumEntity>,
        onStep: (inserted: Int, total: Int) -> Unit,
    ) {
        syncTable(
            newItems = albums,
            idSelector = { it.albumId },
            fetchLocalIdsDao = { getAllAlbumID() },
            deleteDao = { deleteAlbumsByIds(it) },
            upsertDao = { upsertAlbums(it) },
            onStep = onStep,
        )
    }

    private suspend fun syncArtist(
        artists: List<ArtistEntity>,
        onStep: (inserted: Int, total: Int) -> Unit,
    ) {
        syncTable(
            newItems = artists,
            idSelector = { it.artistId },
            fetchLocalIdsDao = { getAllArtistID() },
            deleteDao = { deleteArtistsByIds(it) },
            upsertDao = { upsertArtists(it) },
            onStep = onStep,
        )
    }

    private suspend fun syncGenre(
        artists: List<GenreEntity>,
        onStep: (inserted: Int, total: Int) -> Unit,
    ) {
        syncTable(
            newItems = artists,
            idSelector = { it.genreId },
            fetchLocalIdsDao = { getAllGenreID() },
            deleteDao = { deleteGenreByIds(it.filterNotNull()) },
            upsertDao = { upsertGenres(it) },
            onStep = onStep,
        )
    }

    private inline fun <T, K> syncTable(
        newItems: List<T>,
        idSelector: (T) -> K,
        fetchLocalIdsDao: () -> List<K>,
        deleteDao: (List<K>) -> Unit,
        upsertDao: (List<T>) -> Unit,
        onStep: (inserted: Int, total: Int) -> Unit,
        chunkSize: Int = DEFAULT_CHUNK_SIZE,
    ) {
        val localIds = fetchLocalIdsDao().toHashSet()

        val newIds = newItems.map(idSelector).toHashSet()

        val idsToDelete = localIds - newIds

        if (idsToDelete.isNotEmpty()) {
            idsToDelete.chunked(chunkSize).forEach { batch ->
                deleteDao(batch.toList())
            }
        }

        var currentCount = 0
        if (newItems.isNotEmpty()) {
            newItems.chunked(chunkSize).forEach { batch ->
                upsertDao(batch)
                currentCount += batch.size
                onStep(currentCount, newItems.size)
            }
        }
    }
}
