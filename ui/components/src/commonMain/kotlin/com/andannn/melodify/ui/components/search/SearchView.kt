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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.browsable
import com.andannn.melodify.ui.components.library.util.asDataSource
import com.andannn.melodify.ui.components.librarycontentlist.LibraryDataSource
import com.andannn.melodify.ui.components.search.result.SearchPageView
import com.andannn.melodify.ui.components.search.suggestion.SuggestionsView

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    stateHolder: SearchUiStateHolder = rememberSearchUiState(),
    onBackKeyPressed: () -> Unit = {},
    onNavigateToLibraryContentList: (LibraryDataSource) -> Unit = {},
) {
    val searchedResult = stateHolder.resultListFlow.collectAsState()

    SearchViewContent(
        modifier = modifier,
        searchedResult = searchedResult.value,
        onConfirmSearch = stateHolder::onConfirmSearch,
        onPlayAudio = stateHolder::onPlayAudio,
        onBackKeyPressed = onBackKeyPressed,
        onNavigateToLibraryContentList = onNavigateToLibraryContentList,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchViewContent(
    modifier: Modifier = Modifier,
    searchedResult: SearchState,
    onConfirmSearch: (String) -> Unit = {},
    onBackKeyPressed: () -> Unit = {},
    onNavigateToLibraryContentList: (LibraryDataSource) -> Unit = {},
    onPlayAudio: (AudioItemModel) -> Unit = {}
) {
    fun onResultItemClick(item: MediaItemModel) {
        if (item.browsable) {
            onNavigateToLibraryContentList(item.asDataSource())
        } else {
            // open player and play this audio item.
            onPlayAudio(item as AudioItemModel)
        }
    }

    Scaffold(
        modifier = modifier
    ) {
        Column {
            var expanded by rememberSaveable { mutableStateOf(true) }
            var text by rememberSaveable { mutableStateOf("") }

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = {
                            text = it
                        },
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = it
                        },
                        onSearch = {
                            if (text.isNotEmpty()) {
                                onConfirmSearch(text)
                                expanded = false
                            }
                        },
                        placeholder = {
                            Text(
                                "Song, Artist, Album",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            IconButton(onClick = onBackKeyPressed) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                },
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
            ) {
                SuggestionsView(
                    query = text,
                    onBestMatchedItemClicked = { item ->
                        expanded = false
                        if (item.browsable) {
                            onNavigateToLibraryContentList(item.asDataSource())
                        }
                    },
                    onConfirmSearch = {
                        text = it
                        expanded = false
                        onConfirmSearch(it)
                    },
                )
            }

            SearchPageView(
                modifier = Modifier,
                searchedResult = searchedResult,
                onResultItemClick = {
                    expanded = false
                    onResultItemClick(it)
                }
            )
        }
    }
}


