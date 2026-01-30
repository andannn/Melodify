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
import com.andannn.melodify.domain.model.ContentSortType
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
            .getDefaultPresetSortRule(ContentSortType.Audio)
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )
    private val videoDefaultDisplaySetting =
        repository
            .getDefaultPresetSortRule(ContentSortType.Video)
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )

    private val playlistDefaultDisplaySetting =
        repository
            .getDefaultPresetSortRule(ContentSortType.PlayList)
            .stateIn(
                retainedScope,
                initialValue = null,
                started = WhileSubscribed(5000),
            )

    @Composable
    override fun present(): DefaultSortRuleState {
        val audioDisplaySetting by audioDefaultDisplaySetting.collectAsStateWithLifecycle()
        val videoDisplaySetting by videoDefaultDisplaySetting.collectAsStateWithLifecycle()
        val playlistDefaultDisplaySetting by playlistDefaultDisplaySetting.collectAsStateWithLifecycle()
        val selectedAudioPresetOption =
            remember(audioDisplaySetting) {
                PresetDisplaySetting.entries.firstOrNull {
                    audioDisplaySetting?.isPreset == true && it.tabSortRule == audioDisplaySetting
                }
            }
        val selectedVideoPresetOption =
            remember(videoDisplaySetting) {
                PresetDisplaySetting.entries.firstOrNull {
                    videoDisplaySetting?.isPreset == true && it.tabSortRule == videoDisplaySetting
                }
            }
        val selectedPlaylistPresetOption =
            remember(playlistDefaultDisplaySetting) {
                PresetDisplaySetting.entries.firstOrNull {
                    playlistDefaultDisplaySetting?.isPreset == true && it.tabSortRule == playlistDefaultDisplaySetting
                }
            }

        return DefaultSortRuleState(
            audioDisplaySetting = selectedAudioPresetOption,
            videoDisplaySetting = selectedVideoPresetOption,
            playlistPresetOption = selectedPlaylistPresetOption,
        ) { event ->
            when (event) {
                is DefaultSortRuleStateEvent.ChangeAudioSortRule -> {
                    retainedScope.launch {
                        repository.saveDefaultSortRule(
                            event.type,
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
    val playlistPresetOption: PresetDisplaySetting?,
    val eventSink: (DefaultSortRuleStateEvent) -> Unit = {},
)

internal sealed interface DefaultSortRuleStateEvent {
    data class ChangeAudioSortRule(
        val type: ContentSortType,
        val sortRule: PresetDisplaySetting,
    ) : DefaultSortRuleStateEvent
}
