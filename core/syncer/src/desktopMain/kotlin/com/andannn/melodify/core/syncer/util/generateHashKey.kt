package com.andannn.melodify.core.syncer.util

fun generateHashKey(absolutePath: String): Long {
    return absolutePath.hashCode().toLong()
}