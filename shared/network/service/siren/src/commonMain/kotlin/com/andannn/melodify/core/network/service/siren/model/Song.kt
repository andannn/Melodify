/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.service.siren.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val cid: String,
    val name: String,
    val artistes: List<String> = emptyList(),
    val albumCid: String? = null,
    val sourceUrl: String? = null,
    val lyricUrl: String? = null,
    val mvUrl: String? = null,
    val mvCoverUrl: String? = null,
)
