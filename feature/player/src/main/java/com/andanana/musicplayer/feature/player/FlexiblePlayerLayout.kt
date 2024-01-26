package com.andanana.musicplayer.feature.player

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil.compose.AsyncImage
import com.andanana.musicplayer.core.data.model.PlayMode
import com.andanana.musicplayer.core.designsystem.R
import com.andanana.musicplayer.core.designsystem.component.SmpMainIconButton
import com.andanana.musicplayer.core.designsystem.component.SmpSubIconButton
import com.andanana.musicplayer.core.designsystem.theme.MusicPlayerTheme

private const val TAG = "BottomPlayerSheet"

val MinImageSize = 60.dp
// MaxImageSize is calculated.

val MinImagePaddingTop = 5.dp
val MaxImagePaddingTop = 130.dp

val MinImagePaddingStart = 5.dp
val MaxImagePaddingStart = 20.dp

val MinFadeoutWithExpandAreaPaddingTop = 15.dp

@Composable
fun FlexiblePlayerLayout(
    modifier: Modifier = Modifier,
    heightPxRange: ClosedFloatingPointRange<Float> = 100f..800f,
    coverUri: String,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    title: String = "",
    artist: String = "",
    progress: Float = 1f,
    onPlayerSheetClick: () -> Unit = {},
    onPlayControlButtonClick: () -> Unit = {},
    onPlayNextButtonClick: () -> Unit = {},
    onFavoriteButtonClick: () -> Unit = {},
    onShrinkButtonClick: () -> Unit = {},
    onOptionIconClick: () -> Unit = {},
) {
    val statusBarHeight =
        with(LocalDensity.current) {
            WindowInsets.statusBars.getTop(this).toDp()
        }

    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        shadowElevation = 10.dp,
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val maxImageWidth = maxWidth - MaxImagePaddingStart * 2
            val minImageWidth = MinImageSize

            val (minHeightPx, maxHeightPx) = heightPxRange.start to heightPxRange.endInclusive
            val currentHeight = constraints.maxHeight
            val expandFactor =
                (currentHeight - minHeightPx).div(maxHeightPx - minHeightPx).coerceIn(0f, 1f)
            Log.d(TAG, "FlexiblePlayerLayout: expandFactor $expandFactor")

            val imageWidthDp = lerp(start = minImageWidth, stop = maxImageWidth, expandFactor)
            val imagePaddingTopDp =
                lerp(start = MinImagePaddingTop, stop = MaxImagePaddingTop, expandFactor)
            val imagePaddingStartDp =
                lerp(start = MinImagePaddingStart, stop = MaxImagePaddingStart, expandFactor)
            val fadingAreaPaddingTop =
                lerp(
                    start = MinFadeoutWithExpandAreaPaddingTop,
                    stop = statusBarHeight,
                    expandFactor,
                )

            val fadeoutAreaAlpha = 1 - (expandFactor * 4).coerceIn(0f, 1f)
            val isExpand = expandFactor > 0.01f
            FadeoutWithExpandArea(
                modifier =
                    Modifier
                        .graphicsLayer {
                            alpha = fadeoutAreaAlpha
                        }
                        .fillMaxWidth()
                        .padding(
                            top = fadingAreaPaddingTop,
                            start = MinImagePaddingStart * 2 + MinImageSize,
                        ),
                title = title,
                artist = artist,
                isPlaying = isPlaying,
                isFavorite = isFavorite,
                onPlayControlButtonClick = onPlayControlButtonClick,
                onPlayNextButtonClick = onPlayNextButtonClick,
                onFavoriteButtonClick = onFavoriteButtonClick,
            )

            val fadeInAreaAlpha = (1f - (1f - expandFactor).times(3f)).coerceIn(0f, 1f)
            IconButton(
                modifier =
                    Modifier
                        .padding(top = statusBarHeight, start = 4.dp)
                        .rotate(-90f)
                        .graphicsLayer {
                            alpha = fadeInAreaAlpha
                        },
                onClick = onShrinkButtonClick,
            ) {
                Icon(imageVector = Icons.Rounded.ArrowBackIos, contentDescription = "Shrink")
            }
            IconButton(
                modifier =
                    Modifier
                        .padding(top = statusBarHeight, end = 4.dp)
                        .align(Alignment.TopEnd)
                        .graphicsLayer {
                            alpha = fadeInAreaAlpha
                        },
                onClick = onOptionIconClick,
            ) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
            }

            Column {
                CircleImage(
                    modifier =
                        Modifier
//                        .align(Alignment.TopStart)
                            .padding(top = imagePaddingTopDp)
                            .padding(start = imagePaddingStartDp)
                            .width(imageWidthDp)
                            .aspectRatio(1f),
                    model = coverUri,
                )

                FadeInWithExpandArea(
                    modifier =
                        Modifier
                            .weight(1f)
                            .graphicsLayer {
                                alpha = fadeInAreaAlpha
                            }
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
//                        .padding(top = imagePaddingTopDp + imageWidthDp)
                    progress = progress,
                    title = title,
                    artist = artist,
                )
            }

            if (!isExpand) {
                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth(fraction = progress)
                            .align(Alignment.BottomStart)
                            .height(3.dp)
                            .background(
                                brush =
                                    Brush.horizontalGradient(
                                        colors =
                                            listOf(
                                                MaterialTheme.colorScheme.tertiaryContainer,
                                                MaterialTheme.colorScheme.inversePrimary,
                                                MaterialTheme.colorScheme.primary,
                                            ),
                                    ),
                            ),
                )
            }
        }
    }
}

