/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer.model

data class MediaDataModel(
    val audioData: List<AudioData>,
    val albumData: List<AlbumData>,
    val artistData: List<ArtistData>,
    val genreData: List<GenreData>,
)
