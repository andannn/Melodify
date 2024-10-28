package com.andannn.melodify.feature.playList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.feature.common.component.ExtraPaddingBottom
import com.andannn.melodify.feature.common.component.ListTileItemView
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.MediaListSource
import com.andannn.melodify.feature.common.theme.MelodifyTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlayListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayListViewModel = koinViewModel(),
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.state.collectAsState()

    val source =
        remember {
            viewModel.mediaListSource
        }

    when (source) {
        MediaListSource.ALBUM,
        MediaListSource.PLAY_LIST-> {
            HeaderPlayListContent(
                modifier = modifier,
                header = uiState.headerInfoItem ?: AlbumItemModel.DEFAULT,
                audioList = uiState.audioList,
                showTrackNum = source == MediaListSource.ALBUM,
                playingMediaItem = uiState.playingMediaItem,
                onEvent = viewModel::onEvent,
                onBackPressed = onBackPressed,
            )
        }

        MediaListSource.ARTIST,
        MediaListSource.GENRE -> {
            CommonPlayListContent(
                modifier = modifier,
                header = uiState.headerInfoItem ?: ArtistItemModel.DEFAULT,
                audioList = uiState.audioList,
                playingMediaItem = uiState.playingMediaItem,
                onEvent = viewModel::onEvent,
                onBackPressed = onBackPressed,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommonPlayListContent(
    header: MediaItemModel,
    audioList: ImmutableList<AudioItemModel>,
    playingMediaItem: AudioItemModel?,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onEvent: (PlayListEvent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = header.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        LazyColumn(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            items(
                items = audioList,
                key = { item -> item.id },
            ) { item: AudioItemModel ->
                ListTileItemView(
                    modifier = Modifier.padding(vertical = 4.dp).animateItemPlacement(),
                    isActive = playingMediaItem?.id == item.id,
                    albumArtUri = item.artWorkUri,
                    title = item.name,
                    showTrackNum = false,
                    subTitle = item.artist,
                    trackNum = item.cdTrackNumber,
                    onMusicItemClick = {
                        onEvent(
                            PlayListEvent.OnStartPlayAtIndex(
                                index = audioList.indexOf(item),
                            ),
                        )
                    },
                    onOptionButtonClick = {
                        onEvent.invoke(PlayListEvent.OnOptionClick(item))
                    },
                )
            }

            item { ExtraPaddingBottom() }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeaderPlayListContent(
    header: MediaItemModel,
    audioList: ImmutableList<AudioItemModel>,
    playingMediaItem: AudioItemModel?,
    modifier: Modifier = Modifier,
    showTrackNum: Boolean = true,
    onBackPressed: () -> Unit = {},
    onEvent: (PlayListEvent) -> Unit = {},
) {
    var appBarHeight by remember {
        mutableIntStateOf(0)
    }

    val appBarHeightDp =
        with(LocalDensity.current) {
            appBarHeight.toDp()
        }

    var headerHeight by remember {
        mutableIntStateOf(0)
    }

    /**
     * Animation Start Edge: Scroll 0:
     * Status bar is transparent, App bar is transparent. PlayBox alpha is 1.
     *
     * Animation End Edge: Scroll x is Height of PlayBox:
     * Status bar is surface color, App bar is surface color. PlayBox alpha is 0.
     *
     * PlayBox alpha need to be animated from 0 to 1.
     * App bar title need to be animated visible.
     */

    val lazyListState = rememberLazyListState()

    val headerScrollFactor by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0 && appBarHeight != 0 && headerHeight != 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat().div(headerHeight)
                    .coerceIn(0f, 1f)
            } else if (lazyListState.firstVisibleItemIndex >= 1) {
                1f
            } else {
                0f
            }
        }
    }

    val isHeaderVisible by remember {
        derivedStateOf {
            headerScrollFactor != 1f
        }
    }

    val isAppbarTitleVisible by remember {
        derivedStateOf {
            headerScrollFactor >= 0.7f
        }
    }

    Scaffold(
        modifier = modifier,
    ) { padding ->
        // ignore warning.
        padding

        LazyColumn(
            state = lazyListState,
        ) {
            item {
                Column {
                    Spacer(
                        modifier = Modifier.padding(top = appBarHeightDp),
                    )
                    PlayListHeader(
                        modifier =
                        Modifier
                            .padding(10.dp)
                            .graphicsLayer {
                                alpha = 1 - headerScrollFactor
                            }
                            .onSizeChanged {
                                headerHeight = it.height
                            },
                        coverArtUri = header.artWorkUri,
                        title = header.name,
                        trackCount = header.trackCount,
                        onPlayAllButtonClick = {
                            onEvent(
                                PlayListEvent.OnPlayAllButtonClick
                            )
                        },
                        onShuffleButtonClick = {
                            onEvent(
                                PlayListEvent.OnShuffleButtonClick
                            )
                        },
                        onOptionClick = {
                            onEvent(PlayListEvent.OnHeaderOptionClick)
                        }
                    )
                }
            }

            items(
                items = audioList,
                key = { it.id },
            ) { item ->
                ListTileItemView(
                    modifier = Modifier.padding(vertical = 4.dp).animateItemPlacement(),
                    isActive = playingMediaItem?.id == item.id,
                    albumArtUri = header.artWorkUri,
                    title = item.name,
                    showTrackNum = showTrackNum,
                    subTitle = item.artist,
                    trackNum = item.cdTrackNumber,
                    onMusicItemClick = {
                        onEvent(
                            PlayListEvent.OnStartPlayAtIndex(
                                index = audioList.indexOf(item),
                            ),
                        )
                    },
                    onOptionButtonClick = {
                        onEvent(
                            PlayListEvent.OnOptionClick(
                                item,
                            ),
                        )
                    },
                )
            }

            item { ExtraPaddingBottom() }
        }

        CustomAppTopBar(
            modifier = Modifier.onSizeChanged {
                appBarHeight = it.height
            },
            isBackgroundTransparent = isHeaderVisible,
            isTitleVisible = isAppbarTitleVisible,
            title = header.name,
            onBackPressed = onBackPressed,
        )
    }
}

@Composable
private fun CustomAppTopBar(
    title: String,
    isTitleVisible: Boolean,
    isBackgroundTransparent: Boolean,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Row(
        modifier =
        modifier
            .background(color = if (isBackgroundTransparent) Color.Transparent else MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackPressed,
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
            )
        }
        AnimatedVisibility(
            visible = isTitleVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun PlayListScreenContentPreview() {
    MelodifyTheme {
        CommonPlayListContent(
            header = ArtistItemModel.DEFAULT,
            audioList = listOf(
                AudioItemModel(
                    id = "0",
                    name = "Song 1",
                    modifiedDate = 0,
                    album = "Album 1",
                    albumId = "0",
                    artist = "Artist 1",
                    artistId = "0",
                    cdTrackNumber = 1,
                    discNumberIndex = 0,
                    artWorkUri = "",
                ),
                AudioItemModel(
                    id = "1",
                    name = "Song 2",
                    modifiedDate = 0,
                    album = "Album 1",
                    albumId = "0",
                    artist = "Artist 1",
                    artistId = "0",
                    cdTrackNumber = 2,
                    discNumberIndex = 0,
                    artWorkUri = "",
                ),
            ).toImmutableList(),
            playingMediaItem = null
        )
    }
}


@Preview
@Composable
private fun AlbumPlayListContentPreview() {
    MelodifyTheme {
        HeaderPlayListContent(
            header = AlbumItemModel(
                id = "0",
                name = "Album 1",
                trackCount = 2,
                artWorkUri = "",
            ),
            audioList = listOf(
                AudioItemModel(
                    id = "0",
                    name = "Song 1",
                    modifiedDate = 0,
                    album = "Album 1",
                    albumId = "0",
                    artist = "Artist 1",
                    artistId = "0",
                    cdTrackNumber = 1,
                    discNumberIndex = 0,
                    artWorkUri = "",
                ),
                AudioItemModel(
                    id = "1",
                    name = "Song 2",
                    modifiedDate = 0,
                    album = "Album 1",
                    albumId = "0",
                    artist = "Artist 1",
                    artistId = "0",
                    cdTrackNumber = 2,
                    discNumberIndex = 0,
                    artWorkUri = "",
                ),
            ).toImmutableList(),
            playingMediaItem = null
        )
    }
}


