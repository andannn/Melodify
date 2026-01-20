/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.service.siren.model

import kotlinx.serialization.Serializable

@Serializable
internal data class MonsterSirenResponse<T>(
    val code: Int,
    val msg: String,
    val data: T,
)
