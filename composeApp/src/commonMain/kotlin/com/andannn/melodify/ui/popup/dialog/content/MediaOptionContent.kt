/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog.content

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.popup.dialog.OptionItem
import com.andannn.melodify.ui.widgets.SmpIcon
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MediaOptionContent(
    dialogId: DialogId.OptionDialog,
    modifier: Modifier = Modifier,
    onAction: (DialogAction) -> Unit = {},
) {
    Column(modifier.navigationBarsPadding().fillMaxWidth()) {
        dialogId.options.mapIndexed { index, item ->
            SheetItem(
                item = item,
                onClick = {
                    onAction(DialogAction.MediaOptionDialog.ClickOptionItem(item))
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
internal fun SheetItem(
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
