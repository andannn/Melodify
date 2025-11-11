/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.datastore.model

object PlatModeValues {
    /**
     * `PLAT_MODE_REPEAT_ONE = 0;`
     */
    const val PLAT_MODE_REPEAT_ONE_VALUE: Int = 0

    /**
     * `PLAT_MODE_REPEAT_OFF = 1;`
     */
    const val PLAT_MODE_REPEAT_OFF_VALUE: Int = 1

    /**
     * `PLAT_MODE_REPEAT_ALL = 2;`
     */
    const val PLAT_MODE_REPEAT_ALL_VALUE: Int = 2
}

object PreviewModeValues {
    /**
     * `LIST_PREVIEW = 0;`
     */
    const val LIST_PREVIEW_VALUE: Int = 0

    /**
     * `GRID_PREVIEW = 1;`
     */
    const val GRID_PREVIEW_VALUE: Int = 1
}

object DefaultPresetValues {
    const val ALBUM_ASC_VALUE: Int = 0
    const val ARTIST_ASC_VALUE: Int = 1
    const val TITLE_ASC_VALUE: Int = 2
    const val ARTIST_ALBUM_ASC_VALUE: Int = 3
    const val BUCKET_NAME_ASC_VALUE: Int = 4
}
