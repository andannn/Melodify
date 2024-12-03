package com.andannn.melodify.ui.components.tabcontent

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.core.data.model.browsableOrPlayable
import com.andannn.melodify.core.data.model.key
import com.andannn.melodify.ui.common.widgets.ExtraPaddingBottom
import com.andannn.melodify.ui.common.widgets.ListTileItemView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource

@Composable
fun TabContent(
    stateHolder: TabContentStateHolder
) {
    val listLayoutState =
// TODO: add selectedIndex
        rememberSaveable(saver = LazyListState.Saver) {
            LazyListState()
        }
    LazyListContent(
        modifier =
        Modifier.fillMaxSize(),
        state = listLayoutState,
        contentMap = stateHolder.state.contentMap,
        onMusicItemClick = stateHolder::playMusic,
        onShowMusicItemOption = stateHolder::onShowMusicItemOption
    )
}

@Composable
private fun LazyListContent(
    contentMap: Map<MediaItemModel, List<AudioItemModel>>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    onMusicItemClick: (AudioItemModel) -> Unit = {},
    onShowMusicItemOption: (AudioItemModel) -> Unit = {},
) {
    LazyColumn(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 5.dp),
    ) {
        contentMap.forEach { (header, mediaItems) ->
            item(header.key) {
                ListHeader(
                    coverArtUri = header.artWorkUri,
                    title = header.name,
                    trackCount = mediaItems.size,
                )
            }

            items(
                items = mediaItems,
                key = { it.key },
            ) { item ->
                ListTileItemView(
                    modifier =
                    Modifier.animateItem(),
                    playable = item.browsableOrPlayable,
                    isActive = false,
                    albumArtUri = item.artWorkUri,
                    title = item.name,
                    showTrackNum = header is AlbumItemModel,
                    trackNum = item.cdTrackNumber,
                    onMusicItemClick = {
                        onMusicItemClick.invoke(item)
                    },
                    onOptionButtonClick = {
                        onShowMusicItemOption(item)
                    },
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        item { ExtraPaddingBottom() }
    }
}
