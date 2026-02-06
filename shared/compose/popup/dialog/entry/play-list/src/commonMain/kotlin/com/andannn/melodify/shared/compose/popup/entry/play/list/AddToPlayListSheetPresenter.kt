/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.popup.entry.play.list

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun retainedAddToPlayListSheetState(repository: Repository = LocalRepository.current): Presenter<AddToPlayListSheetState> =
    retainPresenter(repository) {
        AddToPlayListSheetPresenter(repository)
    }

@OptIn(ExperimentalMaterial3Api::class)
internal class AddToPlayListSheetPresenter(
    repository: Repository,
) : RetainedPresenter<AddToPlayListSheetState>() {
    private val playListStateFlow =
        repository
            .getAllPlayListFlow()
            .stateInRetainedModel(
                initialValue = emptyList(),
            )

    @Composable
    override fun present(): AddToPlayListSheetState {
        val playLists by playListStateFlow.collectAsStateWithLifecycle()
        return AddToPlayListSheetState(playLists)
    }
}

@Stable
internal data class AddToPlayListSheetState(
    val playLists: List<PlayListItemModel>,
)
