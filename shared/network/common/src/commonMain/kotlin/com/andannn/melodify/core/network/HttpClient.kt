/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClientModule =
    module {
        single<HttpClient> {
            PlatformHttpClient.config {
                commonConfig()
            }
        }
    }

private fun HttpClientConfig<*>.commonConfig() {
    expectSuccess = true

    install(Resources)
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            },
        )
    }

    install(Logging) {
        logger =
            object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = "HTTP-SERVICE") { "HttpLogInfo: $message" }
                }
            }
        level = LogLevel.HEADERS
    }
}
