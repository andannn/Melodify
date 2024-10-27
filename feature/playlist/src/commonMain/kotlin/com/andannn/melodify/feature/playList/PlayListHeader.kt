package com.andannn.melodify.feature.playList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.andannn.melodify.feature.common.component.SmpTextButton
import com.andannn.melodify.feature.common.theme.MelodifyTheme
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.default_image_icon
import melodify.feature.common.generated.resources.play
import melodify.feature.common.generated.resources.shuffle
import melodify.feature.common.generated.resources.track_count
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PlayListHeader(
    modifier: Modifier = Modifier,
    coverArtUri: String = "",
    title: String = "",
    trackCount: Int = 0,
    defaultImagePlaceholderRes: DrawableResource = Res.drawable.default_image_icon,
    onPlayAllButtonClick: () -> Unit = {},
    onShuffleButtonClick: () -> Unit = {},
    onOptionClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        Surface(
            modifier =
            Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Row(
                modifier =
                Modifier
                    .padding(5.dp)
                    .height(IntrinsicSize.Max),
            ) {
                AsyncImage(
                    modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.extraSmall),
                    placeholder = painterResource(defaultImagePlaceholderRes),
                    error = painterResource(defaultImagePlaceholderRes),
                    model = coverArtUri,
                    contentDescription = "",
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier =
                    Modifier.weight(1f),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        modifier = Modifier,
                        text = stringResource(Res.string.track_count, trackCount),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onOptionClick) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "More")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Spacer(modifier = Modifier.width(10.dp))
            SmpTextButton(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Rounded.PlayArrow,
                text = stringResource(Res.string.play),
                onClick = onPlayAllButtonClick,
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onShuffleButtonClick,
            ) {
                Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)

                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(Res.string.shuffle),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview
@Composable
private fun PlayListControlBoxPreview() {
    MelodifyTheme {
        Surface {
            PlayListHeader(
                coverArtUri = "",
                title = "Title",
                trackCount = 10,
            )
        }
    }
}

@Preview
@Composable
private fun PlayListControlBoxLongTitlePreview() {
    MelodifyTheme {
        Surface {
            PlayListHeader(
                coverArtUri = "",
                title = "Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title Title ",
                trackCount = 10,
            )
        }
    }
}
