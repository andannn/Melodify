/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.components.playcontrol.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.BottomSheetDragAreaHeight
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.MinFadeoutWithExpandAreaPaddingTop
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.MinImagePaddingStart
import com.andannn.melodify.ui.components.playcontrol.ui.shrinkable.MinImagePaddingTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class PlayerState { Shrink, Expand }

enum class BottomSheetState { Shrink, Expand }

val ShrinkPlayerHeight = 70.dp
val MinImageSize = 60.dp

@Composable
internal fun rememberPlayerViewState(
    screenSize: Size,
    navigationBarHeightPx: Int,
    statusBarHeightPx: Int,
    density: Density,
    animaScope: CoroutineScope = rememberCoroutineScope(),
) = remember(
    screenSize,
    navigationBarHeightPx,
    statusBarHeightPx,
    density,
    animaScope,
) {
    PlayerViewState(
        screenSize = screenSize,
        navigationBarHeightPx = navigationBarHeightPx,
        statusBarHeightPx = statusBarHeightPx.toFloat(),
        density = density,
        animaScope = animaScope,
    )
}

@Stable
@OptIn(ExperimentalFoundationApi::class)
internal class
PlayerViewState(
    screenSize: Size,
    val navigationBarHeightPx: Int,
    statusBarHeightPx: Float,
    private val density: Density,
    private val animaScope: CoroutineScope,
) {
    private val shrinkPlayerHeightPx = ShrinkPlayerHeight.toPx()
    val bottomSheetHeight =
        screenSize.height - statusBarHeightPx - shrinkPlayerHeightPx
    private val bottomSheetDragAreaHeightPx = with(density) { BottomSheetDragAreaHeight.toPx() }

    val bottomSheetState =
        AnchoredDraggableState(
            initialValue = BottomSheetState.Shrink,
            anchors =
                DraggableAnchors {
                    BottomSheetState.Shrink at bottomSheetHeight - bottomSheetDragAreaHeightPx
                    BottomSheetState.Expand at 0f
                },
        )

    // 1f when sheet shrink, 0f when sheet fully expanded.
    private val bottomSheetOffsetFactor by derivedStateOf {
        bottomSheetState.offset.div(bottomSheetHeight - bottomSheetDragAreaHeightPx)
    }

    val isBottomSheetExpanding by derivedStateOf {
        bottomSheetOffsetFactor < 1f
    }

    private val shrinkPlayerHeight = shrinkPlayerHeightPx + navigationBarHeightPx
    val playerExpandState =
        AnchoredDraggableState(
            initialValue = PlayerState.Shrink,
            anchors =
                DraggableAnchors {
                    PlayerState.Shrink at shrinkPlayerHeight
                    PlayerState.Expand at screenSize.height
                },
        )

    // 0f when player shrink, 1f when player fully expanded.
    val playerExpandFactor by derivedStateOf {
        (playerExpandState.offset - shrinkPlayerHeight).div(screenSize.height - shrinkPlayerHeight)
    }
    val isPlayerExpanding by derivedStateOf {
        playerExpandFactor > 0f
    }
    val isFullExpanded by derivedStateOf {
        playerExpandFactor == 1f
    }

    val playerState: PlayerState by
        derivedStateOf {
            playerExpandState.currentValue
        }

    // - when bottom sheet is expanding: 0f when sheet fully expanded, 1f when sheet shrink.
    // - when player is expanding: 0f when player fully expanded, 1f when player shrink.
    val imageTransactionFactor by
        derivedStateOf {
            if (isBottomSheetExpanding) bottomSheetOffsetFactor else playerExpandFactor
        }

    private val maxImageSize = screenSize.height.toDp().div(2.5f)
    private val imagePaddingTopWhenFillExpandDp = screenSize.height.toDp().div(8)
    private val imagePaddingHorizontalWhenFillExpandDp =
        screenSize.width
            .toDp()
            .minus(maxImageSize)
            .div(2)

    val imageSizeDp: Dp by
        derivedStateOf {
            lerp(start = MinImageSize, stop = maxImageSize, imageTransactionFactor)
        }

    val imagePaddingTopDp: Dp by
        derivedStateOf {
            lerp(
                start = MinImagePaddingTop + if (isBottomSheetExpanding) statusBarHeightPx.toDp() else 0.dp,
                stop = imagePaddingTopWhenFillExpandDp,
                imageTransactionFactor,
            )
        }

    val imagePaddingStartDp: Dp by
        derivedStateOf {
            lerp(
                start = MinImagePaddingStart,
                stop = imagePaddingHorizontalWhenFillExpandDp,
                imageTransactionFactor,
            )
        }

    val miniPlayerPaddingTopDp: Dp by
        derivedStateOf {
            lerp(
                start = MinFadeoutWithExpandAreaPaddingTop + if (isBottomSheetExpanding) statusBarHeightPx.toDp() else 0.dp,
                stop = 0.dp,
                imageTransactionFactor,
            )
        }

    fun shrinkPlayerLayout() {
        animaScope.launch {
            playerExpandState.animateTo(PlayerState.Shrink)
        }
    }

    fun expandPlayerLayout() {
        animaScope.launch {
            playerExpandState.animateTo(PlayerState.Expand)
        }
    }

    fun expandBottomSheet() {
        animaScope.launch {
            bottomSheetState.animateTo(BottomSheetState.Expand)
        }
    }

    private fun Dp.toPx() = with(density) { toPx() }

    private fun Float.toDp() = with(density) { toDp() }
}
