/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sort.rule

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun retainDefaultSortRulePresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(repository) {
        DefaultSortRulePresenter(repository)
    }

@OptIn(ExperimentalMaterial3Api::class)
private class DefaultSortRulePresenter(
    private val repository: Repository,
) : RetainedPresenter<DefaultSortRuleState>() {
    private val audioDefaultDisplaySetting =
        repository
            .getDefaultAudioPresetSortRule()
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )
    private val videoDefaultDisplaySetting =
        repository
            .getDefaultVideoPresetSortRule()
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )

// TODO: add playlist default display setting
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
                is DefaultSortRuleStateEvent.ChangeAudioSortRule -> {
                    retainedScope.launch {
                        repository.saveDefaultSortRule(
                            isAudio = true,
                            event.sortRule,
                        )
                    }
                }

                is DefaultSortRuleStateEvent.ChangeVideoSortRule -> {
                    retainedScope.launch {
                        repository.saveDefaultSortRule(
                            isAudio = false,
                            event.sortRule,
                        )
                    }
                }
            }
        }
    }
}

@Stable
internal data class DefaultSortRuleState(
    val audioDisplaySetting: PresetDisplaySetting?,
    val videoDisplaySetting: PresetDisplaySetting?,
    val eventSink: (DefaultSortRuleStateEvent) -> Unit = {},
)

internal sealed interface DefaultSortRuleStateEvent {
    data class ChangeAudioSortRule(
        val sortRule: PresetDisplaySetting,
    ) : DefaultSortRuleStateEvent

    data class ChangeVideoSortRule(
        val sortRule: PresetDisplaySetting,
    ) : DefaultSortRuleStateEvent
}
