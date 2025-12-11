package com.andannn.melodify.shared.compose.common.model

import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.album_page_title
import melodify.shared.compose.resource.generated.resources.artist_page_title
import melodify.shared.compose.resource.generated.resources.audio_page_title
import melodify.shared.compose.resource.generated.resources.genre_title
import melodify.shared.compose.resource.generated.resources.playlist_page_title
import melodify.shared.compose.resource.generated.resources.video_page_title
import org.jetbrains.compose.resources.StringResource

enum class ShortcutItem(
    val iconRes: SmpIcon,
    val textRes: StringResource,
) {
    ALL_SONG(
        iconRes = SimpleMusicIcons.Music,
        textRes = Res.string.audio_page_title,
    ),

    ALL_VIDEO(
        iconRes = SimpleMusicIcons.Video,
        textRes = Res.string.video_page_title,
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

    PLAYLIST(
        iconRes = SimpleMusicIcons.PlayList,
        textRes = Res.string.playlist_page_title,
    ),
}
