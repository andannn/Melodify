/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.Boolean
import kotlin.String

@Serializable
data class LyricData(
    @SerialName(value = "id")
    val id: Long,
    @SerialName(value = "name")
    val name: String,
    @SerialName(value = "trackName")
    val trackName: String,
    @SerialName(value = "artistName")
    val artistName: String,
    @SerialName(value = "albumName")
    val albumName: String,
    @SerialName(value = "duration")
    val duration: Double,
    @SerialName(value = "instrumental")
    val instrumental: Boolean,
    @SerialName(value = "plainLyrics")
    val plainLyrics: String,
    @SerialName(value = "syncedLyrics")
    val syncedLyrics: String,
)
