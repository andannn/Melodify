package com.andannn.melodify.feature.common.util

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel

val MediaItemModel.key get() = when (this) {
    is AlbumItemModel,
    is ArtistItemModel,
    is GenreItemModel,
    is PlayListItemModel -> id
    is AudioItemModel -> {
        if (this.isValid()) {
            // Use hashCode as key for invalid item
            this.hashCode()
        } else {
            id
        }
    }
}

val MediaItemModel.browsableOrPlayable get() = when (this) {
    is AlbumItemModel,
    is ArtistItemModel,
    is GenreItemModel,
    is PlayListItemModel -> true
    is AudioItemModel -> {
        this.isValid()
    }
}