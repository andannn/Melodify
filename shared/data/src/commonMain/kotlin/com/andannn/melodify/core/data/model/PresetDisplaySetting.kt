/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.model

enum class PresetDisplaySetting(
    val displaySetting: DisplaySetting,
) {
    AlbumAsc(
        displaySetting = DisplaySetting.Preset.AlbumASC,
    ),
    ArtistAsc(
        displaySetting = DisplaySetting.Preset.ArtistASC,
    ),
    TitleNameAsc(
        displaySetting = DisplaySetting.Preset.TitleASC,
    ),
    ArtistAlbumASC(
        displaySetting = DisplaySetting.Preset.ArtistAlbumASC,
    ),
}
