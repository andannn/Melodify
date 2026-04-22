/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.routes.home.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andannn.melodify.domain.Repository
import com.andannn.melodify.domain.model.AlbumItemModel
import com.andannn.melodify.domain.model.ArtistItemModel
import com.andannn.melodify.domain.model.GenreItemModel
import com.andannn.melodify.domain.model.MediaItemModel
import com.andannn.melodify.domain.model.PlayListItemModel
import com.andannn.melodify.domain.model.TabKind
import com.andannn.melodify.domain.model.VideoBucketModel
import com.andannn.melodify.shared.compose.common.LocalRepository
import com.andannn.melodify.shared.compose.common.RetainedPresenter
import com.andannn.melodify.shared.compose.common.retainPresenter
import com.andannn.melodify.shared.compose.common.stateInRetainedModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.album_page_title
import melodify.shared.compose.resource.generated.resources.artist_page_title
import melodify.shared.compose.resource.generated.resources.audio_page_title
import melodify.shared.compose.resource.generated.resources.genre_title
import melodify.shared.compose.resource.generated.resources.playlist_page_title
import melodify.shared.compose.resource.generated.resources.preset
import melodify.shared.compose.resource.generated.resources.video_buckets_page_title
import melodify.shared.compose.resource.generated.resources.video_page_title
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TabSelector(
    modifier: Modifier = Modifier,
    state: TabSelectorState,
) {
    val selectedIndex by rememberUpdatedState(
        TabSelectorKind.entries.indexOf(state.selectedKind),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ScrollableTabRow(
            selectedTabIndex = TabSelectorKind.entries.indexOf(state.selectedKind),
        ) {
            TabSelectorKind.entries.forEachIndexed { index, item ->
                Tab(
                    modifier = Modifier,
                    selected = index == selectedIndex,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                    text = @Composable {
                        Text(
                            text = stringResource(item.titleResource()),
                        )
                    },
                    onClick = {
                        state.eventSink.invoke(TabSelectorUiEvent.OnChangeSelectedTabKind(item))
                    },
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.currentTabs, key = { it.externalId to it.tabKind }) { tabSource ->
                TabSourceItem(tabSource = tabSource)
            }
        }
    }
}

internal enum class TabSelectorKind {
    // Contains All Video and All Audio tab.
    PRESET,
    ALBUM,
    ARTIST,
    GENRE,
    PLAYLIST,
    VIDEO_BUCKET,
}

@Composable
internal fun retainTabSelectorPresenter(repository: Repository = LocalRepository.current) =
    retainPresenter(repository) {
        TabSelectorPresenter(repository)
    }

internal data class TabSelectorState(
    val selectedKind: TabSelectorKind,
    val currentTabs: List<TabSource>,
    val eventSink: (TabSelectorUiEvent) -> Unit,
)

internal data class TabSource(
    val label: String,
    val tabKind: TabKind,
    val externalId: Long = -1L,
)

internal sealed interface TabSelectorUiEvent {
    data class OnChangeSelectedTabKind(
        val kind: TabSelectorKind,
    ) : TabSelectorUiEvent
}

internal class TabSelectorPresenter(
    val repository: Repository,
) : RetainedPresenter<TabSelectorState>() {
    internal val selectedKindStateFlow = MutableStateFlow(TabSelectorKind.PRESET)

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val currentTabs =
        selectedKindStateFlow
            .flatMapLatest { tabKind -> tabKind.contents(repository) }
            .stateInRetainedModel(emptyList())

    @Composable
    override fun present(): TabSelectorState {
        val selectedKind by selectedKindStateFlow.collectAsStateWithLifecycle()
        val currentTabs by currentTabs.collectAsStateWithLifecycle()
        return TabSelectorState(selectedKind, currentTabs) { event ->
            when (event) {
                is TabSelectorUiEvent.OnChangeSelectedTabKind -> {
                    selectedKindStateFlow.value = event.kind
                }
            }
        }
    }
}

private fun TabSelectorKind.contents(repository: Repository): Flow<List<TabSource>> =
    when (this) {
        TabSelectorKind.PRESET -> presetTabsFlow()
        TabSelectorKind.ALBUM -> {
            repository.getAllAlbumsFlow().map(::mapMediaItemsToTabSource)
        }

        TabSelectorKind.ARTIST -> {
            repository.getAllArtistFlow().map(::mapMediaItemsToTabSource)
        }

        TabSelectorKind.GENRE -> {
            repository.getAllGenreFlow().map(::mapMediaItemsToTabSource)
        }

        TabSelectorKind.PLAYLIST -> {
            repository.getAllPlayListFlow().map(::mapMediaItemsToTabSource)
        }

        TabSelectorKind.VIDEO_BUCKET -> {
            repository.getAllVideoBucketsFlow().map(::mapMediaItemsToTabSource)
        }
    }

private fun presetTabsFlow(): Flow<List<TabSource>> =
    flow {
        emit(
            listOf(
                TabSource(
                    tabKind = TabKind.ALL_MUSIC,
                    label = getString(Res.string.audio_page_title),
                ),
                TabSource(
                    tabKind = TabKind.ALL_VIDEO,
                    label = getString(Res.string.video_page_title),
                ),
            ),
        )
    }

private fun mapMediaItemsToTabSource(items: List<MediaItemModel>) =
    items.map { item ->
        when (item) {
            is AlbumItemModel ->
                TabSource(
                    tabKind = TabKind.ALBUM,
                    externalId = item.id,
                    label = item.name,
                )

            is ArtistItemModel ->
                TabSource(
                    tabKind = TabKind.ARTIST,
                    externalId = item.id,
                    label = item.name,
                )

            is GenreItemModel ->
                TabSource(
                    tabKind = TabKind.GENRE,
                    externalId = item.id,
                    label = item.name,
                )

            is PlayListItemModel ->
                TabSource(
                    tabKind = TabKind.PLAYLIST,
                    externalId = item.id,
                    label = item.name,
                )

            is VideoBucketModel -> {
                TabSource(
                    tabKind = TabKind.VIDEO_BUCKET,
                    externalId = item.id,
                    label = item.name,
                )
            }

            else -> error("Never")
        }
    }

private fun TabSelectorKind.titleResource() =
    when (this) {
        TabSelectorKind.PRESET -> Res.string.preset
        TabSelectorKind.ALBUM -> Res.string.album_page_title
        TabSelectorKind.ARTIST -> Res.string.artist_page_title
        TabSelectorKind.GENRE -> Res.string.genre_title
        TabSelectorKind.PLAYLIST -> Res.string.playlist_page_title
        TabSelectorKind.VIDEO_BUCKET -> Res.string.video_buckets_page_title
    }