@Composable
private fun FadeoutWithExpandArea(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onPlayControlButtonClick: () -> Unit,
    onPlayNextButtonClick: () -> Unit,
    onFavoriteButtonClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = artist,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(
            modifier =
                Modifier
                    .size(30.dp)
                    .scale(1.2f),
            onClick = onPlayControlButtonClick,
        ) {
            if (isPlaying) {
                Icon(imageVector = Icons.Rounded.Pause, contentDescription = "")
            } else {
                Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "")
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            modifier =
                Modifier
                    .size(30.dp)
                    .padding(5.dp)
                    .rotate(180f),
            onClick = onPlayNextButtonClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.music_music_player_player_previous_icon),
                contentDescription = "",
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            modifier = Modifier.size(30.dp),
            onClick = onFavoriteButtonClick,
        ) {
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    tint = Color.Red,
                    contentDescription = "",
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = "",
                )
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
    }
}

@Composable
private fun FadeInWithExpandArea(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    progress: Float = 0.5f,
    isPlaying: Boolean = false,
    playMode: PlayMode = PlayMode.REPEAT_ALL,
    onPlayModeButtonClick: () -> Unit = {},
    onPreviousButtonClick: () -> Unit = {},
    onPlayButtonClick: () -> Unit = {},
    onNextButtonClick: () -> Unit = {},
    onPlayListButtonClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
            text = artist,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(60.dp))
        Slider(
            value = progress,
            onValueChange = { },
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SmpSubIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                onClick = onPlayModeButtonClick,
                imageVector = Icons.Rounded.ShuffleOn,
            )
            SmpSubIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                scale = 2f,
                onClick = onPreviousButtonClick,
                imageVector = Icons.Rounded.SkipPrevious,
            )

            SmpMainIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                onClick = onPlayButtonClick,
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            )
            SmpSubIconButton(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(10.dp),
                scale = 2f,
                onClick = onNextButtonClick,
                imageVector = Icons.Rounded.SkipNext,
            )
            SmpSubIconButton(
                modifier = Modifier.weight(1f),
                onClick = onPlayListButtonClick,
                imageVector = Icons.Filled.RepeatOne,
            )
        }
    }
}

@Composable
fun CircleImage(
    modifier: Modifier = Modifier,
    model: String,
) {
    AsyncImage(
        modifier =
            modifier
                .clip(shape = CircleShape)
                .border(
                    shape = CircleShape,
                    border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary),
                ),
        model = model,
        contentDescription = "",
    )
}

@Preview(name = "Dark")
@Composable
fun PlayingWithFavoriteSongBottomPlayerSheetPreview() {
    MusicPlayerTheme {
        FlexiblePlayerLayout(
            modifier = Modifier.height(820.dp),
            coverUri = "",
            isPlaying = true,
            isFavorite = true,
            title = "Song name",
            artist = "Artist name",
        )
    }
}

@Preview(name = "Dark")
@Composable
fun DarkBottomPlayerSheetPreview() {
    MusicPlayerTheme(darkTheme = true) {
        FlexiblePlayerLayout(
            modifier = Modifier.height(70.dp),
            coverUri = "",
            title = "Song name",
            artist = "Artist name",
        )
    }
}

@Preview(name = "Light")
@Composable
fun LightBottomPlayerSheetPreview() {
    MusicPlayerTheme(darkTheme = false) {
        FlexiblePlayerLayout(
            modifier = Modifier.height(70.dp),
            coverUri = "",
            title = "Song name",
            artist = "Artist name",
        )
    }
}

@Preview(name = "Light")
@Composable
fun LargeControlAreaPreview() {
    MusicPlayerTheme(darkTheme = false) {
        Surface {
            FadeInWithExpandArea(
                modifier = Modifier,
                title = "title",
                artist = "artist",
            )
        }
    }
}