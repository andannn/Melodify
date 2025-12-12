/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.util

import io.github.aakira.napier.Napier
import java.io.File
import java.io.IOException
import java.nio.file.Files

internal fun isAudioFile(fileName: String): Boolean = getMineType(fileName)?.split("/")?.firstOrNull() == "audio"

internal fun getMineType(fileName: String): String? =
    try {
        Files.probeContentType(File(fileName).toPath())
    } catch (e: IOException) {
        Napier.d { "failed to get mine type $e" }
        null
    }
