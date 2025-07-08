package com.andannn.melodify.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal val clientBuilder: () -> HttpClient = {
    PlatformHttpClient.config {
        commonConfig()
    }
}

private fun HttpClientConfig<*>.commonConfig() {
    expectSuccess = true

    install(Resources)
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
            },
        )
    }
}
