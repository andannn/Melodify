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