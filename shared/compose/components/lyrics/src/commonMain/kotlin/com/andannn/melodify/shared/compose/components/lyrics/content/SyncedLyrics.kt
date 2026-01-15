/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.lyrics.content

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.platform.formatTime
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun SyncedLyrics(
    modifier: Modifier = Modifier,
    syncedLyric: String,
    lazyListState: LazyListState = rememberLazyListState(),
    presenter: Presenter<SyncedLyricsState> =
        retainSyncedLyricsPresenter(
            syncedLyric,
            lazyListState,
        ),
) {
    SyncedLyricsContent(
        modifier = modifier,
        lazyListState = lazyListState,
        state = presenter.present(),
    )
}

@Stable
internal data class SyncedLyricsState(
    val syncedLyricsLines: List<SyncedLyricsLine>,
    val lyricsState: LyricsState = LyricsState.AutoScrolling,
    val currentPlayingIndex: Int = 0,
    val eventSink: (SyncedLyricsEvent) -> Unit = {},
)

internal sealed interface SyncedLyricsEvent {
    data class SeekToTime(
        val time: Long,
    ) : SyncedLyricsEvent
}

@Stable
internal data class SyncedLyricsLine(
    val startTimeMs: Long,
    val endTimeMs: Long = 0L,
    private val lyrics: String,
) {
    val lyricsString: String
        get() = lyrics.takeIf { it.isNotBlank() } ?: "[ Music ]"
}

@Stable
internal sealed interface LyricsState {
    data object AutoScrolling : LyricsState

    data class Seeking(
        val currentSeekIndex: Int,
    ) : LyricsState

    data class WaitingSeekingResult(
        val requestTimeMs: Long,
    ) : LyricsState
}

@Composable
private fun SyncedLyricsContent(
    state: SyncedLyricsState,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
) {
    val activeIndex =
        remember(state) {
            when (val lyricsState = state.lyricsState) {
                LyricsState.AutoScrolling -> -1
                is LyricsState.Seeking -> lyricsState.currentSeekIndex
                is LyricsState.WaitingSeekingResult -> -1
            }
        }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val maxHeight = maxHeight
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = maxHeight / 2),
        ) {
            itemsIndexed(
                items = state.syncedLyricsLines,
                key = { _, item -> item.startTimeMs },
            ) { index, item ->
                LyricLine(
                    lyricsLine = item,
                    isPlaying = state.currentPlayingIndex == index,
                    isActive = activeIndex == index,
                    onSeekTimeClick = {
                        state.eventSink.invoke(SyncedLyricsEvent.SeekToTime(item.startTimeMs))
                    },
                )
            }
        }
    }
}

@Composable
private fun LyricLine(
    isPlaying: Boolean,
    isActive: Boolean,
    lyricsLine: SyncedLyricsLine,
    modifier: Modifier = Modifier,
    onSeekTimeClick: () -> Unit = {},
) {
    val transition = updateTransition(targetState = isPlaying, label = "playingState")

    val alpha by transition.animateFloat(label = "alpha") { playing ->
        if (playing) 1f else 0.4f
    }

    val activeTransition = updateTransition(targetState = isActive, label = "playingState")
    val backgroundColor by activeTransition.animateColor(label = "color") { active ->
        if (active) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        }
    }

    val fontColor by activeTransition.animateColor(label = "fontColor") { active ->
        if (active) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    }

    val seekButtonAlpha by activeTransition.animateFloat(label = "seekButtonAlpha") { active ->
        if (active) 1f else 0f
    }

    Box(
        modifier =
            modifier
                .background(backgroundColor)
                .fillMaxWidth(),
    ) {
        Text(
            modifier =
                Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .alpha(alpha),
            text = lyricsLine.lyricsString,
            style = MaterialTheme.typography.headlineSmall,
            color = fontColor,
        )

        if (seekButtonAlpha != 0f) {
            Surface(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .alpha(seekButtonAlpha)
                        .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = onSeekTimeClick,
            ) {
                Row(
                    modifier =
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        modifier = Modifier,
                        text =
                            lyricsLine.startTimeMs.milliseconds.toComponents { minutes, seconds, _ ->
                                formatTime(minutes, seconds)
                            },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}

@Preview
@Composable
private fun LyricsViewContentSyncedLoadedPreview() {
    MelodifyTheme {
        Surface {
            val lines =
                remember {
                    parseSyncedLyrics(
                        """
                        [00:00.05] Please hear me
                        [00:03.45] I want to tell you
                        [00:06.25] Please sing to me
                        [00:10.35] I wanna hear your voice
                        [00:16.27] 
                        [00:27.94] 時の鼓動がまだ響く間
                        [00:35.09] 裸の言葉胸に閉じこめた
                        [00:40.55] 記憶の色が滲み始める
                        [00:47.61] 破れた世界の隅で
                        [00:55.08] 何も求めずにただ抱き寄せる
                        [01:01.63] 今の僕にはそれしか出来ない
                        [01:07.13] 震えた強がりでもプライドに見える
                        [01:14.35] 逸れた子供のように
                        [01:20.77] 最後の声さえも
                        [01:27.78] 風がさまようせいで消された
                        [01:34.19] 月に手を向けたまま
                        [01:40.99] 君は空の星に消えた
                        [01:49.53] 「側にいて」と抱きしめても
                        [01:56.33] もう2度と聞こえない君の歌声は
                        [02:02.99] 降り注いだ雨のサイレン
                        [02:10.49] 僕の代わりに今この空が泣き続ける
                        [02:25.08] 
                        [02:31.40] これまで踏みつけてきた教えを
                        [02:38.00] 今掻き集めこの胸に当てても
                        [02:43.97] 救い求め歌うようなお遊戯に見える
                        [02:50.84] 物語る大人のように
                        [02:57.67] 言葉に寄り添うだけの
                        [03:04.23] 空の愛と導きはいらない
                        [03:10.86] 飾られた祈りでは
                        [03:17.55] 明日の手掛かりに触れない
                        [03:24.79] いつか君に届くはずの
                        [03:31.28] 名も無き幼い詩(し)が描くわがままを
                        [03:38.17] 忘れたいよ一度だけ
                        [03:45.68] 眠れぬ悲しみがその詩(うた)を抱きしめてる
                        [03:53.07] Freezing cold shatters my sorrow
                        [03:59.42] And scorching sand puts it together again
                        [04:06.32] Freezing cold shatters my sorrow
                        [04:12.86] And scorching sand puts it together again
                        [04:18.35] 投げ捨てられる正しさなら
                        [04:24.79] 消える事ない間違いの方が良い
                        [04:31.95] 臆病に隠してた声を今
                        [04:38.50] この手でもう一度さらせば良い
                        [04:47.83] 掴む軌道も咲く光も
                        [04:54.79] 乾いた心のせいでモノクロに見えた
                        [05:01.33] 忘れないよ今日の景色を
                        [05:08.98] ありふれた願いが足元を照らしてくれる
                        [05:17.06] 
                        """.trimIndent(),
                    )
                }
            SyncedLyricsContent(
                state =
                    SyncedLyricsState(
                        syncedLyricsLines = lines,
                        currentPlayingIndex = 0,
                    ),
            )
        }
    }
}
