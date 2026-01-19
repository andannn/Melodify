package com.andannn.melodify.core.network

open class ServerException(
    override val message: String,
) : IllegalStateException(message)

/**
 * Represents an error response with status code.
 */
open class ServerResponseException(
    val statusCode: Int,
    override val message: String,
) : ServerException(message)
