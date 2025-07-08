/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network

import com.andannn.melodify.core.network.model.LyricData
import com.andannn.melodify.core.network.resources.ApiRes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.get

interface LrclibService {
    /**
     * Get lyric from lrclib
     *
     * @param trackName Title of the track
     * @param artistName Name of the artist
     * @param albumName Name of the album (optional)
     * @param duration Track's duration in seconds (optional)
     */
    suspend fun getLyric(
        trackName: String,
        artistName: String,
        albumName: String? = null,
        duration: Long? = null,
    ): LyricData
}

internal class LrclibServiceImpl(
    httpClient: HttpClient,
) : LrclibService {
//
//    private val userAgent: String
//
//    init {
//        val packageManager = application.packageManager
//        val packageName = application.packageName
//
//        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
//        val packageInfo = packageManager.getPackageInfo(packageName, 0)
//        val appLabel = packageManager.getApplicationLabel(applicationInfo)
//        userAgent =
//            "${appLabel}/${packageInfo.versionName} (${Build.MANUFACTURER}; ${Build.MODEL}; ${Build.VERSION.RELEASE}) // https://github.com/andannn/Melodify"
//    }

    private val httpClient =
        httpClient.config {
            defaultRequest {
//            header("User-Agent", userAgent)
                url("https://lrclib.net/")
            }
        }

    override suspend fun getLyric(
        trackName: String,
        artistName: String,
        albumName: String?,
        duration: Long?,
    ): LyricData =
        httpClient
            .get(
                ApiRes.Get(
                    track_name = trackName,
                    artist_name = artistName,
                    album_name = albumName,
                    duration = duration,
                ),
            )
            .body()
}
