/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.browsable
import com.andannn.melodify.ui.components.library.util.asDataSource
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupControllerImpl
import com.andannn.melodify.ui.components.popup.dialog.ActionDialogContainer
import com.andannn.melodify.ui.components.popup.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.components.search.result.SearchPageView
import com.andannn.melodify.ui.components.search.suggestion.Suggestions

@Composable
fun Search(
    state: SearchUiState,
    modifier: Modifier = Modifier,
) {
    SearchViewContent(
        modifier = modifier,
        inputText = state.inputText,
        isExpand = state.isExpand,
        searchedResult = state.searchState,
        onConfirmSearch = {
            state.eventSink.invoke(SearchUiEvent.OnConfirmSearch(it))
        },
        onPlayAudio = {
            state.eventSink.invoke(SearchUiEvent.OnPlayAudio(it))
        },
        onBackKeyPressed = {
            state.eventSink.invoke(SearchUiEvent.Back)
        },
        onNavigateToLibraryContentList = {
            state.eventSink.invoke(SearchUiEvent.OnNavigateToLibraryContentList(it))
        },
        onInputTextChange = {
            state.eventSink.invoke(SearchUiEvent.OnInputTextChange(it))
        },
        onExpandChange = {
            state.eventSink.invoke(SearchUiEvent.OnExpandChange(it))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchViewContent(
    inputText: String,
    isExpand: Boolean,
    searchedResult: SearchState,
    modifier: Modifier = Modifier,
    onConfirmSearch: (String) -> Unit = {},
    onBackKeyPressed: () -> Unit = {},
    onNavigateToLibraryContentList: (LibraryDataSource) -> Unit = {},
    onPlayAudio: (AudioItemModel) -> Unit = {},
    onInputTextChange: (String) -> Unit = {},
    onExpandChange: (Boolean) -> Unit = {},
) {
    fun onResultItemClick(item: MediaItemModel) {
        if (item.browsable) {
            onNavigateToLibraryContentList(item.asDataSource())
        } else {
            // open player and play this audio item.
            onPlayAudio(item as AudioItemModel)
        }
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(rememberAndSetupSnackBarHostState())
        },
    ) {
        Column {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.focusRequester(focusRequester),
                        query = inputText,
                        onQueryChange = onInputTextChange,
                        expanded = isExpand,
                        onExpandedChange = onExpandChange,
                        onSearch = onConfirmSearch,
                        placeholder = {
                            Text(
                                "Song, Artist, Album",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                onBackKeyPressed.invoke()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = "Back",
                                )
                            }
                        },
                    )
                },
                expanded = isExpand,
                onExpandedChange = onExpandChange,
            ) {
                Suggestions(
                    query = inputText,
                    onConfirmSearch = onConfirmSearch,
                )
            }

            SearchPageView(
                modifier = Modifier,
                searchedResult = searchedResult,
                onResultItemClick = {
                    onResultItemClick(it)
                },
            )
        }
    }

    ActionDialogContainer()
}
