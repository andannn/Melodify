package com.andannn.melodify.ui.components.popup.dialog.content

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.repository.MediaControllerRepository
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.common.util.durationString
import com.andannn.melodify.ui.components.popup.DialogAction
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.cancel_timer
import melodify.ui.common.generated.resources.sleep_timer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.mp.KoinPlatform.getKoin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun SleepTimerCountingContent(
    modifier: Modifier = Modifier,
    onAction: (DialogAction) -> Unit = {},
) {
    val remainTime by getKoin().get<MediaControllerRepository>().observeRemainTime()
        .collectAsState(0.seconds)
    SleepTimerCounterSheetContent(
        remain = remainTime,
        onClickCancel = {
            onAction(DialogAction.SleepTimerCountingDialog.OnCancelTimer)
        }
    )
}

@Composable
private fun SleepTimerCounterSheetContent(
    remain: Duration,
    modifier: Modifier = Modifier,
    onClickCancel: () -> Unit = {}
) {
    Surface(modifier = modifier) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(Res.string.sleep_timer),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = durationString(duration = remain),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                modifier = Modifier
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
private fun SleepTimerCounterSheetContentPreview() {
    MelodifyTheme {
        SleepTimerCounterSheetContent(
            remain = 121234.seconds
        )
    }
}