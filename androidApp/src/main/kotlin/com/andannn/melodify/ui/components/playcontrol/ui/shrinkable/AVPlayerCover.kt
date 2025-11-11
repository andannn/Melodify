package com.andannn.melodify.ui.components.playcontrol.ui.shrinkable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.PlayMode
import com.andannn.melodify.ui.components.playcontrol.PlayerUiEvent
import com.andannn.melodify.ui.util.formatDuration
import com.andannn.melodify.ui.widgets.LinerWaveSlider
import com.andannn.melodify.ui.widgets.MarqueeText
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AVPlayerCover(
    title: String,
    subTitle: String,
    duration: Long,
    progress: Float,
    isShuffle: Boolean,
    isPlaying: Boolean,
    playMode: PlayMode,
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false,
    onEvent: (PlayerUiEvent) -> Unit,
    onClickFullScreen: () -> Unit,
) {
    var isShowing by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isShowing) {
        if (isShowing) {
            delay(4000)
            isShowing = false
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = null,
                    onClick = {
                        isShowing = !isShowing
                    },
                ).clip(shape = RoundedCornerShape(8.dp)),
    ) {
        AnimatedVisibility(
            visible = isShowing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp),
            ) {
                if (isFullScreen) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        MarqueeText(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaxImagePaddingStart),
                            text = title,
                            spacingBetweenCopies = 40.dp,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = MaxImagePaddingStart),
                            text = subTitle,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                if (isFullScreen) {
                    Row(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Spacer(Modifier.weight(1f))
                        PlayControlButtons(
                            modifier = Modifier.weight(2f),
                            isShuffle = isShuffle,
                            isPlaying = isPlaying,
                            playMode = playMode,
                            onEvent = onEvent,
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                ) {
                    if (isFullScreen) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val durationString =
                                remember(duration) {
                                    formatDuration(duration)
                                }
                            val progressString =
                                remember(progress, duration) {
                                    formatDuration((progress * duration).roundToLong())
                                }

                            Text(progressString, style = MaterialTheme.typography.labelLarge)
                            LinerWaveSlider(
                                modifier = Modifier.weight(1f),
                                playing = isPlaying,
                                value = progress,
                                onValueChange = {
                                    onEvent(PlayerUiEvent.OnProgressChange(it))
                                },
                            )
                            Text(durationString, style = MaterialTheme.typography.labelLarge)
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = onClickFullScreen,
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Play",
                        )
                    }
                }
            }
        }
    }
}
