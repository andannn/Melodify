package com.andannn.melodify.ui.components.library

import com.andannn.melodify.ui.common.icons.SimpleMusicIcons
import com.andannn.melodify.ui.common.icons.SmpIcon
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.album_page_title
import melodify.ui.common.generated.resources.artist_page_title
import melodify.ui.common.generated.resources.audio_page_title
import melodify.ui.common.generated.resources.genre_title
import melodify.ui.common.generated.resources.favorite
import melodify.ui.common.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.StringResource

enum class ShortcutItem(
    val iconRes: SmpIcon,
    val textRes: StringResource,
) {
    ALL_SONG(
        iconRes = SimpleMusicIcons.Music,
        textRes = Res.string.audio_page_title,
    ),

    ALBUM(
        iconRes = SimpleMusicIcons.Album,
        textRes = Res.string.album_page_title,
    ),

    ARTIST(
        iconRes = SimpleMusicIcons.Artist,
        textRes = Res.string.artist_page_title,
    ),

    GENRE(
        iconRes = SimpleMusicIcons.Genre,
        textRes = Res.string.genre_title,
    ),

    FAVORITE(
        iconRes = SimpleMusicIcons.AddFavorite,
        textRes = Res.string.favorite,
    ),

    PLAYLIST(
        iconRes = SimpleMusicIcons.PlayList,
        textRes = Res.string.playlist_page_title,
    )
}