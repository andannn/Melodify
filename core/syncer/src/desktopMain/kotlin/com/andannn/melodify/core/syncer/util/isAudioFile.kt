package com.andannn.melodify.core.syncer.util

import io.github.aakira.napier.Napier
import java.io.File
import java.io.IOException
import java.nio.file.Files

fun isAudioFile(fileName: String): Boolean {
    return getMineType(fileName)?.split("/")?.firstOrNull() == "audio"
}

fun isImageFile(fileName: String): Boolean {
    return getMineType(fileName)?.split("/")?.firstOrNull() == "image"
}

fun getMineType(fileName: String): String? {
    return try {
        Files.probeContentType(File(fileName).toPath())
    } catch (e: IOException) {
        Napier.d { "failed to get mine type $e" }
        null
    }
}