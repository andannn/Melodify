package com.andannn.melodify.feature.player.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.feature.player.ui.shrinkable.bottom.SheetTab
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.lyrics
import melodify.feature.common.generated.resources.play_queue

fun PlayMode.getIcon() =
    when (this) {
        PlayMode.REPEAT_ONE -> Icons.Rounded.RepeatOneOn
        PlayMode.REPEAT_OFF -> Icons.Rounded.Repeat
        PlayMode.REPEAT_ALL -> Icons.Rounded.RepeatOn
    }

fun SheetTab.getLabel() = when(this) {
    SheetTab.NEXT_SONG -> Res.string.play_queue
    SheetTab.LYRICS -> Res.string.lyrics
}