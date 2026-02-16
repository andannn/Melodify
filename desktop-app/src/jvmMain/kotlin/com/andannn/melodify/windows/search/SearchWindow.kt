/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.windows.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.shared.compose.common.LocalNavigationRequestEventSink
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.NavigationRequestEventSink
import com.andannn.melodify.shared.compose.components.search.result.SearchResultPage
import com.andannn.melodify.shared.compose.popup.LocalPopupHostState
import com.andannn.melodify.shared.compose.usecase.playOrGoToBrowsable
import com.andannn.melodify.windows.CustomMenuBar
import com.andannn.melodify.windows.WindowNavigator
import com.andannn.melodify.windows.common.CommonActionDialog
import com.andannn.melodify.windows.handleMenuEvent
import io.github.andannn.RetainedModel
import io.github.andannn.popup.PopupHostState
import io.github.andannn.retainRetainedModel
import kotlinx.coroutines.launch
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.search_your_library
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchWindow(
    navigator: WindowNavigator,
    onCloseRequest: () -> Unit,
) {
    Window(
        state = rememberWindowState(),
        onCloseRequest = onCloseRequest,
        title = stringResource(Res.string.search_your_library),
    ) {
        CustomMenuBar(navigator::handleMenuEvent)

        SearchWindowContent()

        CommonActionDialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchWindowContent(
    modifier: Modifier = Modifier,
    model: RetainedSearchModel = retainRetainSearchModel(),
) {
    val textInput = rememberTextFieldState()
    Column(modifier = modifier) {
        val inputField =
            @Composable {
                SearchBarDefaults.InputField(
                    textFieldState = textInput,
                    searchBarState = rememberSearchBarState(),
                    onSearch = {
                    },
                    placeholder = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = stringResource(Res.string.search_your_library))
                        }
                    },
                )
            }
        SearchBar(
            modifier =
                Modifier.fillMaxWidth().padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ),
            state = rememberSearchBarState(),
            inputField = inputField,
        )

        SearchResultPage(
            query = textInput.text.toString(),
            onResultItemClick = {
                model.onClickResultModel(it)
            },
        )
    }
}

@Composable
private fun retainRetainSearchModel(
    navigationRequestEventSink: NavigationRequestEventSink = LocalNavigationRequestEventSink.current,
    popupHostState: PopupHostState = LocalPopupHostState.current,
    repository: Repository = LocalRepository.current,
) = retainRetainedModel(
    navigationRequestEventSink,
    popupHostState,
    repository,
) {
    RetainedSearchModel(
        navigationRequestEventSink,
        popupHostState,
        repository,
    )
}

private class RetainedSearchModel(
    private val navigationRequestEventSink: NavigationRequestEventSink,
    private val popupHostState: PopupHostState,
    private val repository: Repository,
) : RetainedModel() {
    fun onClickResultModel(item: MediaItemModel) {
        context(navigationRequestEventSink, popupHostState, repository) {
            retainedScope.launch {
                playOrGoToBrowsable(item)
            }
        }
    }
}
