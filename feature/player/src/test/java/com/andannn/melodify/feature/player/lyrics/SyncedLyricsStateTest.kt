package com.andannn.melodify.feature.player.lyrics
import com.andannn.melodify.feature.player.ui.shrinkable.bottom.lyrics.parseSyncedLyrics
import org.junit.Test

class ParseSyncLyricsTest {
    private val dummy = "[00:00.55] 正しさとは 愚かさとは\n" +
            "[00:03.72] それが何か見せつけてやる\n" +
            "[00:08.78] \n" +
            "[00:16.80] ちっちゃな頃から優等生\n"

    @Test
    fun parse_sync_lyrics() {
        val result = parseSyncedLyrics(dummy)
        println(result)
    }
}
