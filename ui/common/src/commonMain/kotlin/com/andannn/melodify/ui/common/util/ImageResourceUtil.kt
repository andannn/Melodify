package com.andannn.melodify.ui.common.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import com.andannn.melodify.core.data.model.PlayMode

fun PlayMode.getIcon() =
    when (this) {
        PlayMode.REPEAT_ONE -> Icons.Rounded.RepeatOneOn
        PlayMode.REPEAT_OFF -> Icons.Rounded.Repeat
        PlayMode.REPEAT_ALL -> Icons.Rounded.RepeatOn
    }
