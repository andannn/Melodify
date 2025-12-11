package com.andannn.melodify.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual val PlatformHttpClient: HttpClient = HttpClient(Darwin)
