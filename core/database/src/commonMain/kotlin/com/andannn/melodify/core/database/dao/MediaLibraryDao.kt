/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.entity.AlbumColumns
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistColumns
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.GenreColumns
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaColumns
import com.andannn.melodify.core.database.entity.MediaEntity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

private const val TAG = "MediaLibraryDao"

@Dao
interface MediaLibraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedias(audios: List<MediaEntity>)

    @Query("DELETE FROM ${Tables.LIBRARY_ALBUM}")
    suspend fun deleteAllAlbums()

    @Query("DELETE FROM ${Tables.LIBRARY_ARTIST}")
    suspend fun deleteAllArtists()

    @Query("DELETE FROM ${Tables.LIBRARY_GENRE}")
    suspend fun deleteAllGenres()

    @Query("DELETE FROM ${Tables.LIBRARY_MEDIA}")
    suspend fun deleteAllMedias()

    @Query("SELECT * FROM ${Tables.LIBRARY_ALBUM}")
    fun getAllAlbumFlow(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM ${Tables.LIBRARY_GENRE}")
    fun getAllGenreFlow(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM ${Tables.LIBRARY_ARTIST}")
    fun getAllArtistFlow(): Flow<List<ArtistEntity>>

    @RawQuery(observedEntities = [MediaEntity::class])
    fun getMediaFlowRaw(rawQuery: RoomRawQuery): Flow<List<MediaEntity>>

    fun getAllMediaFlow(sort: SortMethod? = null): Flow<List<MediaEntity>> = getMediaFlowRaw(buildAllMediaRawQuery(sort))

    private fun buildAllMediaRawQuery(sort: SortMethod?): RoomRawQuery {
        val sort = sort?.toSortString() ?: ""
        val sql = "SELECT * FROM ${Tables.LIBRARY_MEDIA} $sort"
        return RoomRawQuery(sql)
    }

    fun getMediasByAlbumIdFlow(
        albumId: String,
        sort: SortMethod?,
    ): Flow<List<MediaEntity>> = getMediaFlowRaw(buildAlbumMediaRawQuery(albumId, sort))

    private fun buildAlbumMediaRawQuery(
        albumId: String,
        sort: SortMethod?,
    ): RoomRawQuery {
        val sort = sort?.toSortString() ?: ""
        val sql =
            "SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ALBUM_ID} = $albumId $sort"
        Napier.d(tag = TAG) { "buildAlbumMediaRawQuery: $sql" }
        return RoomRawQuery(sql)
    }

    fun getMediasByArtistIdFlow(
        artistId: String,
        sort: SortMethod?,
    ): Flow<List<MediaEntity>> = getMediaFlowRaw(buildArtistMediaRawQuery(artistId, sort))

    private fun buildArtistMediaRawQuery(
        artistId: String,
        sort: SortMethod?,
    ): RoomRawQuery {
        val sort = sort?.toSortString() ?: ""
        val sql =
            "SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ARTIST_ID} = $artistId $sort"
        Napier.d(tag = TAG) { "buildArtistMediaRawQuery: $sql" }
        return RoomRawQuery(sql)
    }

    fun getMediasByGenreIdFlow(
        genreId: String,
        sort: SortMethod?,
    ): Flow<List<MediaEntity>> = getMediaFlowRaw(buildGenreMediaRawQuery(genreId, sort))

    private fun buildGenreMediaRawQuery(
        genreId: String,
        sort: SortMethod?,
    ): RoomRawQuery {
        val sort = sort?.toSortString() ?: ""
        val sql =
            "SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.GENRE_ID} = $genreId $sort"
        Napier.d(tag = TAG) { "buildGenreMediaRawQuery: $sql" }
        return RoomRawQuery(sql)
    }

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ALBUM_ID} = :albumId")
    suspend fun getMediasByAlbumId(albumId: String): List<MediaEntity>

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.ARTIST_ID} = :artistId")
    suspend fun getMediasByArtistId(artistId: String): List<MediaEntity>

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.GENRE_ID} = :genreId")
    fun getMediasByGenreIdFlow(genreId: String): Flow<List<MediaEntity>>

    @Query("SELECT * FROM ${Tables.LIBRARY_MEDIA} WHERE ${MediaColumns.GENRE_ID} = :genreId")
    suspend fun getMediasByGenreId(genreId: String): List<MediaEntity>

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
    ) {
        deleteAllAlbums()
        deleteAllArtists()
        deleteAllGenres()
        deleteAllMedias()

        insertAlbums(albums)
        insertArtists(artists)
        insertGenres(genres)
        insertMedias(audios)
    }
}
