package com.andannn.melodify.feature.player.lyrics
import org.junit.Test

class ParseSyncLyricsTest {
    private val dummy = "[00:00.55] 正しさとは 愚かさとは\n" +
            "[00:03.72] それが何か見せつけてやる\n" +
            "[00:08.78] \n" +
            "[00:16.80] ちっちゃな頃から優等生\n" +
            "[00:19.55] 気づいたら大人になっていた\n" +
            "[00:21.81] ナイフの様な思考回路\n" +
            "[00:24.67] 持ち合わせる訳もなく\n" +
            "[00:27.31] でも遊び足りない 何か足りない\n" +
            "[00:30.36] 困っちまうこれは誰かのせい\n" +
            "[00:32.78] あてもなくただ混乱するエイデイ\n" +
            "[00:37.29] それもそっか\n" +
            "[00:38.97] 最新の流行は当然の把握\n" +
            "[00:41.10] 経済の動向も通勤時チェック\n" +
            "[00:43.53] 純情な精神で入社しワーク\n" +
            "[00:46.61] 社会人じゃ当然のルールです\n" +
            "[00:51.21] はぁ？うっせぇうっせぇうっせぇわ\n" +
            "[00:54.38] あなたが思うより健康です\n" +
            "[00:56.70] 一切合切凡庸な\n" +
            "[00:59.85] あなたじゃ分からないかもね\n" +
            "[01:02.85] 嗚呼よく似合う\n" +
            "[01:04.65] その可もなく不可もないメロディー\n" +
            "[01:08.35] うっせぇうっせぇうっせぇわ\n" +
            "[01:10.89] 頭の出来が違うので問題はナシ\n" +
            "[01:16.41] \n" +
            "[01:25.32] つっても私模範人間\n" +
            "[01:28.12] 殴ったりするのはノーセンキュー\n" +
            "[01:30.73] だったら言葉の銃口を\n" +
            "[01:33.58] その頭に突きつけて撃てば\n" +
            "[01:36.43] マジヤバない？止まれやしない\n" +
            "[01:38.80] 不平不満垂れて成れの果て\n" +
            "[01:41.72] サディスティックに変貌する精神\n" +
            "[01:46.03] クソだりぃな\n" +
            "[01:47.38] 酒が空いたグラスあれば直ぐに注ぎなさい\n" +
            "[01:49.87] 皆がつまみ易いように串外しなさい\n" +
            "[01:52.25] 会計や注文は先陣を切る\n" +
            "[01:55.39] 不文律最低限のマナーです\n" +
            "[02:00.23] はぁ？うっせぇうっせぇうっせぇわ\n" +
            "[02:03.17] くせぇ口塞げや限界です\n" +
            "[02:05.96] 絶対絶対現代の代弁者は私やろがい\n" +
            "[02:11.65] もう見飽きたわ\n" +
            "[02:13.43] 二番煎じ言い換えのパロディ\n" +
            "[02:16.65] うっせぇうっせぇうっせぇわ\n" +
            "[02:19.25] 丸々と肉付いたその顔面にバツ\n" +
            "[02:24.69] \n" +
            "[02:34.40] うっせぇうっせぇうっせぇわ\n" +
            "[02:39.96] うっせぇうっせぇうっせぇわ\n" +
            "[02:42.98] 私が俗に言う天才です\n" +
            "[02:45.51] うっせぇうっせぇうっせぇわ\n" +
            "[02:48.08] あなたが思うより健康です\n" +
            "[02:50.55] 一切合切凡庸な\n" +
            "[02:53.11] あなたじゃ分からないかもね\n" +
            "[02:56.33] 嗚呼つまらねぇ\n" +
            "[02:58.11] 何回聞かせるんだそのメモリー\n" +
            "[03:01.77] うっせぇうっせぇうっせぇわ\n" +
            "[03:04.29] アタシも大概だけど\n" +
            "[03:06.29] どうだっていいぜ問題はナシ\n" +
            "[03:08.50] "

    @Test
    fun parse_sync_lyrics() {
        val result = parseSyncedLyrics(dummy)
        println(result)
    }
}