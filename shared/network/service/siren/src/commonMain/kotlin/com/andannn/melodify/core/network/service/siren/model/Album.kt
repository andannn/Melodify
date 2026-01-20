/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.service.siren.model

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val cid: String,
    val name: String,
    val coverUrl: String,
    val intro: String? = null,
    val belong: String? = null,
    val coverDeUrl: String? = null,
    val artistes: List<String> = emptyList(),
    val songs: List<Song> = emptyList(),
)
