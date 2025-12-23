/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
internal fun TouchToggleVisible(
    modifier: Modifier = Modifier,
    showDurationMs: Long = 4000,
    content: @Composable () -> Unit,
) {
    var isShowing by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isShowing) {
        if (isShowing) {
            delay(showDurationMs)
            isShowing = false
        }
    }
    Box(
        modifier =
            modifier
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = {
                        isShowing = !isShowing
                    },
                ),
    ) {
        AnimatedVisibility(
            visible = isShowing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            content()
        }
    }
}
