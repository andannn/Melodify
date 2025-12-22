package com.andannn.melodify.ui.player.internal.cover

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import kotlinx.coroutines.delay

@Composable
internal fun PlayerGestureFunctionCover(
    modifier: Modifier = Modifier,
    showDurationMs: Long = 4000,
    onEvent: (PlayerUiEvent) -> Unit,
    controlWidget: @Composable () -> Unit,
) {
    var uiState: UiState by remember {
        mutableStateOf(UiState.Idle)
    }

    LaunchedEffect(uiState) {
        if (uiState == UiState.ShowControl) {
            delay(showDurationMs)
            uiState = UiState.Idle
        }
    }

    fun togglePlayControl() {
        uiState =
            when (uiState) {
                UiState.Idle -> {
                    UiState.ShowControl
                }

                UiState.ShowControl -> {
                    UiState.Idle
                }

                UiState.DoubleSpeedPlaying,
                is UiState.Seeking,
                -> {
                    error("Never")
                }
            }
    }

    fun startDoubleSpeedPlay() {
        uiState = UiState.DoubleSpeedPlaying
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSetDoublePlaySpeed)
    }

    fun endDoubleSpeedPlay() {
        uiState = UiState.Idle
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSetPlaySpeedToNormal)
    }

    fun seekBackward() {
        uiState = uiState.toNewSeekingState(isSeekForward = false)
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSeekBackwardGesture)
        // trigger animation
    }

    fun seekForward() {
        uiState = uiState.toNewSeekingState(isSeekForward = true)
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSeekForwardGesture)
        // trigger animation
    }

    fun endSeek() {
        uiState = UiState.Idle
    }

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier.weight(1f).fillMaxHeight().detectLongPressAndContinuousTap(
                    key = "control cover detector left",
                    onTap = ::togglePlayControl,
                    onLongPressStart = ::startDoubleSpeedPlay,
                    onLongPressEnd = ::endDoubleSpeedPlay,
                    onContinuousTap = ::seekBackward,
                    onContinuousTapEnd = ::endSeek,
                ),
                contentAlignment = Alignment.Center,
            ) {
                val label = (uiState as? UiState.Seeking)?.label() ?: ""
                SeekHintLabel(text = label, isSeekNext = false)
            }

            Box(
                Modifier.weight(1f).fillMaxHeight().detectLongPressAndContinuousTap(
                    key = "control cover detector right",
                    onTap = ::togglePlayControl,
                    onLongPressStart = ::startDoubleSpeedPlay,
                    onLongPressEnd = ::endDoubleSpeedPlay,
                    onContinuousTap = ::seekForward,
                    onContinuousTapEnd = ::endSeek,
                ),
                contentAlignment = Alignment.Center,
            ) {
                val label = (uiState as? UiState.Seeking)?.label() ?: ""
                SeekHintLabel(text = label, isSeekNext = true)
            }
        }

        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = uiState,
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            },
        ) { uiState ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    UiState.DoubleSpeedPlaying -> {
                        DoubleSpeedPlayLabel(modifier = Modifier.align(Alignment.Center))
                    }

                    UiState.ShowControl -> {
                        controlWidget()
                    }

                    is UiState.Seeking -> {
                    }

                    UiState.Idle -> {
                    }
                }
            }
        }
    }
}

private sealed interface UiState {
    data object Idle : UiState

    data object ShowControl : UiState

    data object DoubleSpeedPlaying : UiState

    data class Seeking(
        val seekSeconds: Int,
    ) : UiState {
        fun label(): String = seekSeconds.toString() + "s"
    }

    fun toNewSeekingState(isSeekForward: Boolean) =
        when (this) {
            Idle,
            ShowControl,
            -> Seeking(if (isSeekForward) 10 else -10)

            is Seeking -> copy(seekSeconds = if (isSeekForward) seekSeconds + 10 else seekSeconds - 10)

            DoubleSpeedPlaying -> error("tapped when long press, impossible.")
        }
}

@Composable
private fun DoubleSpeedPlayLabel(modifier: Modifier = Modifier) {
    Text(
        modifier =
            modifier
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 6.dp, vertical = 3.dp),
        text = "X2 speed",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.White,
    )
}

@Composable
private fun SeekHintLabel(
    text: String,
    isSeekNext: Boolean,
    modifier: Modifier = Modifier,
    showDurationMs: Long = 400,
) {
    val alphaAnimatable = remember(text, isSeekNext) { Animatable(1f) }
    LaunchedEffect(isSeekNext, text) {
        alphaAnimatable.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = showDurationMs.toInt()),
        )
    }
    if (alphaAnimatable.value == 0f && text.isEmpty()) {
        Box(modifier = modifier) {
            Row(
                modifier =
                    Modifier
                        .graphicsLayer {
                            alpha = alphaAnimatable.value
                        }.clip(RoundedCornerShape(3.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isSeekNext) Icons.Default.SkipNext else Icons.Default.SkipPrevious,
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }
    }
}
