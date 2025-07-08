/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.common.util

import androidx.compose.runtime.Composable
import com.andannn.melodify.core.data.model.CustomTab
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.audio_page_title
import melodify.ui.common.generated.resources.number_hours
import melodify.ui.common.generated.resources.number_minutes
import melodify.ui.common.generated.resources.number_seconds
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration

@Composable
fun durationString(duration: Duration): String {
    duration.toComponents { hours, minutes, seconds, nanoseconds ->
        val hasHours = hours != 0L
        val hasMinutes = minutes != 0
        val hasSeconds = seconds != 0 || nanoseconds != 0

        return buildString {
            if (hasHours) {
                append(stringResource(Res.string.number_hours, hours))
            }
            if (hasMinutes) {
                if (hasHours) {
                    append(" ")
                }
                append(stringResource(Res.string.number_minutes, minutes))
            }
            if (hasSeconds) {
                if (hasHours || hasMinutes) {
                    append(" ")
                }
                append(stringResource(Res.string.number_seconds, seconds))
            }
        }
    }
}

@Composable
fun getCategoryResource(category: CustomTab): String {
    return when (category) {
        CustomTab.AllMusic -> stringResource(Res.string.audio_page_title)
        is CustomTab.AlbumDetail -> category.label
        is CustomTab.ArtistDetail -> category.label
        is CustomTab.GenreDetail -> category.label
        is CustomTab.PlayListDetail -> category.label
    }
}
