package com.andannn.melodify.ui.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.common.widgets.ListTileItemView
import com.andannn.melodify.ui.components.search.suggestion.SuggestionsView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    stateHolder: SearchUiStateHolder = rememberSearchUiState(),
    onBackKeyPressed: () -> Unit = {}
) {
    val searchedResult = stateHolder.searchedResult

    SearchViewContent(
        modifier = modifier,
        searchedResult = searchedResult.toImmutableList(),
        onConfirmSearch = stateHolder::onConfirmSearch,
        onBackKeyPressed = onBackKeyPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchViewContent(
    modifier: Modifier = Modifier,
    searchedResult: ImmutableList<MediaItemModel>,
    onConfirmSearch: (String) -> Unit = {},
    onBackKeyPressed: () -> Unit = {}
) {
    val albumItems by rememberUpdatedState(searchedResult.filterIsInstance<AlbumItemModel>())
    val audioItems by rememberUpdatedState(searchedResult.filterIsInstance<AudioItemModel>())
    val artistItems by rememberUpdatedState(searchedResult.filterIsInstance<ArtistItemModel>())

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
                    onConfirmSearch = {
                        text = it
                        expanded = false
                        onConfirmSearch(it)
                    },
                    onClickBestMatchedItem = {

                    }
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                if (albumItems.isNotEmpty()) {
                    item {
                        Surface {
                            Text("Album")
                        }
                    }

                    items(
                        items = albumItems,
                        key = { it.id }
                    ) {
                        Surface {
                            Text(it.toString())
                        }
                    }
                }

                if (audioItems.isNotEmpty()) {
                    item {
                        Surface {
                            Text("Audios")
                        }
                    }

                    items(
                        items = audioItems,
                        key = { it.id }
                    ) {
                        ListTileItemView(
                            title = it.name,
                            albumArtUri = it.artWorkUri,
                            onOptionButtonClick = {}
                        )
                    }
                }

                if (artistItems.isNotEmpty()) {
                    item {
                        Text("Artists")
                    }

                    items(
                        items = artistItems,
                        key = { it.id }
                    ) {
                        Surface {
                            Text(it.toString())
                        }
                    }
                }
            }
        }
    }
}


