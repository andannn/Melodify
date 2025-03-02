package com.andannn.melodify.ui.components.search.searchedItem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.ui.common.widgets.ListTileItemView
import com.andannn.melodify.ui.components.popup.LocalPopupController
import com.andannn.melodify.ui.components.popup.PopupController
import com.andannn.melodify.ui.components.popup.dialog.DialogAction
import com.andannn.melodify.ui.components.popup.dialog.DialogId
import com.andannn.melodify.ui.components.popup.onMediaOptionClick
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@Composable
internal fun SearchedItem(
    modifier: Modifier = Modifier,
    mediaItemModel: MediaItemModel,
    popupController: PopupController = LocalPopupController.current,
    repository: Repository = getKoin().get()
) {
    suspend fun onHandleResult(result: DialogAction) {
        if (result is DialogAction.MediaOptionDialog.ClickItem) {
            repository.onMediaOptionClick(
                optionItem = result.optionItem,
                dialog = result.dialog,
                popupController = popupController
            )
        }
    }

    val scope = rememberCoroutineScope()
    when (mediaItemModel) {
        is AlbumItemModel -> Item(
            modifier = modifier,
            title = mediaItemModel.name,
            subTitle = "Album",
            cover = mediaItemModel.artWorkUri,
            onOptionButtonClick = {
                scope.launch {
                    val result = popupController.showDialog(DialogId.AlbumOption(mediaItemModel))
                    onHandleResult(result)
                }
            },
            onItemClick = {
                // TODO: navigate to album
            }
        )

        is ArtistItemModel -> Item(
            modifier = modifier,
            title = mediaItemModel.name,
            subTitle = "Artist",
            cover = mediaItemModel.artWorkUri,
            onOptionButtonClick = {
                scope.launch {
                    val result = popupController.showDialog(DialogId.ArtistOption(mediaItemModel))
                    onHandleResult(result)
                }
            },
            onItemClick = {
                // TODO: navigate to artist
            }
        )

        is AudioItemModel -> Item(
            modifier = modifier,
            title = mediaItemModel.name,
            cover = mediaItemModel.artWorkUri,
            subTitle = "Song",
            onItemClick = {
                // TODO: play this song and navigate to player page
            },
            onOptionButtonClick = {
                scope.launch {
                    val result = popupController.showDialog(DialogId.AudioOption(mediaItemModel))
                    onHandleResult(result)
                }
            },
        )

        is GenreItemModel,
        is PlayListItemModel -> error("Not supported as searched result")
    }
}

@Composable
private fun Item(
    modifier: Modifier = Modifier,
    title: String,
    cover: String = "",
    subTitle: String = "",
    onOptionButtonClick: () -> Unit = {},
    onItemClick: () -> Unit = {},
) {
    ListTileItemView(
        modifier = modifier,
        title = title,
        albumArtUri = cover,
        subTitle = subTitle,
        defaultColor = Color.Transparent,
        onOptionButtonClick = onOptionButtonClick,
        onMusicItemClick = onItemClick
    )
}