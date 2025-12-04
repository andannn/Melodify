/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.model.ShortcutItem
import com.andannn.melodify.model.toDataSource
import com.andannn.melodify.ui.core.Navigator
import com.andannn.melodify.ui.core.Presenter
import com.andannn.melodify.ui.core.rememberAndSetupSnackBarHostState
import com.andannn.melodify.ui.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.widgets.ShortcutItem

@Composable
fun Library(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    presenter: Presenter<LibraryState> = retainLibraryPresenter(navigator),
) {
    val state = presenter.present()
    LibraryContent(
        modifier = modifier,
        onBackKeyPressed = {
            state.evenSink.invoke(LibraryUiEvent.Back)
        },
        navigateToSearch = {
            state.evenSink.invoke(LibraryUiEvent.OnNavigateToSearch)
        },
        onShortcutItemClick = { item ->
            state.evenSink.invoke(
                LibraryUiEvent.OnNavigateToLibraryContentList(item.toDataSource()),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryContent(
    modifier: Modifier = Modifier,
    onBackKeyPressed: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    onShortcutItemClick: (ShortcutItem) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(rememberAndSetupSnackBarHostState())
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackKeyPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text(text = "Library")
                },
                actions = {
                    IconButton(onClick = navigateToSearch) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "SearchScreen",
                        )
                    }
                },
            )
        },
    ) {
        LazyVerticalGrid(
            modifier =
                Modifier
                    .padding(it)
                    .padding(4.dp),
            columns = GridCells.Adaptive(minSize = 180.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = ShortcutItem.entries.toTypedArray(),
                key = { it },
            ) { item ->
                ShortcutItem(
                    shortcutItem = item,
                    onClick = {
                        onShortcutItemClick(item)
                    },
                )
            }

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                },
            ) {
                ExtraPaddingBottom()
            }
        }
    }
}
