/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.andannn.melodify.core.database.MediaType
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.VideoEntity
import com.andannn.melodify.core.database.model.AlbumWithMediaCount
import com.andannn.melodify.core.database.model.ArtistWithMediaCount
import com.andannn.melodify.core.database.model.LibraryContentSearchResult
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaLibraryDao {
    @Query("UPDATE library_media_table SET deleted = 1 WHERE media_id IN (:ids)")
    suspend fun markMediaAsDeleted(ids: List<String>)

    @Query("UPDATE library_video_table SET video_deleted = 1 WHERE video_id IN (:ids)")
    suspend fun markVideoAsDeleted(ids: List<String>)

    @Query(
        """
        DELETE FROM library_album_table
        WHERE album_id NOT IN (
            SELECT DISTINCT media_album_id 
            FROM library_media_table 
            WHERE media_album_id IS NOT NULL
        )
    """,
    )
    suspend fun deleteOrphanAlbums()

    @Query(
        """
        DELETE FROM library_artist_table
        WHERE artist_id NOT IN (
            SELECT DISTINCT media_artist_id 
            FROM library_media_table 
            WHERE media_artist_id IS NOT NULL
        )
    """,
    )
    suspend fun deleteOrphanArtists()

    @Query(
        """
        DELETE FROM library_genre_table
        WHERE genre_id NOT IN (
            SELECT DISTINCT media_genre_id 
            FROM library_media_table 
            WHERE media_genre_id IS NOT NULL
        )
    """,
    )
    suspend fun deleteOrphanGenres()

    @Query("SELECT album_id FROM library_album_table")
    suspend fun getAllAlbumID(): List<Long>

    @Query(
        """
        SELECT a.*, COUNT(m.media_id) AS track_count
        FROM library_album_table AS a
        LEFT JOIN library_media_table AS m ON a.album_id = m.media_album_id
        GROUP BY a.album_id
    """,
    )
    fun getAllAlbumFlow(): Flow<List<AlbumWithMediaCount>>

    @Query("SELECT * FROM library_genre_table")
    fun getAllGenreFlow(): Flow<List<GenreEntity>>

    @Query(
        """
        SELECT a.*, COUNT(m.media_id) AS track_count
        FROM library_artist_table AS a
        LEFT JOIN library_media_table AS m ON a.artist_id = m.media_artist_id
        GROUP BY a.artist_id
    """,
    )
    fun getAllArtistFlow(): Flow<List<ArtistWithMediaCount>>

    @Query("SELECT * FROM library_media_table WHERE media_genre_id = :genreId")
    fun getMediasByGenreIdFlow(genreId: String): Flow<List<AudioEntity>>

    @Query(
        """
        SELECT a.*, COUNT(m.media_id) AS track_count
        FROM library_album_table AS a
        LEFT JOIN library_media_table AS m ON a.album_id = m.media_album_id
        WHERE album_id = :albumId
        GROUP BY a.album_id
    """,
    )
    fun getAlbumByAlbumIdFlow(albumId: Long): Flow<AlbumWithMediaCount?>

    @Query(
        """
        SELECT a.*, COUNT(m.media_id) AS track_count
        FROM library_artist_table AS a
        LEFT JOIN library_media_table AS m ON a.artist_id = m.media_artist_id
        WHERE a.artist_id = :artistId
        GROUP BY a.artist_id        
    """,
    )
    fun getArtistByArtistIdFlow(artistId: Long): Flow<ArtistWithMediaCount?>

    @Query("SELECT * FROM library_genre_table WHERE genre_id = :genreId")
    fun getGenreByGenreIdFlow(genreId: String): Flow<GenreEntity?>

    @Query("SELECT * FROM library_genre_table WHERE genre_id = :genreId")
    suspend fun getGenreByGenreId(genreId: Long): GenreEntity?

    @Query("SELECT * FROM library_media_table WHERE media_id IN (:mediaIds)")
    suspend fun getMediaByMediaIds(mediaIds: List<Long>): List<AudioEntity>

    @Query("SELECT * FROM library_video_table WHERE video_id IN (:videoIds)")
    suspend fun getVideoByVideoIds(videoIds: List<Long>): List<VideoEntity>

    @Query("SELECT * FROM library_media_table WHERE source_uri IN (:sources)")
    suspend fun getMediaByMediaSourceUrl(sources: List<String>): List<AudioEntity>

    @Query("DELETE FROM library_media_table WHERE source_uri IN (:uris)")
    suspend fun deleteMediaByUri(uris: List<String>)

    @Query("DELETE FROM library_video_table WHERE video_source_uri IN (:uris)")
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
        SELECT library_album_table.album_id AS id, library_album_table.album_title AS title, ${MediaType.ALBUM} AS type FROM library_album_table 
        WHERE album_id IN (
            SELECT rowid FROM library_fts_album_table 
            WHERE library_fts_album_table MATCH :keyword
        )
        
        UNION
        
        SELECT library_media_table.media_id AS id, library_media_table.media_title AS title, ${MediaType.MEDIA} AS type FROM library_media_table 
        WHERE media_id IN (
            SELECT rowid FROM library_fts_media_table 
            WHERE library_fts_media_table MATCH :keyword
        )
        
        UNION

        SELECT library_artist_table.artist_id AS id, library_artist_table.artist_name AS title, ${MediaType.ARTIST} AS type FROM library_artist_table 
        WHERE artist_id IN (
            SELECT rowid FROM library_fts_artist_table
            WHERE library_fts_artist_table MATCH :keyword
        )
        
        UNION

        SELECT library_video_table.video_id AS id, library_video_table.video_title AS title, ${MediaType.VIDEO} AS type FROM library_video_table 
        WHERE video_id IN (
            SELECT rowid FROM library_fts_video_table
            WHERE library_fts_video_table MATCH :keyword
        )
        
        UNION

        SELECT play_list_table.play_list_id AS id, play_list_table.play_list_name AS title, ${MediaType.PLAY_LIST} AS type FROM play_list_table 
        WHERE play_list_id IN (
            SELECT rowid FROM play_list_fts_table
            WHERE play_list_fts_table MATCH :keyword
        )

        ORDER BY type
    """,
    )
    suspend fun searchContentByKeyword(keyword: String): List<LibraryContentSearchResult>
}
