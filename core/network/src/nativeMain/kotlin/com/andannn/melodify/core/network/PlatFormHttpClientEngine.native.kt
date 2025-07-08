/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

internal actual val PlatformHttpClient: HttpClient = HttpClient(CIO)
