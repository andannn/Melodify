/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sync

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.syncer.ContentType
import com.andannn.melodify.core.syncer.SyncInfo
import com.andannn.melodify.core.syncer.SyncStatus
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.popup.common.DialogEntryProviderScope
import com.andannn.melodify.shared.compose.popup.common.DialogId
import com.andannn.melodify.shared.compose.popup.common.DialogType
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.sync_progress_album
import melodify.shared.compose.resource.generated.resources.sync_progress_artist
import melodify.shared.compose.resource.generated.resources.sync_progress_genre
import melodify.shared.compose.resource.generated.resources.sync_progress_media
import melodify.shared.compose.resource.generated.resources.sync_progress_video
import org.jetbrains.compose.resources.stringResource

data object SyncStatusDialog : DialogId<SyncStatusDialogAction>

class SyncStatusDialogAction

fun DialogEntryProviderScope<DialogId<*>>.syncStatusDialogEntry() {
    entry(
        dialogId = SyncStatusDialog,
        dialogType = DialogType.ModalBottomSheet,
    ) { dialogId, onAction ->
        SyncStatusDialogContent(onAction)
    }
}

@Composable
internal fun SyncStatusDialogContent(
    onAction: (SyncStatusDialog) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state = retainSyncStatusPresenter().present()
    SyncStatusDialog(
        modifier = modifier,
        status = state.syncState.syncStatus,
        syncInfoMap = state.syncState.syncInfoMap,
        onClickReSync = { state.eventSink.invoke(SyncStatusDialogEvent.OnReSync) },
    )
}

@Composable
private fun SyncStatusDialog(
    status: SyncStatus,
    modifier: Modifier = Modifier,
    syncInfoMap: Map<ContentType, SyncInfo>,
    onClickReSync: () -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            text = "Sync media library",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                items(
                    items = syncInfoMap.entries.toList(),
                    key = { it.key },
                ) { (contentType, info) ->
                    val (progress, detail) = info

                    val progressString = progress?.toInfoString(contentType) ?: ""
                    Column {
                        Text(progressString, style = MaterialTheme.typography.titleMedium)

                        detail.forEach { info ->
                            val text =
                                buildAnnotatedString {
                                    append("• ")
                                    if (info.isInsert) {
                                        withStyle(
                                            style =
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.primary,
                                                ),
                                        ) {
                                            append("[Add]")
                                        }
                                    } else {
                                        withStyle(
                                            style =
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.error,
                                                ),
                                        ) {
                                            append("[Remove]")
                                        }
                                    }
                                    append("\t ")

                                    append(info.item)
                                }
                            Text(
                                modifier =
                                    Modifier
                                        .padding(top = 4.dp, start = 24.dp)
                                        .fillMaxWidth(),
                                text = text,
                                maxLines = 1,
                                overflow = TextOverflow.MiddleEllipsis,
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally),
            enabled = status == SyncStatus.ERROR || status == SyncStatus.COMPLETED,
            onClick = onClickReSync,
        ) {
            Text(
                text = "Sync again",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SyncInfo.Progress.toInfoString(type: ContentType): String =
    when (type) {
        ContentType.MEDIA -> stringResource(Res.string.sync_progress_media, progress, total)
        ContentType.ALBUM -> stringResource(Res.string.sync_progress_album, progress, total)
        ContentType.ARTIST -> stringResource(Res.string.sync_progress_artist, progress, total)
        ContentType.GENRE -> stringResource(Res.string.sync_progress_genre, progress, total)
        ContentType.VIDEO -> stringResource(Res.string.sync_progress_video, progress, total)
    }

@Preview
@Composable
private fun SyncStatusDialogPreview() {
    MelodifyTheme {
        Surface {
            SyncStatusDialog(
                status = SyncStatus.START,
                syncInfoMap =
                    mapOf(
                        ContentType.MEDIA to
                            SyncInfo(
                                progress = SyncInfo.Progress(10, 100),
                                insertDeleteInfo =
                                    listOf(
                                        SyncInfo.Info(
                                            isInsert = true,
                                            item = "item1",
                                        ),
                                        SyncInfo.Info(
                                            isInsert = false,
                                            item = "item2",
                                        ),
                                    ),
                            ),
                        ContentType.VIDEO to
                            SyncInfo(
                                progress = SyncInfo.Progress(11, 100),
                            ),
                        ContentType.ARTIST to
                            SyncInfo(
                                progress = SyncInfo.Progress(12, 100),
                            ),
                        ContentType.ALBUM to
                            SyncInfo(
                                progress = SyncInfo.Progress(13, 100),
                            ),
                        ContentType.GENRE to
                            SyncInfo(
                                progress = SyncInfo.Progress(14, 100),
                            ),
                    ),
            )
        }
    }
}
