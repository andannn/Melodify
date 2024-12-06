package com.andannn.melodify.ui.components.popup.dialog

import com.andannn.melodify.ui.common.icons.SimpleMusicIcons
import com.andannn.melodify.ui.common.icons.SmpIcon
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.add_to_playlist
import melodify.ui.common.generated.resources.add_to_queue
import melodify.ui.common.generated.resources.delete
import melodify.ui.common.generated.resources.delete_from_playlist
import melodify.ui.common.generated.resources.delete_this_tab
import melodify.ui.common.generated.resources.play_next
import melodify.ui.common.generated.resources.remove_playlist
import melodify.ui.common.generated.resources.sleep_timer
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
        smpIcon = SimpleMusicIcons.Delete,
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

