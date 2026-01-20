/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.service.siren

import com.andannn.melodify.core.network.ServerException
import com.andannn.melodify.core.network.service.siren.model.Album
import com.andannn.melodify.core.network.service.siren.model.MonsterSirenResponse
import com.andannn.melodify.core.network.service.siren.model.Song
import com.andannn.melodify.core.network.tryGetResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.URLProtocol

interface MonsterSirenService {
    suspend fun getAlbums(): Result<List<Album>>

    suspend fun getAlbumDetail(cid: String): Result<Album>

    suspend fun getSourceUrlOfSong(cid: String): Result<String>
}

internal class MonsterSirenServiceImpl(
    client: HttpClient,
) : MonsterSirenService {
    val httpClient by lazy {
        client.config {
            defaultRequest {
                url {
                    host = "monster-siren.hypergryph.com/api"
                    protocol = URLProtocol.HTTPS
                }
            }
        }
    }

    override suspend fun getAlbums() =
        tryGetResult {
            val response = httpClient.get("/albums").body<MonsterSirenResponse<List<Album>>>()
            response.data
        }

    override suspend fun getAlbumDetail(cid: String) =
        tryGetResult {
            val response = httpClient.get("/album/$cid/detail").body<MonsterSirenResponse<Album>>()
            response.data
        }

    override suspend fun getSourceUrlOfSong(cid: String) =
        tryGetResult {
            val response = httpClient.get("/song/$cid").body<MonsterSirenResponse<Song>>()
            response.data.sourceUrl ?: throw ServerException("sourceUrl is null")
        }
}
