package com.andannn.melodify.core.network

import io.ktor.client.plugins.ResponseException

inline fun <reified T> tryGetResult(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: ResponseException) {
        Result.failure(ServerResponseException(e.response.status.value, e.message.toString()))
    } catch (e: Throwable) {
        Result.failure(ServerException(e.message.toString()))
    }
