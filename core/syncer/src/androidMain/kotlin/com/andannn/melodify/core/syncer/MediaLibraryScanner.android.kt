/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.andannn.melodify.core.database.dao.MediaLibraryDao
import com.andannn.melodify.core.syncer.model.MediaDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class MediaLibraryScannerImpl(
    private val app: Application,
    private val mediaLibraryDao: MediaLibraryDao,
) : MediaLibraryScanner {
    override suspend fun scanAllMedia(): MediaDataModel =
        coroutineScope {
            val musicDataDeferred = async { getAllMusicData() }
            val albumDataDeferred = async { getAllAlbumData() }
            val artistDataDeferred = async { getAllArtistData() }
            val genreDataDeferred = async { getAllGenreData() }

            return@coroutineScope MediaDataModel(
                audioData = musicDataDeferred.await(),
                albumData = albumDataDeferred.await(),
                artistData = artistDataDeferred.await(),
                genreData = genreDataDeferred.await(),
            )
        }

    override suspend fun scanMediaByUri(uris: List<String>): MediaDataModel {
        val ids = uris.mapNotNull { Uri.parse(it).lastPathSegment?.toLongOrNull() }
        val audioData = getMusicDataByIds(ids)
        val albumIds = audioData.mapNotNull { it.albumId }.distinct()
        val artistIds = audioData.mapNotNull { it.artistId }.distinct()
        val genreIds = audioData.mapNotNull { it.genreId }.distinct()

        return coroutineScope {
            val albumDataDeferred = async { getAlbumDataByIds(albumIds) }
            val artistDataDeferred = async { getArtistDataByIds(artistIds) }
            val genreDataDeferred = async { getGenreDataByIds(genreIds) }
            MediaDataModel(
                audioData = audioData,
                albumData = albumDataDeferred.await(),
                artistData = artistDataDeferred.await(),
                genreData = genreDataDeferred.await(),
            )
        }
    }

    private suspend fun getMusicDataByIds(ids: List<Long>) =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                selection = "${MediaStore.Audio.Media._ID} IN (${ids.joinToString(",") { "?" }})",
                selectionArgs = ids.map { it.toString() }.toTypedArray(),
            )?.use { cursor ->
                parseMusicInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getAlbumDataByIds(ids: List<Long>) =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                selection = "${MediaStore.Audio.Albums._ID} IN (${ids.joinToString(",") { "?" }})",
                selectionArgs = ids.map { it.toString() }.toTypedArray(),
            )?.use { cursor ->
                parseAlbumInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getArtistDataByIds(ids: List<Long>) =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                selection = "${MediaStore.Audio.Artists._ID} IN (${ids.joinToString(",") { "?" }})",
                selectionArgs = ids.map { it.toString() }.toTypedArray(),
            )?.use { cursor ->
                parseArtistInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getGenreDataByIds(ids: List<Long>) =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                selection = "${MediaStore.Audio.Genres._ID} IN (${ids.joinToString(",") { "?" }})",
                selectionArgs = ids.map { it.toString() }.toTypedArray(),
            )?.use { cursor ->
                parseGenreInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getAllMusicData() =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            )?.use { cursor ->
                parseMusicInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getAllAlbumData() =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            )?.use { cursor ->
                parseAlbumInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getAllArtistData() =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            )?.use { cursor ->
                parseArtistInfoCursor(cursor)
            } ?: emptyList()

    private suspend fun getAllGenreData() =
        app.contentResolver
            .query2(
                uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            )?.use { cursor ->
                parseGenreInfoCursor(cursor)
            } ?: emptyList()

    private fun parseGenreInfoCursor(cursor: Cursor): List<com.andannn.melodify.core.syncer.model.GenreData> {
        val itemList = mutableListOf<com.andannn.melodify.core.syncer.model.GenreData>()

        val idIndex = cursor.getColumnIndex(MediaStore.Audio.Genres._ID)
        val genreIndex = cursor.getColumnIndex(MediaStore.Audio.Genres.NAME)
        while (cursor.moveToNext()) {
            itemList.add(
                com.andannn.melodify.core.syncer.model.GenreData(
                    genreId = cursor.getLong(idIndex),
                    name = cursor.getString(genreIndex),
                ),
            )
        }
        return itemList
    }

    private fun parseMusicInfoCursor(cursor: Cursor): List<com.andannn.melodify.core.syncer.model.AudioData> {
        val itemList = mutableListOf<com.andannn.melodify.core.syncer.model.AudioData>()

        val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
        val titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
        val dateModifiedIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
        val sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
        val mimeTypeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)
        val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
        val artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
        val artistIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
        val cdTrackNumberIndex = cursor.getColumnIndex(MediaStore.Audio.Media.CD_TRACK_NUMBER)
        val discNumberIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISC_NUMBER)
        val numTracksIndex = cursor.getColumnIndex(MediaStore.Audio.Media.NUM_TRACKS)
        val bitrateIndex = cursor.getColumnIndex(MediaStore.Audio.Media.BITRATE)
        val yearIndex = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
        val composerIndex = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
        val genreIndex =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getColumnIndex(MediaStore.Audio.Media.GENRE)
            } else {
                null
            }
        val genreIdIndex =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                cursor.getColumnIndex(MediaStore.Audio.Media.GENRE_ID)
            } else {
                null
            }

        while (cursor.moveToNext()) {
            val albumId = cursor.getLong(albumIdIndex)
            val id = cursor.getLong(idIndex)
            itemList.add(
                com.andannn.melodify.core.syncer.model.AudioData(
                    id = id,
                    sourceUri =
                        Uri
                            .withAppendedPath(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id.toString(),
                            ).toString(),
                    cover =
                        Uri
                            .withAppendedPath(
                                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                albumId.toString(),
                            ).toString(),
                    title = cursor.getString(titleIndex),
                    duration = cursor.getInt(durationIndex),
                    modifiedDate = cursor.getLong(dateModifiedIndex),
                    size = cursor.getInt(sizeIndex),
                    mimeType = cursor.getString(mimeTypeIndex),
                    album = cursor.getString(albumIndex),
                    albumId = albumId,
                    artist = cursor.getString(artistIndex),
                    artistId = cursor.getLong(artistIdIndex),
                    cdTrackNumber = cursor.getInt(cdTrackNumberIndex),
                    discNumber = cursor.getInt(discNumberIndex),
                    numTracks = cursor.getInt(numTracksIndex),
                    bitrate = cursor.getInt(bitrateIndex),
                    genre = genreIndex?.let { cursor.getString(it) },
                    genreId = genreIdIndex?.let { cursor.getLong(it) },
                    year = cursor.getString(yearIndex),
                    composer = cursor.getString(composerIndex),
                ),
            )
        }
        return itemList
    }

    private fun parseArtistInfoCursor(cursor: Cursor): List<com.andannn.melodify.core.syncer.model.ArtistData> {
        val itemList = mutableListOf<com.andannn.melodify.core.syncer.model.ArtistData>()

        val idIndex = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
        val artistIndex = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
        val numberOfTracksIndex = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
        while (cursor.moveToNext()) {
            itemList.add(
                com.andannn.melodify.core.syncer.model.ArtistData(
                    artistId = cursor.getLong(idIndex),
                    name = cursor.getString(artistIndex),
                    artistCoverUri = "",
                    trackCount = cursor.getInt(numberOfTracksIndex),
                ),
            )
        }
        return itemList
    }

    private fun parseAlbumInfoCursor(cursor: Cursor): List<com.andannn.melodify.core.syncer.model.AlbumData> {
        val itemList = mutableListOf<com.andannn.melodify.core.syncer.model.AlbumData>()

        val idIndex = cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
        val albumIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
        val numberOfSongsIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
        val numberOfSongsForArtistIndex =
            cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST)
        while (cursor.moveToNext()) {
            val albumId = cursor.getLong(idIndex)
            itemList.add(
                com.andannn.melodify.core.syncer.model.AlbumData(
                    albumId = albumId,
                    coverUri =
                        Uri
                            .withAppendedPath(
                                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                albumId.toString(),
                            ).toString(),
                    title = cursor.getString(albumIndex),
                    trackCount = cursor.getInt(numberOfSongsIndex),
                    numberOfSongsForArtist = cursor.getInt(numberOfSongsForArtistIndex),
                ),
            )
        }
        return itemList
    }
}

suspend fun ContentResolver.query2(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    order: String = MediaStore.MediaColumns._ID,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE,
): Cursor? =
    withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val args2 =
                Bundle().apply {
                    // Limit & Offset
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)

                    // order
                    putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(order))
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        if (ascending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
                    )
                    // Selection and groupBy
                    if (selectionArgs != null) {
                        putStringArray(
                            ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                            selectionArgs,
                        )
                    }
                    if (selection != null) {
                        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                    }
                }
            query(uri, projection, args2, null)
        } else {
            val order2 =
                order + (if (ascending) " ASC" else " DESC") + " LIMIT $limit OFFSET $offset"
            query(uri, projection, selection, selectionArgs, order2)
        }
    }
