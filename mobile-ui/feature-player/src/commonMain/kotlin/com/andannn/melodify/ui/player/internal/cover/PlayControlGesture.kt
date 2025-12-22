package com.andannn.melodify.ui.player.internal.cover

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.playControlGesture(key: Any?) =
    Modifier.pointerInput(key) {
        detectTapGestures(
            onPress = {
            },
            onDoubleTap = {
            },
        )
    }
