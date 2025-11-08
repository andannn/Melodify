/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.model

import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.album_page_title
import melodify.shared.ui.generated.resources.artist_page_title
import melodify.shared.ui.generated.resources.audio_page_title
import melodify.shared.ui.generated.resources.favorite
import melodify.shared.ui.generated.resources.genre_title
import melodify.shared.ui.generated.resources.playlist_page_title
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
    ),
}
