package com.andannn.melodify.ui.components.library.util

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.ui.components.library.ShortcutItem
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource

internal fun ShortcutItem.toDataSource() =
    when (this) {
        ShortcutItem.ALL_SONG -> LibraryDataSource.AllSong
        ShortcutItem.ALBUM -> LibraryDataSource.AllAlbum
        ShortcutItem.ARTIST -> LibraryDataSource.AllArtist
        ShortcutItem.GENRE -> LibraryDataSource.AllGenre
        ShortcutItem.FAVORITE -> LibraryDataSource.Favorite
        ShortcutItem.PLAYLIST -> LibraryDataSource.AllPlaylist
    }

internal fun MediaItemModel.asDataSource() =
    when (this) {
        is AlbumItemModel -> LibraryDataSource.AlbumDetail(id)
        is ArtistItemModel -> LibraryDataSource.ArtistDetail(id)
        is GenreItemModel -> LibraryDataSource.GenreDetail(id)
        is PlayListItemModel -> LibraryDataSource.PlayListDetail(id)
        is AudioItemModel -> error("AudioItemModel should not be converted to DataSource")
    }
