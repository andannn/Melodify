package com.andannn.melodify.core.syncer.util

fun generateHashKey(absolutePath: String, lastModifiedDate: Long): Long {
    return (absolutePath + lastModifiedDate.toString()).hashCode().toLong()
}