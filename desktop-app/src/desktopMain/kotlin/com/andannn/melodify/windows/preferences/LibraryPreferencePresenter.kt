/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.RetainedPresenter
import com.andannn.melodify.ui.core.retainPresenter
import com.andannn.melodify.ui.core.showDialogAndWaitAction
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
internal fun retainLibraryPreferenceState(
    popUpController: PopupController = LocalPopupController.current,
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
    private val popUpController: PopupController,
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
            val result = popUpController.showDialogAndWaitAction(DialogId.NewPlayListDialog)

            if (result is DialogAction.InputDialog.Accept) {
                val success = repository.addLibraryPath(result.input)

                if (!success) {
                    popUpController.showDialogAndWaitAction(DialogId.InvalidPathAlert)
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
