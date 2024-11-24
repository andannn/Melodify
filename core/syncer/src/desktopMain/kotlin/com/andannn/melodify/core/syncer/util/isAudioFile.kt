package com.andannn.melodify.core.syncer.util

import java.io.File
import java.io.IOException
import java.nio.file.Files

fun isAudioFile(fileName: String): Boolean {
    return try {
        val mimeType = Files.probeContentType(File(fileName).toPath())
        println(mimeType)
        println(mimeType.split("/").firstOrNull())
        mimeType.split("/").firstOrNull() == "audio"
    } catch (e: IOException) {
        false
    }
}