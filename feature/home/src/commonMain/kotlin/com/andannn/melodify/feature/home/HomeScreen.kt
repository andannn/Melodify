@file:OptIn(ExperimentalFoundationApi::class)

package com.andannn.melodify.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.feature.common.component.LargePreviewCard
import com.andannn.melodify.feature.common.component.ListTileItemView
import com.andannn.melodify.core.data.model.MediaListSource
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.feature.common.component.ExtraPaddingBottom
import com.andannn.melodify.feature.common.theme.MelodifyTheme
import com.andannn.melodify.feature.common.util.browsableOrPlayable
import com.andannn.melodify.feature.common.util.getCategoryResource
import com.andannn.melodify.feature.common.util.getUiRetainedScope
import com.andannn.melodify.feature.common.util.key
import com.andannn.melodify.feature.drawer.DrawerController
import com.andannn.melodify.feature.message.MessageController
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    scope: Scope? = getUiRetainedScope(),
    homeViewModel: HomeViewModel = koinViewModel {
        parametersOf(
            scope?.get<DrawerController>(),
            scope?.get<MessageController>(),
        )
    },
    onNavigateToPlayList: (id: String, source: MediaListSource) -> Unit,
    onNavigateCustomTabSetting: () -> Unit,
) {
    fun onMediaItemClick(mediaItem: MediaItemModel) {
        when (mediaItem) {
            is AlbumItemModel -> {
                onNavigateToPlayList(mediaItem.id, MediaListSource.ALBUM)
            }

            is ArtistItemModel -> {
                onNavigateToPlayList(mediaItem.id, MediaListSource.ARTIST)
            }

            is AudioItemModel -> {
                homeViewModel.onEvent(HomeUiEvent.OnMusicItemClick(mediaItem))
            }

            is GenreItemModel -> {
                onNavigateToPlayList(mediaItem.id, MediaListSource.GENRE)
            }

            is PlayListItemModel -> {
                onNavigateToPlayList(mediaItem.id, MediaListSource.PLAY_LIST)
            }
        }
    }

    val state by homeViewModel.state.collectAsState()

    HomeScreen(
        state = state,
        modifier = modifier,
        onEvent = homeViewModel::onEvent,
        onMediaItemClick = ::onMediaItemClick,
        onSettingButtonClick = onNavigateCustomTabSetting
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onMediaItemClick: (MediaItemModel) -> Unit = {},
    onSettingButtonClick: () -> Unit = {},
    onEvent: (HomeUiEvent) -> Unit = {},
) {
    val uiState by rememberUpdatedState(state)
    val categories by rememberUpdatedState(state.customTabList)
    val selectedIndex by rememberUpdatedState(state.selectedIndex)

    val scrollBehavior = enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors().run {
                    copy(scrolledContainerColor = containerColor)
                },
                title = {
                    Text(text = "Melodify")
                },
                actions = {
                    IconButton(
                        onClick = onSettingButtonClick,
                        content = {
                            Icon(Icons.Rounded.Settings, contentDescription = "")
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        Column(
            modifier =
            Modifier
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize(),
        ) {
            if (categories.isNotEmpty()) {
                ScrollableTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedIndex,
                ) {
                    categories.forEachIndexed { index, item ->
                        Tab(
                            selected = index == selectedIndex,
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                            text = @Composable {
                                Text(
                                    text = getCategoryResource(item),
                                )
                            },
                            onClick = {
                                onEvent(HomeUiEvent.OnSelectedCategoryChanged(index))
                            },
                        )
                    }
                }
            }

            val mediaItems by rememberUpdatedState(uiState.mediaItems)
            val previewMode by rememberUpdatedState(uiState.previewMode)

            when (previewMode) {
                MediaPreviewMode.GRID_PREVIEW -> {
                    val gridLayoutState =
                        rememberSaveable(selectedIndex, saver = LazyGridState.Saver) {
                            LazyGridState()
                        }
                    LazyGridContent(
                        state = gridLayoutState,
                        modifier =
                        Modifier.fillMaxSize(),
                        layoutToggleButton = {
                            LayoutToggleButton(
                                previewMode = previewMode,
                                onClick = {
                                    onEvent(HomeUiEvent.OnTogglePreviewMode)
                                }
                            )
                        },
                        mediaItems = mediaItems,
                        onClick = onMediaItemClick,
                        onLongPress = {
                            onEvent(HomeUiEvent.OnShowItemOption(it))
                        }
                    )
                }

                MediaPreviewMode.LIST_PREVIEW -> {
                    val listLayoutState =
                        rememberSaveable(selectedIndex, saver = LazyListState.Saver) {
                            LazyListState()
                        }
                    LazyListContent(
                        modifier =
                        Modifier.fillMaxSize(),
                        state = listLayoutState,
                        layoutToggleButton = {
                            LayoutToggleButton(
                                previewMode = previewMode,
                                onClick = {
                                    onEvent(HomeUiEvent.OnTogglePreviewMode)
                                }
                            )
                        },
                        mediaItems = mediaItems,
                        onMusicItemClick = onMediaItemClick,
                        onShowMusicItemOption = {
                            onEvent(HomeUiEvent.OnShowItemOption(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun <T : MediaItemModel> LazyGridContent(
    mediaItems: ImmutableList<T>,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    layoutToggleButton: @Composable () -> Unit = {},
    onClick: (T) -> Unit = {},
    onLongPress: (T) -> Unit = {}
) {
    val hapticFeedBack = LocalHapticFeedback.current
    LazyVerticalGrid(
        state = state,
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(180.dp),
    ) {
        item(span = { GridItemSpan(2) }) { layoutToggleButton() }

        items(
            items = mediaItems,
            key = { it.key },
        ) { item ->
            LargePreviewCard(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 3.dp)
                    .animateItem(),
                artCoverUri = item.artWorkUri,
                title = item.name,
                subTitle = subTitle(item),
                onClick = {
                    onClick.invoke(item)
                },
                onLongClick = {
                    hapticFeedBack.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress.invoke(item)
                },
            )
        }

        item { ExtraPaddingBottom() }
    }
}

@Composable
private fun <T : MediaItemModel> LazyListContent(
    mediaItems: ImmutableList<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    layoutToggleButton: @Composable () -> Unit = {},
    onMusicItemClick: (T) -> Unit = {},
    onShowMusicItemOption: (T) -> Unit = {},
) {
    LazyColumn(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 5.dp),
    ) {
        item {
            layoutToggleButton()
        }
        items(
            items = mediaItems,
            key = { it.key },
        ) { item ->
            Napier.d("ListTileItemView: $item")
            ListTileItemView(
                modifier =
                Modifier
                    .padding(vertical = 4.dp)
                    .animateItem(),
                playable = item.browsableOrPlayable,
                isActive = false,
                albumArtUri = item.artWorkUri,
                title = item.name,
                subTitle = subTitle(item),
                onMusicItemClick = {
                    onMusicItemClick.invoke(item)
                },
                onOptionButtonClick = {
                    onShowMusicItemOption(item)
                },
            )
        }

        item { ExtraPaddingBottom() }
    }
}

@Composable
fun LayoutToggleButton(
    previewMode: MediaPreviewMode,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier
                .padding(end = 10.dp),
            onClick = {
                onClick()
            }
        ) {
            when (previewMode) {
                MediaPreviewMode.GRID_PREVIEW -> {
                    Icon(
                        Icons.AutoMirrored.Rounded.List,
                        contentDescription = ""
                    )
                }

                MediaPreviewMode.LIST_PREVIEW -> {
                    Icon(
                        Icons.Rounded.Apps,
                        contentDescription = ""
                    )
                }
            }
        }
    }

}


@Composable
private fun subTitle(
    model: MediaItemModel
): String = when (model) {
    is AudioItemModel -> model.artist
    is AlbumItemModel,
    is PlayListItemModel,
    is ArtistItemModel -> stringResource(Res.string.track_count, model.trackCount.toString())

    is GenreItemModel -> ""
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MelodifyTheme(darkTheme = true) {
        HomeScreen(
            state = HomeUiState(
                mediaItems = (1..4).map {
                    AlbumItemModel(
                        id = it.toString(),
                        name = "Album $it",
                        artWorkUri = "",
                        trackCount = 10
                    )
                }.toImmutableList(),
                previewMode = MediaPreviewMode.GRID_PREVIEW,
            ),
        )
    }
}
