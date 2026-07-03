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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
object A : AbstractEncoder() {
    init {
    }
    override val serializersModule: SerializersModule
        get() = TODO("Not yet implemented")
}
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
