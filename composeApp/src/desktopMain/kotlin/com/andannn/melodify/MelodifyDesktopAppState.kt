package com.andannn.melodify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope

private const val TAG = "MelodifyDesktopAppState"

@Composable
fun rememberMelodifyDesktopAppState(
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(
    scope
) {
    MelodifyDesktopAppState(
        scope = scope
    )
}

class MelodifyDesktopAppState(
    scope: CoroutineScope,
) {
}