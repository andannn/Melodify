/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.search.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.shared.compose.common.Presenter

/**
 * Content of the search bar when expanded.
 *
 * Show history search if the textFiled is empty.
 * Show possible suggestions if the textFiled is not empty.
 * Show best match Result under suggestions if the query string have 60% or more matched contents.
 *
 * @param modifier Modifier to apply to the content.
 * @param query The current query text.
 */
@Composable
internal fun Suggestions(
    query: TextFieldState,
    modifier: Modifier = Modifier,
    presenter: Presenter<SuggestionsUiState> = retainSuggestionsPresenter(query = query.text.toString()),
    onConfirmSearch: (String) -> Unit = {},
) {
    SuggestionUi(
        modifier = modifier,
        uiState = presenter.present(),
        onConfirmSearch = onConfirmSearch,
    )
}

@Composable
internal fun SuggestionUi(
    modifier: Modifier,
    uiState: SuggestionsUiState,
    onConfirmSearch: (String) -> Unit = {},
) {
    Box(
        modifier = modifier,
    ) {
        SuggestionsContent(
            state = uiState.state,
            onConfirmSearch = onConfirmSearch,
        )
    }
}

@Composable
private fun SuggestionsContent(
    state: SuggestionsState,
    modifier: Modifier = Modifier,
    onConfirmSearch: (String) -> Unit = {},
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        when (state) {
            is SuggestionsState.LoadingHistory,
            is SuggestionsState.NoSuggestion,
            is SuggestionsState.LoadingSuggestion,
            -> {
                Spacer(modifier = Modifier)
            }

            is SuggestionsState.SuggestionLoaded -> {
                state.suggestions.forEach {
                    ListItem(
                        colors =
                            ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                            ),
                        modifier =
                            Modifier.clickable {
                                onConfirmSearch(it)
                            },
                        headlineContent = {
                            Text(it)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "History",
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ArrowOutward,
                                contentDescription = "SearchScreen",
                            )
                        },
                    )
                }
            }

            is SuggestionsState.HistoryLoaded -> {
                state.searchWordList.forEach {
                    ListItem(
                        colors =
                            ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                            ),
                        modifier =
                            Modifier.clickable {
                                onConfirmSearch(it)
                            },
                        headlineContent = {
                            Text(it)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = "History",
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ArrowOutward,
                                contentDescription = "SearchScreen",
                            )
                        },
                    )
                }
            }
        }
    }
}
