package com.andannn.melodify.core.syncer.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Scan all audio file in library path.
 */
fun scanAllLibraryAudioFile(libraryPathSet: Set<String>): List<PathWithLastModifyDate> {
    return libraryPathSet.fold(emptyList()) { acc, libraryPath ->
        val data = Files.walk(Paths.get(libraryPath))
            .filter {
                Files.isRegularFile(it)
            }
            .filter {
                isAudioFile(it.toString())
            }
            .map {
                getLastModifyData(it)
            }
            .toList()
        data + acc
    }
}

private fun getLastModifyData(path: Path): PathWithLastModifyDate {
    val lastModified = Files.getLastModifiedTime(path).toMillis()
    return PathWithLastModifyDate(path.toString(), lastModified)
}

data class PathWithLastModifyDate(
    val path: String,
    val lastModified: Long,
) {
    val key = generateHashKey(path)
}
