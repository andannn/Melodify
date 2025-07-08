/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.resources

import io.ktor.resources.Resource

@Resource("/api")
class ApiRes {
    @Resource("/get")
    class Get(
        val parent: ApiRes = ApiRes(),
        val track_name: String,
        val artist_name: String,
        val album_name: String? = null,
        val duration: Long? = null,
    )
}
