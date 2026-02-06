/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.sort.rule

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.ContentSortType
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun retainDefaultSortRulePresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(repository) {
        DefaultSortRuleSettingPresenter(repository)
    }

@OptIn(ExperimentalMaterial3Api::class)
private class DefaultSortRuleSettingPresenter(
    private val repository: Repository,
) : RetainedPresenter<DefaultSortRuleState>() {
    private val audioDefaultDisplaySetting =
        repository
            .getDefaultPresetSortRule(ContentSortType.Audio)
            .stateInRetainedModel(
                initialValue = null,
            )
    private val videoDefaultDisplaySetting =
        repository
            .getDefaultPresetSortRule(ContentSortType.Video)
            .stateInRetainedModel(
                initialValue = null,
            )

    private val playlistDefaultDisplaySetting =
        repository
            .getDefaultPresetSortRule(ContentSortType.PlayList)
            .stateInRetainedModel(
                initialValue = null,
            )

    @Composable
    override fun present(): DefaultSortRuleState {
        val audioDisplaySetting by audioDefaultDisplaySetting.collectAsStateWithLifecycle()
        val videoDisplaySetting by videoDefaultDisplaySetting.collectAsStateWithLifecycle()
        val playlistDefaultDisplaySetting by playlistDefaultDisplaySetting.collectAsStateWithLifecycle()

        return DefaultSortRuleState(
            audioDisplaySetting = audioDisplaySetting,
            videoDisplaySetting = videoDisplaySetting,
            playlistPresetOption = playlistDefaultDisplaySetting,
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
