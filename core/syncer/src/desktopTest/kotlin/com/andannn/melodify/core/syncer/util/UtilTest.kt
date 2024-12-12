package com.andannn.melodify.core.syncer.util

import kotlin.test.Test
import kotlin.test.assertEquals
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
            val audioData = extractTagFromAudioFile("src/desktopTest/testfile/test_audio.mp3")
            println(audioData)
            assertNotNull(audioData)
        }.also { println("consumed time: $it") }
    }

    @Test
    fun scan_all_library_audio_file() {
        measureTime {
            val audioData = scanAllAudioFile(
                setOf(
                    "src/desktopTest/"
                )
            )
            println(audioData)
            assertNotNull(audioData)
        }.also { println("consumed time: $it") }
    }
}