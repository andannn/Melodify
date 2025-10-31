package com.andannn.melodify.core.data.model

/**
 * group key for media items
 */
sealed interface GroupKey {
    data class ARTIST(
        val artistId: String,
    ) : GroupKey

    data class ALBUM(
        val albumId: String,
    ) : GroupKey

    data class Genre(
        val genreId: String,
    ) : GroupKey

    data class YEAR(
        val year: String,
    ) : GroupKey

    data class TITLE(
        val firstCharacterString: String,
    ) : GroupKey
}
