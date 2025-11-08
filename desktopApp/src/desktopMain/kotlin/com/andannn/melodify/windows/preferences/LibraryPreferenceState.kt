/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.PopupController
import com.andannn.melodify.ui.core.ScopedPresenter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
internal fun rememberLibraryPreferenceState(
    popUpController: PopupController = LocalPopupController.current,
    repository: Repository = LocalRepository.current,
) = retain(
    popUpController,
    repository,
) {
    LibraryPreferenceState(
        popUpController,
        repository.userPreferenceRepository,
    )
}

class LibraryPreferenceState(
    private val popUpController: PopupController,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ScopedPresenter<Unit>() {
    private val libraryPathFlow =
        userPreferenceRepository.userSettingFlow.map {
            it.libraryPath
        }

    var libraryPath: Set<String> by mutableStateOf(emptySet())
        private set

    init {
        launch {
            libraryPathFlow.collect {
                libraryPath = it
            }
        }
    }

    fun onAddLibraryButtonClick() {
        launch {
            val result = popUpController.showDialog(DialogId.NewPlayListDialog)

            if (result is DialogAction.InputDialog.Accept) {
                val success = userPreferenceRepository.addLibraryPath(result.input)

                if (!success) {
                    popUpController.showDialog(DialogId.InvalidPathAlert)
                }
            }
        }
    }

    fun onDeleteLibraryPath(pathToDelete: String) {
        launch {
            userPreferenceRepository.deleteLibraryPath(pathToDelete)
        }
    }

    @Composable
    override fun present() {
    }
}
