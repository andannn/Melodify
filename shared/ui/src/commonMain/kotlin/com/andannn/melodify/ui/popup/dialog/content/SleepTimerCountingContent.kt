/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog.content

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.RetainedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.ui.theme.MelodifyTheme
import com.andannn.melodify.ui.util.durationString
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.cancel_timer
import melodify.shared.ui.generated.resources.sleep_timer
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun SleepTimerCountingContent(
    modifier: Modifier = Modifier,
    onAction: (DialogAction) -> Unit = {},
) {
    val state = retainCounterPresenter().present()
    SleepTimerCounterSheetContent(
        modifier = modifier,
        remain = state.remainTime,
        onClickCancel = {
            onAction(DialogAction.SleepTimerCountingDialog.OnCancelTimer)
        },
    )
}

@Composable
private fun SleepTimerCounterSheetContent(
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

@Composable
private fun retainCounterPresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(
        repository,
    ) {
        CounterPresenter(repository)
    }

private class CounterPresenter(
    repository: Repository,
) : RetainedPresenter<CounterState>() {
    private val remainedTimeFlow =
        repository
            .observeRemainTime()
            .stateIn(
                retainedScope,
                initialValue = 0.seconds,
                started = WhileSubscribed(5000),
            )

    @Composable
    override fun present(): CounterState {
        val remainTime by remainedTimeFlow.collectAsStateWithLifecycle()
        return CounterState(remainTime)
    }
}

private data class CounterState(
    val remainTime: Duration,
)
