package com.andannn.melodify.ui.components.search.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.components.search.searchedItem.MediaItemWithOptionAction

/**
 * Content of the search bar when expanded.
 *
 * Show history search if the textFiled is empty.
 * Show possible suggestions if the textFiled is not empty.
 * Show best match Result under suggestions if the query string have 60% or more matched contents.
 *
 * @param modifier Modifier to apply to the content.
 * @param query The current query text.
 * @param onConfirmSearch Called when the user submits the query.
 */
@Composable
internal fun SuggestionsView(
    query: String,
    modifier: Modifier = Modifier,
    onConfirmSearch: (String) -> Unit = {},
) {
    val state by rememberSuggestionsStateHolder(query = query).state.collectAsState()

    Box(
        modifier = modifier
    ) {
        SuggestionsContent(
            state = state,
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
            is SuggestionsState.LoadingSuggestion -> Spacer(modifier = Modifier)

            is SuggestionsState.SuggestionLoaded -> {
                state.suggestions.forEach {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.clickable {
                            onConfirmSearch(it)
                        },
                        headlineContent = {
                            Text(it)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "History"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ArrowOutward,
                                contentDescription = "Search"
                            )
                        }
                    )
                }

                if (state.bestMatchedItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "Quick result",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.bestMatchedItems.forEach {
                    MediaItemWithOptionAction(
                        modifier = Modifier,
                        mediaItemModel = it,
                        onItemClick = {}
                    )
                }
            }

            is SuggestionsState.HistoryLoaded -> {
                state.searchWordList.forEach {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.clickable {
                            onConfirmSearch(it)
                        },
                        headlineContent = {
                            Text(it)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = "History"
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Rounded.ArrowOutward,
                                contentDescription = "Search"
                            )
                        }
                    )
                }
            }
        }
    }
}