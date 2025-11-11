/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
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
import com.andannn.melodify.core.database.entity.PlayListWithMediaCrossRefColumns
import com.andannn.melodify.core.database.entity.VideoColumns
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.toSortString
import com.andannn.melodify.core.database.toWhereString
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

private const val TAG = "MediaLibraryDao"

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Query("UPDATE ${Tables.LIBRARY_MEDIA} SET ${MediaColumns.DELETED} = 1 WHERE ${MediaColumns.ID} IN (:ids)")
    suspend fun markMediaAsDeleted(ids: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedias(audios: List<MediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(audios: List<VideoEntity>)

    @Query("DELETE FROM ${Tables.LIBRARY_ALBUM}")
    suspend fun deleteAllAlbums()

    @Query("DELETE FROM ${Tables.LIBRARY_ARTIST}")
    suspend fun deleteAllArtists()

    @Query("DELETE FROM ${Tables.LIBRARY_GENRE}")
    suspend fun deleteAllGenres()

    @Query("DELETE FROM ${Tables.LIBRARY_MEDIA}")
    suspend fun deleteAllMedias()

    @Query("DELETE FROM ${Tables.LIBRARY_VIDEO}")
    suspend fun deleteAllVideos()

    @Query("SELECT * FROM ${Tables.LIBRARY_ALBUM}")
    fun getAllAlbumFlow(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM ${Tables.LIBRARY_GENRE}")
    fun getAllGenreFlow(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM ${Tables.LIBRARY_ARTIST}")
    fun getAllArtistFlow(): Flow<List<ArtistEntity>>

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

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ID} IN (:mediaIds)")
    fun getMediaByMediaIdsFlow(mediaIds: List<String>): Flow<List<MediaEntity>>

    @Query("DELETE FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.SOURCE_URI} IN (:uris)")
    suspend fun deleteMediaByUri(uris: List<String>)

    @Transaction
    suspend fun deleteMediaByUris(uris: List<String>) {
        deleteMediaByUri(uris)
        deleteInvalidPlayListRefItem()
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

    @Query(
        """
    DELETE FROM ${Tables.PLAY_LIST_WITH_MEDIA_CROSS_REF}
    WHERE ${PlayListWithMediaCrossRefColumns.MEDIA_STORE_ID} NOT IN (
        SELECT ${MediaColumns.ID} FROM ${Tables.LIBRARY_MEDIA}
    );
    """,
    )
    suspend fun deleteInvalidPlayListRefItem()

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
    ) {
        insertAlbums(albums)
        insertArtists(artists)
        insertGenres(genres)
        insertMedias(audios)
    }

    @Transaction
    suspend fun clearAndInsertLibrary(
        albums: List<AlbumEntity>,
        artists: List<ArtistEntity>,
        genres: List<GenreEntity>,
        audios: List<MediaEntity>,
        videos: List<VideoEntity> = emptyList(),
        onStep: (type: Int, inserted: Int, total: Int) -> Unit,
    ) {
        deleteAllAlbums()
        deleteAllArtists()
        deleteAllGenres()
        deleteAllMedias()
        deleteAllVideos()

        batchInsert(albums, DEFAULT_CHUNK_SIZE) { inserted, total ->
            onStep(MediaType.ALBUM, inserted, total)
        }
        batchInsert(artists, DEFAULT_CHUNK_SIZE) { inserted, total ->
            onStep(MediaType.ARTIST, inserted, total)
        }
        batchInsert(genres, DEFAULT_CHUNK_SIZE) { inserted, total ->
            onStep(MediaType.GENRE, inserted, total)
        }
        batchInsert(audios, DEFAULT_CHUNK_SIZE) { inserted, total ->
            onStep(MediaType.MEDIA, inserted, total)
        }
        batchInsert(videos, DEFAULT_CHUNK_SIZE) { inserted, total ->
            onStep(MediaType.VIDEO, inserted, total)
        }
        deleteInvalidPlayListRefItem()
    }

    private suspend fun <T> batchInsert(
        all: List<T>,
        chunk: Int,
        onStep: (inserted: Int, total: Int) -> Unit,
    ) {
        val total = all.size.coerceAtLeast(1)
        var done = 0
        all
            .asSequence()
            .chunked(chunk)
            .forEach { part ->
                when (val first = part.firstOrNull()) {
                    is AlbumEntity -> insertAlbums(part as List<AlbumEntity>)
                    is ArtistEntity -> insertArtists(part as List<ArtistEntity>)
                    is GenreEntity -> insertGenres(part as List<GenreEntity>)
                    is MediaEntity -> insertMedias(part as List<MediaEntity>)
                    is VideoEntity -> insertVideos(part as List<VideoEntity>)
                    else -> Unit
                }
                done += part.size
                onStep(done, total)
            }
    }
}
