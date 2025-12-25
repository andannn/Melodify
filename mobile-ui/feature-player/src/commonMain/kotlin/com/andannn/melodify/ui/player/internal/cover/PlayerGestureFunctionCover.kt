/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.cover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.components.play.control.PlayerUiEvent
import com.andannn.melodify.ui.player.internal.util.OffsetToStepHelper
import com.andannn.melodify.ui.player.internal.util.detectLongPressAndContinuousTap
import com.andannn.melodify.util.brightness.adjustBrightness
import com.andannn.melodify.util.brightness.rememberBrightnessManager
import com.andannn.melodify.util.volumn.VolumeController
import com.andannn.melodify.util.volumn.adjustVolume
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.koin.mp.KoinPlatform.getKoin

private const val TAG = "PlayerGestureFunctionCover"

private const val MAX_BRIGHTNESS_THRESHOLD = 400

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

    val brightnessController = rememberBrightnessManager()
    val volumeController: VolumeController = remember { getKoin().get() }

    val onVolumeStep: (isPositive: Boolean) -> Unit by rememberUpdatedState { isPositive ->
        volumeController.adjustVolume(isPositive)
    }

    val volumeStepHelper: OffsetToStepHelper =
        remember(onVolumeStep) {
            OffsetToStepHelper(
                onStep = onVolumeStep,
            )
        }

    LaunchedEffect(uiState) {
        if (uiState == UiState.ShowControl) {
            delay(showDurationMs)
            uiState = UiState.Idle
        }
    }

    fun togglePlayControl() {
        Napier.d(tag = TAG) { "toggle play control" }
        uiState =
            when (uiState) {
                UiState.Idle -> {
                    UiState.ShowControl
                }

                UiState.ShowControl -> {
                    UiState.Idle
                }

                else -> {
                    error("Never $uiState")
                }
            }
    }

    fun startDoubleSpeedPlay() {
        Napier.d(tag = TAG) { "start double speed play" }

        uiState = UiState.DoubleSpeedPlaying
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSetDoublePlaySpeed)
    }

    fun endDoubleSpeedPlay() {
        Napier.d(tag = TAG) { "end double speed play" }
        uiState = UiState.Idle
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSetPlaySpeedToNormal)
    }

    fun seekBackward() {
        Napier.d(tag = TAG) { "seek backward" }
        uiState = uiState.toNewSeekingState(isSeekForward = false)
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSeekBackwardGesture)
    }

    fun seekForward() {
        Napier.d(tag = TAG) { "seek forward" }
        uiState = uiState.toNewSeekingState(isSeekForward = true)
        // send callback
        onEvent.invoke(PlayerUiEvent.OnSeekForwardGesture)
    }

    fun endSeek() {
        Napier.d(tag = TAG) { "seek end" }
        uiState = UiState.Idle
    }

    fun onAdjustBrightness(offset: Float) {
        Napier.d(tag = TAG) { "on adjust brightness. $offset" }
        uiState = UiState.AdjustingBrightness
        brightnessController.adjustBrightness(
            offset = -1 * offset / MAX_BRIGHTNESS_THRESHOLD,
        )
    }

    fun onAdjustBrightnessEnd() {
        Napier.d(tag = TAG) { "on adjust brightness end." }
        uiState = UiState.Idle
    }

    fun onAdjustVolume(offset: Float) {
        Napier.d(tag = TAG) { "on adjust volume. $offset" }
        uiState = UiState.AdjustingVolume
        volumeStepHelper.onOffset(offset * -1)
    }

    fun onAdjustVolumeEnd() {
        Napier.d(tag = TAG) { "on adjust volume end." }
        uiState = UiState.Idle
        volumeStepHelper.reset()
    }

    // reset brightness when exit
    DisposableEffect(Unit) {
        onDispose {
            brightnessController.resetToSystemBrightness()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Spacer(
                Modifier.weight(1f).fillMaxHeight().detectLongPressAndContinuousTap(
                    key = "control cover detector left",
                    onTap = ::togglePlayControl,
                    onDrag = ::onAdjustBrightness,
                    onDragEnd = ::onAdjustBrightnessEnd,
                    onLongPressStart = ::startDoubleSpeedPlay,
                    onLongPressEnd = ::endDoubleSpeedPlay,
                    onContinuousTap = ::seekBackward,
                    onContinuousTapEnd = ::endSeek,
                ),
            )

            Spacer(
                Modifier.weight(1f).fillMaxHeight().detectLongPressAndContinuousTap(
                    key = "control cover detector right",
                    onTap = ::togglePlayControl,
                    onDrag = ::onAdjustVolume,
                    onDragEnd = ::onAdjustVolumeEnd,
                    onLongPressStart = ::startDoubleSpeedPlay,
                    onLongPressEnd = ::endDoubleSpeedPlay,
                    onContinuousTap = ::seekForward,
                    onContinuousTapEnd = ::endSeek,
                ),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val state = uiState
            AnimatedVisibility(
                uiState is UiState.ShowControl,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                controlWidget()
            }

            AnimatedVisibility(
                uiState is UiState.DoubleSpeedPlaying,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                DoubleSpeedPlayLabel()
            }

            AnimatedVisibility(
                uiState is UiState.AdjustingBrightness,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                BrightnessIndicator(
                    state = brightnessController.brightnessState.value,
                )
            }

            AnimatedVisibility(
                uiState is UiState.AdjustingVolume,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                VolumeIndicator(volumeController = volumeController)
            }

            if (state is UiState.Seeking || state is UiState.Idle) {
                var lastSeekingState by remember { mutableStateOf<UiState.Seeking?>(null) }
                if (state is UiState.Seeking) {
                    lastSeekingState = state
                }
                val label = lastSeekingState?.label()
                val isPositive = lastSeekingState?.seekSeconds?.isPositive == true
                SeekingIndicator(text = label, isPositive)
            }
        }
    }
}

private sealed interface UiState {
    data object Idle : UiState

    data object ShowControl : UiState

    data object DoubleSpeedPlaying : UiState

    data object AdjustingBrightness : UiState

    data object AdjustingVolume : UiState

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

            else -> error("impossible. $this")
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
private fun SeekingIndicator(
    text: String?,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
) {
    val scaleAnim = remember { Animatable(0.5f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(text) {
        // Show
        scaleAnim.snapTo(0.5f)
        alphaAnim.snapTo(0.5f)

        val deffer1 =
            async {
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                )
            }
        val deffer2 =
            async {
                alphaAnim.animateTo(1f, tween(200))
            }

        deffer1.await()
        deffer2.await()

        delay(1000)

        // Hide
        async {
            scaleAnim.animateTo(0.5f)
        }
        async {
            alphaAnim.animateTo(0f)
        }
    }

    if (text != null) {
        Box(modifier = modifier) {
            Row(
                modifier =
                    Modifier
                        .graphicsLayer {
                            alpha = alphaAnim.value
                            scaleX = scaleAnim.value
                            scaleY = scaleAnim.value
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
                    imageVector = if (isPositive) Icons.Default.SkipNext else Icons.Default.SkipPrevious,
                    tint = Color.White,
                    contentDescription = null,
                )
            }
        }
    }
}

private val Int.isPositive: Boolean
    get() = this > 0
