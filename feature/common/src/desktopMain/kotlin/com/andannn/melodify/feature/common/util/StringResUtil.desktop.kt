package com.andannn.melodify.feature.common.util

actual fun formatTime(minutes: Long, seconds: Int) = "%02d:%02d".format(minutes, seconds)