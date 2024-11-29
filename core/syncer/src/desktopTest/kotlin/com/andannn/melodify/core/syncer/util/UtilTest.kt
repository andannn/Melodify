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
            val audioData = scanAllLibraryAudioFile(
                setOf(
                    "src/desktopTest/"
                )
            )
            println(audioData)
            assertNotNull(audioData)
        }.also { println("consumed time: $it") }
    }

    @Test
    fun test_to_file_uri() {
        assertEquals(
            "file:///Volumes/PS2000/Music/2019.12.31%20%5BRDWL-0030%5D%20%E5%BD%81%20%5BC97%5D/%E5%87%8B%E5%8F%B6%E6%A3%95%20-%20%E5%BD%81.flac",
            toFileUrl("/Volumes/PS2000/Music/2019.12.31 [RDWL-0030] 彁 [C97]/凋叶棕 - 彁.flac")
        )
    }
}