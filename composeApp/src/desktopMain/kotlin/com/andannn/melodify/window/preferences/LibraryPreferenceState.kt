/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.window.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.model.DialogAction
import com.andannn.melodify.model.DialogId
import com.andannn.melodify.ui.core.LocalPopupController
import com.andannn.melodify.ui.core.LocalRepository
import com.andannn.melodify.ui.core.PopupController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
internal fun rememberLibraryPreferenceState(
    scope: CoroutineScope = rememberCoroutineScope(),
    popUpController: PopupController = LocalPopupController.current,
    repository: Repository = LocalRepository.current,
) = remember(
    scope,
    popUpController,
    repository,
) {
    LibraryPreferenceState(
        scope,
        popUpController,
        repository.userPreferenceRepository,
    )
}

class LibraryPreferenceState(
    private val scope: CoroutineScope,
    private val popUpController: PopupController,
    private val userPreferenceRepository: UserPreferenceRepository,
) {
    private val libraryPathFlow =
        userPreferenceRepository.userSettingFlow.map {
            it.libraryPath
        }

    var libraryPath: Set<String> by mutableStateOf(emptySet())
        private set

    init {
        scope.launch {
            libraryPathFlow.collect {
                libraryPath = it
            }
        }
    }

    fun onAddLibraryButtonClick() {
        scope.launch {
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
        scope.launch {
            userPreferenceRepository.deleteLibraryPath(pathToDelete)
        }
    }
}
