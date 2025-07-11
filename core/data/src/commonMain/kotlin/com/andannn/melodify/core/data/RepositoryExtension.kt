/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel

context(repo: Repository)
suspend fun MediaItemModel.audios(): List<AudioItemModel> =
    when (this) {
        is AlbumItemModel -> {
            repo.getAudiosOfAlbum(id)
        }

        is ArtistItemModel -> {
            repo.getAudiosOfArtist(id)
        }

        is GenreItemModel -> {
            repo.getAudiosOfGenre(id)
        }

        is AudioItemModel -> {
            listOf(this)
        }

        is PlayListItemModel -> {
            repo.getAudiosOfPlayList(id.toLong())
        }
    }
