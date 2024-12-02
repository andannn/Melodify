package com.andannn.melodify.ui.components.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.LyricModel
import com.andannn.melodify.ui.common.theme.MelodifyTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LyricsView(
    source: AudioItemModel? = null,
    lyricStateHolder: LyricStateHolder = rememberLyricStateHolder(source),
    modifier: Modifier = Modifier,
) {
    LyricsViewContent(
        lyricState = lyricStateHolder.state,
        modifier = modifier,
    )
}

@Composable
private fun LyricsViewContent(
    lyricState: LyricState,
    modifier: Modifier = Modifier,
) {
    when (lyricState) {
        is LyricState.Loaded -> {
            val lyricModel = lyricState.lyric
            if (lyricModel == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "No lyrics found",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            } else {
                if (lyricModel.syncedLyrics.isNotBlank()) {
                    SyncedLyrics(
                        modifier = modifier,
                        syncedLyric = lyricModel.syncedLyrics,
                    )
                } else if (lyricModel.plainLyrics.isNotBlank()) {
                    PlainLyricsView(
                        modifier = modifier,
                        lyric = lyricModel.plainLyrics,
                    )
                }
            }
        }

        LyricState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun PlainLyricsView(
    lyric: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        text = lyric,
    )
}

@Preview
@Composable
private fun PlainLyricsViewPreview() {
    MelodifyTheme {
        Surface {
            LyricsViewContent(
                lyricState =
                    LyricState.Loaded(
                        LyricModel(
                            plainLyrics =
                                "正しさとは 愚かさとは\n" +
                                    "それが何か見せつけてやる\n" +
                                    "\n" +
                                    "ちっちゃな頃から優等生\n" +
                                    "気づいたら大人になっていた\n" +
                                    "ナイフの様な思考回路\n" +
                                    "持ち合わせる訳もなく\n" +
                                    "\n" +
                                    "でも遊び足りない 何か足りない\n" +
                                    "困っちまうこれは誰かのせい\n" +
                                    "あてもなくただ混乱するエイデイ\n" +
                                    "\n" +
                                    "それもそっか\n" +
                                    "最新の流行は当然の把握\n" +
                                    "経済の動向も通勤時チェック\n" +
                                    "純情な精神で入社しワーク\n" +
                                    "社会人じゃ当然のルールです\n" +
                                    "\n" +
                                    "はぁ？うっせぇうっせぇうっせぇわ\n" +
                                    "あなたが思うより健康です\n" +
                                    "一切合切凡庸な\n" +
                                    "あなたじゃ分からないかもね\n" +
                                    "嗚呼よく似合う\n" +
                                    "その可もなく不可もないメロディー\n" +
                                    "うっせぇうっせぇうっせぇわ\n" +
                                    "頭の出来が違うので問題はナシ\n" +
                                    "\n" +
                                    "つっても私模範人間\n" +
                                    "殴ったりするのはノーセンキュー\n" +
                                    "だったら言葉の銃口を\n" +
                                    "その頭に突きつけて撃てば\n" +
                                    "\n" +
                                    "マジヤバない？止まれやしない\n" +
                                    "不平不満垂れて成れの果て\n" +
                                    "サディスティックに変貌する精神\n" +
                                    "\n" +
                                    "クソだりぃな\n" +
                                    "酒が空いたグラスあれば直ぐに注ぎなさい\n" +
                                    "皆がつまみ易いように串外しなさい\n" +
                                    "会計や注文は先陣を切る\n" +
                                    "不文律最低限のマナーです\n" +
                                    "\n" +
                                    "はぁ？うっせぇうっせぇうっせぇわ\n" +
                                    "くせぇ口塞げや限界です\n" +
                                    "絶対絶対現代の代弁者は私やろがい\n" +
                                    "もう見飽きたわ\n" +
                                    "二番煎じ言い換えのパロディ\n" +
                                    "うっせぇうっせぇうっせぇわ\n" +
                                    "丸々と肉付いたその顔面にバツ\n" +
                                    "\n" +
                                    "うっせぇうっせぇうっせぇわ\n" +
                                    "うっせぇうっせぇうっせぇわ\n" +
                                    "私が俗に言う天才です\n" +
                                    "\n" +
                                    "うっせぇうっせぇうっせぇわ\n" +
                                    "あなたが思うより健康です\n" +
                                    "一切合切凡庸な\n" +
                                    "あなたじゃ分からないかもね\n" +
                                    "嗚呼つまらねぇ\n" +
                                    "何回聞かせるんだそのメモリー\n" +
                                    "うっせぇうっせぇうっせぇわ\n" +
                                    "アタシも大概だけど\n" +
                                    "どうだっていいぜ問題はナシ",
                            syncedLyrics = "",
                        ),
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun LyricsViewLoadingPreview() {
    MelodifyTheme {
        Surface {
            LyricsViewContent(
                lyricState = LyricState.Loading,
            )
        }
    }
}
