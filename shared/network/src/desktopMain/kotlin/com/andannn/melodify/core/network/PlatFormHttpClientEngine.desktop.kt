/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import java.util.concurrent.TimeUnit

private const val TAG = "HttpClient"

internal actual val PlatformHttpClient: HttpClient =
    HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(10, TimeUnit.SECONDS)
            }
        }

        install(Logging) {
            logger =
                object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = TAG) { "HttpLogInfo: $message" }
                    }
                }
            level = LogLevel.HEADERS
        }
    }
