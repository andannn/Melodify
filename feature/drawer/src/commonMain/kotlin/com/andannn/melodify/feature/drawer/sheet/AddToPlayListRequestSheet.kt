package com.andannn.melodify.feature.drawer.sheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.feature.common.component.ActionType
import com.andannn.melodify.feature.common.component.LargePreviewCard
import com.andannn.melodify.feature.common.component.ListTileItemView
import com.andannn.melodify.feature.common.util.key
import com.andannn.melodify.feature.drawer.model.SheetModel
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.all_playlists
import melodify.feature.common.generated.resources.all_to_playlist_page_title
import melodify.feature.common.generated.resources.selected_songs
import melodify.feature.common.generated.resources.track_count
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlayListRequestSheet(
    modifier: Modifier = Modifier,
    sheet: SheetModel.AddToPlayListSheet,
    onRequestDismiss: () -> Unit = {},
    onAddToPlay: (PlayListItemModel, List<AudioItemModel>) -> Unit,
) {
    val state = rememberAddToPlayListSheetState(sheet.source)
    ModalBottomSheet(
        sheetState = state.sheetState,
        onDismissRequest = {
            onRequestDismiss.invoke()
        },
    ) {
        AddToPlayListRequestSheetContent(
            modifier.fillMaxWidth(),
            audioList = state.audioListState,
            playLists = state.playListState,
            onRequestDismiss = onRequestDismiss,
            onPlayListClick = { playList ->
                onAddToPlay(playList, state.audioListState)
            }
        )
    }
}

@Composable
internal fun AddToPlayListRequestSheetContent(
    modifier: Modifier = Modifier,
    audioList: List<AudioItemModel>,
    playLists: List<PlayListItemModel>,
    onRequestDismiss: () -> Unit = {},
    onPlayListClick: (PlayListItemModel) -> Unit = {},
) {
    val itemCount by rememberUpdatedState(audioList.size)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.all_to_playlist_page_title, itemCount),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onRequestDismiss
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            item {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(Res.string.selected_songs),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.padding(top = 16.dp).height(150.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = audioList,
                        key = { it.key }
                    ) { audio ->
                        LargePreviewCard(
                            modifier = Modifier.width(100.dp),
                            title = audio.name,
                            backGroundColor = Color.Transparent,
                            artCoverUri = audio.artWorkUri,
                        )
                    }
                }
            }

            item {
                Column {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Text(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                    text = stringResource(Res.string.all_playlists),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            items(
                items = playLists,
                key = { it.key }
            ) { playList ->
                ListTileItemView(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    title = playList.name,
                    subTitle = stringResource(Res.string.track_count, playList.trackCount),
                    albumArtUri = playList.artWorkUri,
                    actionType = ActionType.NONE,
                    defaultColor = Color.Transparent,
                    onMusicItemClick = {
                        onPlayListClick(playList)
                    },
                )
            }
        }
    }
}