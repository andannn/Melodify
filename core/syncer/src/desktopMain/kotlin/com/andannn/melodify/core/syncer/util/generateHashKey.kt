package com.andannn.melodify.core.syncer.util

import java.net.URLEncoder
import java.nio.file.Paths

fun generateHashKey(absolutePath: String): Long {
    return (convertAbsoluteFilePathToFileUri(absolutePath)).hashCode().toLong()
}

internal fun convertAbsoluteFilePathToFileUri(path: String): String {
    val file = Paths.get(path)
    val encodedPath = file.joinToString("/") {
        URLEncoder.encode(it.toString(), "UTF-8").replace("+", "%20")
    }
    return "file:///$encodedPath"
}