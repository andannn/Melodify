/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sleep.timer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.durationString
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.popup.common.DialogEntryProviderScope
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.common.DialogType
import com.andannn.melodify.shared.compose.popup.common.entry
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.cancel_timer
import melodify.shared.compose.resource.generated.resources.sleep_timer
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

sealed interface SleepTimerCountingDialog {
    data object OnCancelTimer : SleepTimerCountingDialog
}

data object SleepCountingDialog : DialogId<SleepTimerCountingDialog>

fun DialogEntryProviderScope<DialogId<*>>.sleepTimerCountingDialogEntry() {
    entry(
        dialogId = SleepCountingDialog,
        dialogType = DialogType.ModalBottomSheet,
    ) { _, onAction ->
        SleepTimerCountingContent(onAction)
    }
}

@Composable
internal fun SleepTimerCountingContent(
    onAction: (SleepTimerCountingDialog) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state = retainCounterPresenter().present()
    SleepTimerCounterSheet(
        modifier = modifier,
        remain = state.remainTime,
        onClickCancel = {
            onAction(SleepTimerCountingDialog.OnCancelTimer)
        },
    )
}

@Composable
private fun SleepTimerCounterSheet(
    remain: Duration,
    modifier: Modifier = Modifier,
    onClickCancel: () -> Unit = {},
) {
    Surface(modifier = modifier) {
        Column {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(Res.string.sleep_timer),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = durationString(duration = remain),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally),
                onClick = onClickCancel,
            ) {
                Text(
                    text = stringResource(Res.string.cancel_timer),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
private fun SleepTimerCountingContentPreview() {
    MelodifyTheme {
        Surface {
            SleepTimerCounterSheet(
                remain = 12.minutes,
            )
        }
    }
}
