package com.andannn.melodify.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
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

    @Transaction
    suspend fun insertLibrary(
        albums: List<AlbumEntity>,
        artists: List<ArtistEntity>,
        genres: List<GenreEntity>,
        audios: List<MediaEntity>
    ) {
        insertAlbums(albums)
        insertArtists(artists)
        insertGenres(genres)
        insertMedias(audios)
    }
}