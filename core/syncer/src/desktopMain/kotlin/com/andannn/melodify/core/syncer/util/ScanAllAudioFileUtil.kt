package com.andannn.melodify.core.syncer.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Scan all audio file in library path.
 *
 * @param pathSet the library path set.
 */
fun scanAllAudioFile(pathSet: Set<String>): List<Path> {
    return pathSet.fold(emptyList()) { acc, libraryPath ->
        val data =
            Files.walk(Paths.get(libraryPath))
                .filter {
                    Files.isRegularFile(it)
                }
                .filter {
                    isAudioFile(it.toString())
                }
                .toList()
        data + acc
    }
}
