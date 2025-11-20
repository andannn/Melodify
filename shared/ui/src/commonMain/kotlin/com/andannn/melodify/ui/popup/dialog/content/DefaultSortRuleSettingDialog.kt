/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.popup.dialog.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.PresetDisplaySetting
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.ui.core.ScopedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.ui.widgets.PresetSortOptionSelector
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import melodify.shared.ui.generated.resources.Res
import melodify.shared.ui.generated.resources.change_default_sort_order
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "DefaultSortRuleSettingDialog"

@Composable
fun DefaultSortRuleSettingDialog(
    modifier: Modifier = Modifier,
    onAction: (DialogAction) -> Unit = {},
) {
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
                val selectedPresetOption = selectedAudioPresetOption
                PresetSortOptionSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedPresetOption = selectedPresetOption,
                    isAudio = true,
                    onChangePresetSortRule = onChangeAudioSortRule,
                )
            }

            item {
                val selectedPresetOption = selectedVideoPresetOption
                PresetSortOptionSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedPresetOption = selectedPresetOption,
                    isAudio = false,
                    onChangePresetSortRule = onChangeVideoSortRule,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun retainDefaultSortRulePresenter() =
    retainPresenter {
        DefaultSortRulePresenter()
    }

@OptIn(ExperimentalMaterial3Api::class)
private class DefaultSortRulePresenter(
    repository: Repository = getKoin().get<Repository>(),
) : ScopedPresenter<DefaultSortRuleState>() {
    private val userPreferences = repository.userPreferenceRepository

    private val audioDefaultDisplaySetting =
        userPreferences
            .getDefaultPresetSortRule(isAudio = true)
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )
    private val videoDefaultDisplaySetting =
        userPreferences
            .getDefaultPresetSortRule(isAudio = false)
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )

    @Composable
    override fun present(): DefaultSortRuleState {
        val audioDisplaySetting by audioDefaultDisplaySetting.collectAsStateWithLifecycle()
        val videoDisplaySetting by videoDefaultDisplaySetting.collectAsStateWithLifecycle()
        val selectedAudioPresetOption =
            remember(audioDisplaySetting) {
                PresetDisplaySetting.entries.firstOrNull {
                    audioDisplaySetting?.isPreset == true && it.displaySetting == audioDisplaySetting
                }
            }

        val selectedVideoPresetOption =
            remember(videoDisplaySetting) {
                PresetDisplaySetting.entries.firstOrNull {
                    videoDisplaySetting?.isPreset == true && it.displaySetting == videoDisplaySetting
                }
            }

        return DefaultSortRuleState(
            selectedAudioPresetOption,
            selectedVideoPresetOption,
        ) { event ->
            when (event) {
                is DefaultSortRuleStateEvent.ChangeAudioSortRule ->
                    retainedScope.launch {
                        userPreferences.saveDefaultSortRule(
                            isAudio = true,
                            event.sortRule,
                        )
                    }

                is DefaultSortRuleStateEvent.ChangeVideoSortRule ->
                    retainedScope.launch {
                        userPreferences.saveDefaultSortRule(
                            isAudio = false,
                            event.sortRule,
                        )
                    }
            }
        }
    }
}

@Stable
private data class DefaultSortRuleState(
    val audioDisplaySetting: PresetDisplaySetting?,
    val videoDisplaySetting: PresetDisplaySetting?,
    val eventSink: (DefaultSortRuleStateEvent) -> Unit = {},
)

private sealed interface DefaultSortRuleStateEvent {
    data class ChangeAudioSortRule(
        val sortRule: PresetDisplaySetting,
    ) : DefaultSortRuleStateEvent

    data class ChangeVideoSortRule(
        val sortRule: PresetDisplaySetting,
    ) : DefaultSortRuleStateEvent
}
