package com.andannn.melodify.core.syncer.util

import java.nio.file.Path

fun generateHashKey(absolutePath: Path): Long {
    if (!absolutePath.isAbsolute) {
        throw IllegalArgumentException("The path must be absolute path.")
    }

    return absolutePath.toUri().toString().hashCode().toLong()
}
