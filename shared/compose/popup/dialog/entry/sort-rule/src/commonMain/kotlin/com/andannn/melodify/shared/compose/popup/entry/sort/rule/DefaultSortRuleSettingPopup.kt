/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sort.rule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.ContentSortType
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.popup.ModalBottomSheetFactoryProvider
import io.github.andannn.popup.PopupEntryProviderScope
import io.github.andannn.popup.PopupId
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.change_default_sort_order
import org.jetbrains.compose.resources.stringResource

data object DefaultSortRuleSettingPopup : PopupId<Unit>

fun PopupEntryProviderScope<PopupId<*>>.defaultSortRuleSettingDialogEntry() {
    entry(
        dialogId = DefaultSortRuleSettingPopup,
        metadata = ModalBottomSheetFactoryProvider.bottomSheet(),
    ) { _, _ ->
        DefaultSortRuleSettingDialog()
    }
}

@Composable
private fun DefaultSortRuleSettingDialog(modifier: Modifier = Modifier) {
    val state = retainDefaultSortRulePresenter().present()

    DefaultSortRuleSettingDialogContent(
        modifier = modifier,
        selectedAudioPresetOption = state.audioDisplaySetting,
        selectedVideoPresetOption = state.videoDisplaySetting,
        onChangeAudioSortRule = {
            state.eventSink.invoke(
                DefaultSortRuleStateEvent.ChangeAudioSortRule(it),
            )
        },
        onChangeVideoSortRule = {
            state.eventSink.invoke(
                DefaultSortRuleStateEvent.ChangeVideoSortRule(it),
            )
        },
    )
}

@Composable
private fun DefaultSortRuleSettingDialogContent(
    modifier: Modifier = Modifier,
    selectedAudioPresetOption: PresetDisplaySetting? = null,
    selectedVideoPresetOption: PresetDisplaySetting? = null,
    onChangeAudioSortRule: (PresetDisplaySetting) -> Unit = {},
    onChangeVideoSortRule: (PresetDisplaySetting) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxHeight()) {
        val title = stringResource(Res.string.change_default_sort_order)
        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                PresetSortOptionSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedPresetOption = selectedAudioPresetOption,
                    type = ContentSortType.Audio,
                    onChangePresetSortRule = onChangeAudioSortRule,
                )
            }

            item {
                PresetSortOptionSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedPresetOption = selectedVideoPresetOption,
                    type = ContentSortType.Audio,
                    onChangePresetSortRule = onChangeVideoSortRule,
                )
            }
        }
    }
}

@Preview
@Composable
private fun DefaultSortRuleSettingDialogContentPreview() {
    MelodifyTheme {
        Surface {
            DefaultSortRuleSettingDialogContent()
        }
    }
}
