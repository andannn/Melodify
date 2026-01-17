/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sleep.timer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.durationString
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import com.andannn.melodify.shared.compose.popup.PopupEntryProviderScope
import com.andannn.melodify.shared.compose.popup.PopupId
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.end_of_song
import melodify.shared.compose.resource.generated.resources.sleep_timer
import org.jetbrains.compose.resources.stringResource

data object SleepTimerOptionPopup : PopupId<SleepTimerOptionDialogAction>

sealed interface SleepTimerOptionDialogAction {
    data class OnOptionClick(
        val option: SleepTimerOption,
    ) : SleepTimerOptionDialogAction
}

fun PopupEntryProviderScope<PopupId<*>>.sleepTimerOptionDialogEntry() {
    entry(
        dialogId = SleepTimerOptionPopup,
        metadata = ModalBottomSheetFactoryProvider.bottomSheet(),
    ) { _, onAction ->
        SleepTimerOptionDialogContent(onAction)
    }
}

@Composable
private fun SleepTimerOptionDialogContent(onAction: (SleepTimerOptionDialogAction) -> Unit = {}) {
    SleepTimerOptionSheetContent(
        onSelectOption = {
            onAction(SleepTimerOptionDialogAction.OnOptionClick(it))
        },
    )
}

@Composable
private fun SleepTimerOptionSheetContent(
    modifier: Modifier = Modifier,
    onSelectOption: (SleepTimerOption) -> Unit = {},
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(Res.string.sleep_timer),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(3.dp))

            SleepTimerOption.entries.forEach {
                OptionItem(
                    text =
                        if (it != SleepTimerOption.SONG_FINISH) {
                            durationString(it.timeMinutes!!)
                        } else {
                            stringResource(Res.string.end_of_song)
                        },
                    onClick = {
                        onSelectOption.invoke(it)
                    },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun OptionItem(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun SleepTimerOptionDialogContentPreview() {
    MelodifyTheme {
        Surface {
            SleepTimerOptionDialogContent()
        }
    }
}
