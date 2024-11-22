package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.andannn.melodify.core.database.Tables
import com.andannn.melodify.core.database.entity.AlbumEntity
import com.andannn.melodify.core.database.entity.ArtistEntity
import com.andannn.melodify.core.database.entity.GenreEntity
import com.andannn.melodify.core.database.entity.MediaEntity

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

    @Transaction
    suspend fun clearAndInsertLibrary(
        albums: List<AlbumEntity>,
        artists: List<ArtistEntity>,
        genres: List<GenreEntity>,
        audios: List<MediaEntity>
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