package com.andannn.melodify.core.data.model

import com.andannn.melodify.core.database.dao.PlayListDao.Companion.FAVORITE_PLAY_LIST_ID

sealed interface MediaItemModel {
    val id: String
    val name: String
    val artWorkUri: String
    val trackCount: Int
}

data class AudioItemModel constructor(
    override val id: String,
    override val name: String,
    override val artWorkUri: String,
    val modifiedDate: Long,
    val album: String,
    val albumId: String,
    val artist: String,
    val artistId: String,
    val cdTrackNumber: Int,
    val discNumber: Int,
    val source: String,
    val extraUniqueId: String? = null,
    override val trackCount: Int = -1,
) : MediaItemModel {
    companion object {
        val DEFAULT = AudioItemModel("0", "", "", 0, "", "0", "", "0", 0, 0, "")

        // prefix for invalid item which local file is deleted but still in playlist
        const val INVALID_ID_PREFIX = "invalid_id_"
    }

    fun isValid() = !this.id.contains(INVALID_ID_PREFIX)
}

data class AlbumItemModel(
    override val id: String,
    override val name: String,
    override val artWorkUri: String,
    override val trackCount: Int,
) : MediaItemModel {
    companion object {
        val DEFAULT = AlbumItemModel("0", "", "", 0)
    }
}

data class ArtistItemModel(
    override val id: String,
    override val name: String,
    override val artWorkUri: String,
    override val trackCount: Int,
) : MediaItemModel {
    companion object {
        val DEFAULT = ArtistItemModel("0", "", "", 0)
    }
}

data class GenreItemModel(
    override val id: String,
    override val name: String,
    override val artWorkUri: String,
    override val trackCount: Int,
) : MediaItemModel {
    companion object {
        val DEFAULT = GenreItemModel("0", "", "", 0)
        val UNKNOWN = GenreItemModel("-1", "", "", 0)
    }
}

data class PlayListItemModel(
    override val id: String,
    override val name: String,
    override val artWorkUri: String,
    override val trackCount: Int,
) : MediaItemModel {
    val isFavorite: Boolean
        get() = id.toLong() == FAVORITE_PLAY_LIST_ID
}