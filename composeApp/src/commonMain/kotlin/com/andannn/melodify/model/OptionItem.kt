/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog

import com.andannn.melodify.model.SimpleMusicIcons
import com.andannn.melodify.model.SmpIcon
import melodify.composeapp.generated.resources.Res
import melodify.composeapp.generated.resources.add_to_home_tab
import melodify.composeapp.generated.resources.add_to_playlist
import melodify.composeapp.generated.resources.add_to_queue
import melodify.composeapp.generated.resources.delete_from_playlist
import melodify.composeapp.generated.resources.delete_this_tab
import melodify.composeapp.generated.resources.display_settings
import melodify.composeapp.generated.resources.play_next
import melodify.composeapp.generated.resources.remove_playlist
import melodify.composeapp.generated.resources.sleep_timer
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
}

enum class SleepTimerOption(
    val timeMinutes: Duration?,
) {
    FIVE_MINUTES(5.minutes),
    FIFTEEN_MINUTES(15.minutes),
    THIRTY_MINUTES(30.minutes),
    SIXTY_MINUTES(60.minutes),
    SONG_FINISH(null),
}
