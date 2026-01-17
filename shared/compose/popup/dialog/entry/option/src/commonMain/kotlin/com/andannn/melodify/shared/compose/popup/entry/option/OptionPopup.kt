/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.option

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.SmpIcon
import io.github.andannn.popup.PopupEntryProviderScope
import io.github.andannn.popup.PopupId
import org.jetbrains.compose.resources.stringResource

data class OptionPopup(
    val options: List<OptionItem>,
) : PopupId<MediaOptionDialogResult>

sealed interface MediaOptionDialogResult {
    data class ClickOptionItemResult(
        val optionItem: OptionItem,
    ) : MediaOptionDialogResult
}

fun PopupEntryProviderScope<PopupId<*>>.addMediaOptionDialogEntry() {
    entry(
        metadata = optionDialogType,
    ) { dialogId, onAction ->
        MediaOptionContent(
            dialogId,
            onAction = onAction,
        )
    }
}

internal expect val optionDialogType: Map<String, Any>

@Composable
private fun MediaOptionContent(
    dialogId: OptionPopup,
    onAction: (MediaOptionDialogResult) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier.navigationBarsPadding().fillMaxWidth()) {
        dialogId.options.mapIndexed { index, item ->
            SheetItem(
                item = item,
                onClick = {
                    onAction(MediaOptionDialogResult.ClickOptionItemResult(item))
                },
            )

            if (index != dialogId.options.size - 1) {
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SheetItem(
    item: OptionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        SmpIcon(item.smpIcon)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(item.text),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview
@Composable
private fun MediaOptionContentPreview() {
    MelodifyTheme {
        Surface {
            MediaOptionContent(
                OptionPopup(
                    options =
                        listOf(
                            OptionItem.DELETE_MEDIA_FILE,
                            OptionItem.DISPLAY_SETTING,
                            OptionItem.SLEEP_TIMER,
                            OptionItem.DELETE_PLAYLIST,
                        ),
                ),
            )
        }
    }
}
