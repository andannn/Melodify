/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.PresetDisplaySetting
import com.andannn.melodify.ui.util.headerText
import com.andannn.melodify.ui.util.subTitle
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.preset_audio
import melodify.shared.ui.generated.resources.preset_video
import org.jetbrains.compose.resources.stringResource

@Composable
fun PresetSortOptionSelector(
    modifier: Modifier = Modifier,
    selectedPresetOption: PresetDisplaySetting?,
    isAudio: Boolean,
    onChangePresetSortRule: (PresetDisplaySetting) -> Unit = {},
) {
    val options =
        if (isAudio) PresetDisplaySetting.AUDIO_OPTIONS else PresetDisplaySetting.VIDEO_OPTIONS

    Column(modifier) {
        Text(
            modifier =
                Modifier.padding(horizontal = 12.dp),
            text = stringResource(if (isAudio) Res.string.preset_audio else Res.string.preset_video),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        options.forEach { rule ->
            TransparentBackgroundListItem(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = {
                    if (rule != selectedPresetOption) {
                        onChangePresetSortRule(rule)
                    }
                },
                headlineContent = {
                    Text(
                        text = rule.headerText(),
                    )
                },
                supportingContent = {
                    Text(
                        text = rule.subTitle(),
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = selectedPresetOption == rule,
                        onClick = null,
                    )
                },
            )
        }
    }
}
