/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.popup.entry.alert.InvalidPathAlert
import com.andannn.melodify.shared.compose.popup.entry.play.list.InputDialogResult
import com.andannn.melodify.shared.compose.popup.entry.play.list.NewPlayListPopup
import io.github.andannn.popup.PopupHostState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
internal fun retainLibraryPreferenceState(
    popUpController: PopupHostState = LocalPopupHostState.current,
    repository: Repository = LocalRepository.current,
) = retainPresenter(
    popUpController,
    repository,
) {
    LibraryPreferencePresenter(
        popUpController,
        repository,
    )
}

@Stable
class LibraryPreferenceUiState(
    val libraryPath: Set<String>,
    val eventSink: (LibraryPreferenceUiEvent) -> Unit,
)

sealed interface LibraryPreferenceUiEvent {
    data object OnAddLibraryButtonClick : LibraryPreferenceUiEvent

    data class OnDeleteLibraryPath(
        val path: String,
    ) : LibraryPreferenceUiEvent
}

class LibraryPreferencePresenter(
    private val popUpController: PopupHostState,
    private val repository: Repository,
) : RetainedPresenter<LibraryPreferenceUiState>() {
    private val libraryPathFlow =
        repository.userSettingFlow
            .map {
                it.libraryPath
            }.stateIn(
                retainedScope,
                initialValue = emptySet(),
                started = kotlinx.coroutines.flow.SharingStarted.Lazily,
            )

    @Composable
    override fun present(): LibraryPreferenceUiState {
        val libraryPath by libraryPathFlow.collectAsStateWithLifecycle()
        return LibraryPreferenceUiState(
            libraryPath = libraryPath,
        ) { event ->
            when (event) {
                is LibraryPreferenceUiEvent.OnAddLibraryButtonClick -> onAddLibraryButtonClick()
                is LibraryPreferenceUiEvent.OnDeleteLibraryPath -> onDeleteLibraryPath(event.path)
            }
        }
    }

    private fun onAddLibraryButtonClick() {
        retainedScope.launch {
            val result = popUpController.showDialog(NewPlayListPopup)

            if (result is InputDialogResult.Accept) {
                val success = repository.addLibraryPath(result.input)

                if (!success) {
                    popUpController.showDialog(InvalidPathAlert)
                }
            }
        }
    }

    private fun onDeleteLibraryPath(pathToDelete: String) {
        retainedScope.launch {
            repository.deleteLibraryPath(pathToDelete)
        }
    }
}
