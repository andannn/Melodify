package com.andannn.melodify.feature.home

import android.net.Uri
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.common.R
import com.andannn.melodify.core.domain.model.AlbumItemModel
import com.andannn.melodify.core.domain.model.MediaItemModel
import com.andannn.melodify.core.domain.model.ArtistItemModel
import com.andannn.melodify.core.domain.model.AudioItemModel
import com.andannn.melodify.feature.common.component.LargePreviewCard
import com.andannn.melodify.feature.common.component.ListTileItemView
import com.andannn.melodify.core.domain.model.MediaListSource
import com.andannn.melodify.core.domain.model.MediaPreviewMode
import com.andannn.melodify.feature.common.component.ExtraPaddingBottom
import com.andannn.melodify.feature.common.theme.MelodifyTheme
import com.andannn.melodify.feature.home.util.ResourceUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
    onNavigateToPlayList: (id: String, source: MediaListSource) -> Unit,
) {
    fun onMediaItemClick(mediaItem: MediaItemModel) {
        when (mediaItem) {
            is AlbumItemModel -> {
                onNavigateToPlayList(mediaItem.id.toString(), MediaListSource.ALBUM)
            }

            is ArtistItemModel -> {
                onNavigateToPlayList(mediaItem.id.toString(), MediaListSource.ARTIST)
            }

            is AudioItemModel -> {
                homeViewModel.onEvent(HomeUiEvent.OnPlayMusic(mediaItem))
            }
        }
    }

    val state by homeViewModel.state.collectAsState()

    HomeScreen(
        state = state,
        modifier = modifier,
        onEvent = homeViewModel::onEvent,
        onMediaItemClick = ::onMediaItemClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onMediaItemClick: (MediaItemModel) -> Unit = {},
    onEvent: (HomeUiEvent) -> Unit = {},
) {
    val uiState by rememberUpdatedState(state)
    val categories = MediaCategory.entries.toTypedArray()
    val selectedIndex by remember {
        derivedStateOf {
            categories.indexOf(uiState.currentCategory)
        }
    }

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
            ScrollableTabRow(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = selectedIndex,
            ) {
                categories.forEachIndexed { index, item ->
                    Tab(
                        modifier = Modifier,
                        selected = index == selectedIndex,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                        text = @Composable {
                            Text(
                                text = stringResource(id = ResourceUtil.getCategoryResource(item)),
                            )
                        },
                        onClick = {
                            onEvent(HomeUiEvent.OnSelectedCategoryChanged(categories[index]))
                        },
                    )
                }
            }

            val mediaItems by remember {
                derivedStateOf {
                    uiState.mediaItems
                }
            }

            val previewMode by remember {
                derivedStateOf { uiState.previewMode }
            }


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
            key = { it.id },
        ) { item ->
            LargePreviewCard(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 3.dp)
                    .animateItem(),
                artCoverUri = Uri.parse(item.artWorkUri),
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
            key = { it.id },
        ) { item ->
            ListTileItemView(
                modifier =
                Modifier
                    .padding(vertical = 4.dp)
                    .animateItem(),
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
    is AlbumItemModel -> stringResource(id = R.string.track_count, model.trackCount)
    is ArtistItemModel -> stringResource(id = R.string.track_count, model.trackCount)
    else -> ""
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MelodifyTheme(darkTheme = true) {
        HomeScreen(
            state = HomeUiState(
                mediaItems = (1..4).map {
                    AlbumItemModel(
                        id = it.toLong(),
                        name = "Album $it",
                        artWorkUri = "",
                        trackCount = 10
                    )
                }.toImmutableList(),
                currentCategory = MediaCategory.ALBUM,
                previewMode = MediaPreviewMode.GRID_PREVIEW,
            ),
        )
    }
}
