/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.util

import io.github.aakira.napier.Napier
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Scan all audio file in library path.
 *
 * @param pathSet the library path set.
 */
internal fun scanAllAudioFile(pathSet: Set<String>): List<Path> =
    pathSet.fold(emptyList()) { acc, libraryPath ->
        val data =
            Files
                .walk(Paths.get(libraryPath))
                .filter {
                    Files.isRegularFile(it)
                }.filter {
                    isAudioFile(it.toString())
                }.toList()
        data + acc
    }

internal fun isAudioFile(fileName: String): Boolean = getMineType(fileName)?.split("/")?.firstOrNull() == "audio"

private fun getMineType(fileName: String): String? =
    try {
        Files.probeContentType(File(fileName).toPath())
    } catch (e: IOException) {
        Napier.d { "failed to get mine type $e" }
        null
    }
