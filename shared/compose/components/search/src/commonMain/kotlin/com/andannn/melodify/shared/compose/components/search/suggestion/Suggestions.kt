/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.search.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.MediaType
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.ExtraPaddingBottom
import com.andannn.melodify.shared.compose.components.library.item.MediaLibraryItem
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.album_page_title
import melodify.shared.compose.resource.generated.resources.artist_page_title
import melodify.shared.compose.resource.generated.resources.audio_page_title
import melodify.shared.compose.resource.generated.resources.genre_title
import melodify.shared.compose.resource.generated.resources.playlist_page_title
import melodify.shared.compose.resource.generated.resources.video_page_title
import org.jetbrains.compose.resources.stringResource

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
fun Suggestions(
    query: TextFieldState,
    modifier: Modifier = Modifier,
    presenter: Presenter<SuggestionsUiState> = retainSuggestionsPresenter(query = query.text.toString()),
    onConfirmSearch: (String) -> Unit = {},
    onResultItemClick: (MediaItemModel) -> Unit = {},
) {
    SuggestionsContent(
        modifier = modifier,
        state = presenter.present().state,
        onConfirmSearch = onConfirmSearch,
        onResultItemClick = onResultItemClick,
    )
}

@Composable
private fun SuggestionsContent(
    state: SuggestionsState,
    modifier: Modifier = Modifier,
    onConfirmSearch: (String) -> Unit = {},
    onResultItemClick: (MediaItemModel) -> Unit = {},
) {
    LazyColumn(modifier = modifier) {
        when (state) {
            is SuggestionsState.LoadingHistory,
            is SuggestionsState.NoSuggestion,
            is SuggestionsState.LoadingSuggestion,
            -> {
                item {
                    Spacer(modifier = Modifier)
                }
            }

            is SuggestionsState.SuggestionLoaded -> {
                state.suggestions.forEach { (type, suggestions) ->
                    item {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp).padding(top = 24.dp),
                            text = stringResource(type.label()),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    items(
                        suggestions,
                        key = { type to it.id },
                    ) { matchedContent ->
                        MediaLibraryItem(
                            contentId = matchedContent.id,
                            contentType = type,
                            onItemClick = {
                                onResultItemClick(it)
                            },
                        )
                    }
                }
            }

            is SuggestionsState.HistoryLoaded -> {
                items(
                    state.searchWordList,
                    key = { it },
                ) { item ->
                    ListItem(
                        colors =
                            ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                            ),
                        modifier =
                            Modifier.clickable {
                                onConfirmSearch(item)
                            },
                        headlineContent = {
                            Text(item)
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

        item {
            ExtraPaddingBottom()
        }
    }
}

private fun MediaType.label() =
    when (this) {
        MediaType.AUDIO -> Res.string.audio_page_title
        MediaType.VIDEO -> Res.string.video_page_title
        MediaType.ALBUM -> Res.string.album_page_title
        MediaType.ARTIST -> Res.string.artist_page_title
        MediaType.GENRE -> Res.string.genre_title
        MediaType.PLAYLIST -> Res.string.playlist_page_title
    }

@Preview
@Composable
private fun SuggestionUiLoadingHistoryPreview() {
    MelodifyTheme {
        Surface {
            SuggestionsContent(
                state =
                    SuggestionsUiState(
                        SuggestionsState.HistoryLoaded(
                            listOf(
                                "Suggestion 1",
                                "Suggestion 2",
                                "Suggestion 3",
                                "Suggestion 4",
                            ),
                        ),
                    ).state,
            )
        }
    }
}
