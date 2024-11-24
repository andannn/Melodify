package com.andannn.melodify.core.syncer.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.measureTime

class UtilTest {

    @Test
    fun is_audio_file_test() {
        assertFalse { isAudioFile("./testfile/some.abc") }
        assertTrue { isAudioFile("./testfile/text_with_mp3.mp3") }
    }

    @Test
    fun extract_tag_from_audio_file_test() {
        measureTime {
            val audioData = extractTagFromAudioFile("src/desktopTest/testfile/track_11_Harinezumi.flac")
            println(audioData)
            assertNotNull(audioData)
        }.also { println("consumed time: $it") }
    }
}