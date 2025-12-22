package com.andannn.melodify.core.platform

actual fun formatTime(
    minutes: Long,
    seconds: Int,
): String = "%02d:%02d".format(minutes, seconds)
