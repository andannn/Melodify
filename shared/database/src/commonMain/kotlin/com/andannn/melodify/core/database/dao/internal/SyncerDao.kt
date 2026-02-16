/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.database.dao.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.AudioEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.VideoEntity

@Dao
internal interface SyncerDao {
    @Upsert(entity = AlbumEntity::class)
    suspend fun upsertAlbums(albums: List<AlbumEntity>): List<Long>

    @Upsert(entity = ArtistEntity::class)
    suspend fun upsertArtist(artists: List<ArtistEntity>): List<Long>

    @Query("SELECT video_title FROM library_video_table WHERE video_id IN (:ids)")
    suspend fun getNameOfVideo(ids: List<Long>): List<String>

    @Query("SELECT album_title FROM library_album_table WHERE album_id IN (:ids)")
    suspend fun getNameOfAlbum(ids: List<Long>): List<String>

    @Query("SELECT artist_name FROM library_artist_table WHERE artist_id IN (:ids)")
    suspend fun getNameOfArtist(ids: List<Long>): List<String>

    @Query("SELECT media_title FROM library_media_table WHERE media_id IN (:ids)")
    suspend fun getNameOfMedia(ids: List<Long>): List<String>

    @Upsert
    suspend fun upsertMedias(audios: List<AudioEntity>): List<Long>

    @Upsert
    suspend fun upsertVideos(audios: List<VideoEntity>): List<Long>

    @Insert(entity = GenreEntity::class, onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun upsertGenres(genres: List<GenreEntity>): List<Long>

    @Query("DELETE FROM library_album_table WHERE album_id IN (:ids)")
    suspend fun deleteAlbumsByIds(ids: List<Long>)

    @Query("DELETE FROM library_artist_table WHERE artist_id IN (:ids)")
    suspend fun deleteArtistsByIds(ids: List<Long>)

    @Query("DELETE FROM library_genre_table WHERE genre_id IN (:ids)")
    suspend fun deleteGenreByIds(ids: List<Long>)

    @Query("DELETE FROM library_media_table WHERE media_id IN (:ids)")
    suspend fun deleteMediasByIds(ids: List<Long>)

    @Query("DELETE FROM library_video_table WHERE video_id IN (:ids)")
    suspend fun deleteVideoByIds(ids: List<Long>)

    @Query("SELECT media_id FROM library_media_table")
    suspend fun getAllMediaID(): List<Long>

    @Query("SELECT video_id FROM library_video_table")
    suspend fun getAllVideoID(): List<Long>

    @Query("SELECT artist_id FROM library_artist_table")
    suspend fun getAllArtistID(): List<Long>

    @Query("SELECT genre_id FROM library_genre_table")
    suspend fun getAllGenreID(): List<Long>
}
