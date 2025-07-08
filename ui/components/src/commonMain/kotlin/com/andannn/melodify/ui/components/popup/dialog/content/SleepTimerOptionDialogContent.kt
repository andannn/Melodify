/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.popup.dialog.content

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
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.common.util.durationString
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.SleepTimerOption
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.end_of_song
import melodify.ui.common.generated.resources.sleep_timer
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SleepTimerOptionDialogContent(onAction: (DialogAction) -> Unit = {}) {
    SleepTimerOptionSheetContent(
        onSelectOption = {
            onAction(DialogAction.SleepTimerOptionDialog.OnOptionClick(it))
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
