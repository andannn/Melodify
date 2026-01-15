/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.option

import com.andannn.melodify.shared.compose.common.model.SimpleMusicIcons
import com.andannn.melodify.shared.compose.common.model.SmpIcon
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.add_to_home_tab
import melodify.shared.compose.resource.generated.resources.add_to_playlist
import melodify.shared.compose.resource.generated.resources.add_to_queue
import melodify.shared.compose.resource.generated.resources.delete_from_playlist
import melodify.shared.compose.resource.generated.resources.delete_media_file
import melodify.shared.compose.resource.generated.resources.delete_this_tab
import melodify.shared.compose.resource.generated.resources.display_settings
import melodify.shared.compose.resource.generated.resources.go_to_library_album
import melodify.shared.compose.resource.generated.resources.go_to_library_artist
import melodify.shared.compose.resource.generated.resources.play_next
import melodify.shared.compose.resource.generated.resources.remove_playlist
import melodify.shared.compose.resource.generated.resources.sleep_timer
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class OptionItem(
    val smpIcon: SmpIcon,
    val text: StringResource,
) {
    PLAY_NEXT(
        smpIcon = SimpleMusicIcons.PlayNext,
        text = Res.string.play_next,
    ),
    ADD_TO_PLAYLIST(
        smpIcon = SimpleMusicIcons.AddPlayList,
        text = Res.string.add_to_playlist,
    ),
    DELETE_FROM_PLAYLIST(
        smpIcon = SimpleMusicIcons.PlayListRemove,
        text = Res.string.delete_from_playlist,
    ),
    DELETE_PLAYLIST(
        smpIcon = SimpleMusicIcons.Delete,
        text = Res.string.remove_playlist,
    ),
    ADD_TO_QUEUE(
        smpIcon = SimpleMusicIcons.QueueMusic,
        text = Res.string.add_to_queue,
    ),
    SLEEP_TIMER(
        smpIcon = SimpleMusicIcons.Timer,
        text = Res.string.sleep_timer,
    ),
    DELETE_TAB(
        smpIcon = SimpleMusicIcons.DeleteSweep,
        text = Res.string.delete_this_tab,
    ),
    ADD_TO_HOME_TAB(
        smpIcon = SimpleMusicIcons.AddToHomeTab,
        text = Res.string.add_to_home_tab,
    ),
    DISPLAY_SETTING(
        smpIcon = SimpleMusicIcons.DisplaySettings,
        text = Res.string.display_settings,
    ),
    OPEN_LIBRARY_ALBUM(
        smpIcon = SimpleMusicIcons.Album,
        text = Res.string.go_to_library_album,
    ),
    OPEN_LIBRARY_ARTIST(
        smpIcon = SimpleMusicIcons.Artist,
        text = Res.string.go_to_library_artist,
    ),
    DELETE_MEDIA_FILE(
        smpIcon = SimpleMusicIcons.DeleteSweep,
        text = Res.string.delete_media_file,
    ),
}
