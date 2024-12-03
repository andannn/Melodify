package com.andannn.melodify.ui.components.drawer.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.ArtistItemModel
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.GenreItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.ui.common.widgets.FavoriteIconButton
import com.andannn.melodify.ui.common.icons.SmpIcon
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import com.andannn.melodify.ui.components.drawer.model.SheetModel
import com.andannn.melodify.ui.components.drawer.model.SheetOptionItem
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.default_image_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaOptionBottomSheet(
    optionSheet: SheetModel.MediaOptionSheet,
    modifier: Modifier = Modifier,
    onClickOption: (SheetOptionItem) -> Unit = {},
    onToggleFavorite: (model: AudioItemModel) -> Unit = {},
    onRequestDismiss: () -> Unit = {}
) {
    val sheetState =
        rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onRequestDismiss.invoke()
        },
    ) {
        Surface(modifier = modifier.navigationBarsPadding()) {
            val isAudioOptionSheet = remember(optionSheet) {
                optionSheet is SheetModel.AudioOptionSheet || optionSheet is SheetModel.PlayerOptionSheet
            }
            Column(Modifier.fillMaxWidth()) {
                SheetHeader(
                    showFavorite = isAudioOptionSheet,
                    mediaItem = optionSheet.source,
                    onToggleFavorite = {
                        onToggleFavorite(optionSheet.source as AudioItemModel)
                    }
                )

                HorizontalDivider()

                optionSheet.options.map { item ->
                    SheetItem(
                        item = item,
                        onClick = {
                            onClickOption.invoke(item)
                        },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SheetHeader(
    mediaItem: MediaItemModel,
    showFavorite: Boolean,
    modifier: Modifier = Modifier,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    onToggleFavorite: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier =
            Modifier
                .size(65.dp)
                .clip(MaterialTheme.shapes.extraSmall),
            placeholder = painterResource(defaultImagePlaceholderRes),
            error = painterResource(defaultImagePlaceholderRes),
            model = mediaItem.artWorkUri,
            contentDescription = "",
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = mediaItem.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(Modifier.weight(1f))

            Text(
                modifier = Modifier.alpha(0.7f),
                text = mediaItem.subTitle(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (showFavorite) {
            val isFavorite by getKoin().get<PlayListRepository>()
                .isMediaInFavoritePlayListFlow(mediaItem.id).collectAsState(false)

            Spacer(modifier = Modifier.weight(1f))
            FavoriteIconButton(
                isFavorite = isFavorite,
                enabled = true,
                onClick = onToggleFavorite,
            )
        }
    }
}

private fun MediaItemModel.subTitle() = when (this) {
    is AlbumItemModel,
    is ArtistItemModel,
    is PlayListItemModel -> {
        "$trackCount songs"
    }

    is AudioItemModel -> {
        artist
    }

    is GenreItemModel -> {
        ""
    }

}

@Composable
fun SheetItem(
    item: SheetOptionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        SmpIcon(item.smpIcon)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(item.text),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SmpIcon(item: SmpIcon) {
    when (item) {
        is SmpIcon.ImageVectorIcon -> {
            Icon(imageVector = item.imageVector, contentDescription = "")
        }
    }
}

@Preview
@Composable
private fun SheetHeaderPreview() {
    MelodifyTheme {
        Surface {
            SheetHeader(
                mediaItem = source,
                showFavorite = true,
            )
        }
    }
}

@Preview
@Composable
private fun MediaBottomDrawerDemo() {
    MelodifyTheme {
        var isShow by remember {
            mutableStateOf(false)
        }

        Surface {
            Button(onClick = { isShow = true }) {
                Text(text = "Show")
            }
        }

        if (isShow) {
            MediaOptionBottomSheet(
                optionSheet = SheetModel.AudioOptionSheet(
                    source = source,
                ),
                onClickOption = {
                    isShow = false
                },
            )
        }
    }
}

private val source = AudioItemModel(
    id = "0",
    name = "Song 1 LongLongLongLongLongLongLongLongLongLongLongLongLong",
    modifiedDate = 0,
    album = "Album 1",
    albumId = "0",
    artist = "Artist 1",
    artistId = "0",
    cdTrackNumber = 1,
    discNumber = 0,
    artWorkUri = "",
    source = "",
)