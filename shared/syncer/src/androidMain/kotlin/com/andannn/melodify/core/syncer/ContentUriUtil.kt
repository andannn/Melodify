/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

import android.net.Uri
import android.provider.MediaStore

fun Uri.isAudioUri(): Boolean =
    this.toString().startsWith(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            .toString(),
    )

fun Uri.isVideoUri(): Boolean =
    this.toString().startsWith(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            .toString(),
    )
