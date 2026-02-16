/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.mock

import com.andannn.melodify.domain.model.AudioItemModel
import com.andannn.melodify.domain.model.PlayListItemModel

object MockData {
    val medias =
        listOf(
            // ==========================================================
            // Group 1: The Weeknd (Same Artist, Different Albums)
            // 验证点：点击歌手 "The Weeknd" (ID: 101) 应该能看到两个专辑
            // ==========================================================
            // Album: Starboy (ID: 301)
            AudioItemModel(
                id = 2001L,
                name = "Starboy",
                artWorkUri = "content://media/external/audio/albumart/301",
                path = "/music/TheWeeknd/Starboy/01_Starboy.mp3",
                modifiedDate = 1672531200000L,
                album = "Starboy",
                albumId = 301L,
                genre = "R&B",
                genreId = 501L,
                artist = "The Weeknd",
                artistId = 101L,
                releaseYear = "2016",
                cdTrackNumber = 1,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = 2002L,
                name = "I Feel It Coming",
                artWorkUri = "content://media/external/audio/albumart/301",
                path = "/music/TheWeeknd/Starboy/02_IFeelItComing.mp3",
                modifiedDate = 1672531200000L,
                album = "Starboy",
                albumId = 301L,
                genre = "R&B",
                genreId = 501L,
                artist = "The Weeknd",
                artistId = 101L,
                releaseYear = "2016",
                cdTrackNumber = 2,
                discNumber = 1,
                source = "local",
            ),
            // Album: After Hours (ID: 302)
            AudioItemModel(
                id = 2003L,
                name = "Blinding Lights",
                artWorkUri = "content://media/external/audio/albumart/302",
                path = "/music/TheWeeknd/AfterHours/09_BlindingLights.mp3",
                modifiedDate = 1682531200000L,
                album = "After Hours",
                albumId = 302L,
                genre = "Synth-pop",
                genreId = 502L,
                artist = "The Weeknd",
                artistId = 101L, // 相同的歌手ID
                releaseYear = "2020",
                cdTrackNumber = 9,
                discNumber = 1,
                source = "local",
            ),
            // ==========================================================
            // Group 2: Adele (Same Album, Multiple Tracks)
            // 验证点：专辑 "25" (ID: 401) 详情页，音轨排序
            // ==========================================================
            AudioItemModel(
                id = 3001L,
                name = "Hello",
                artWorkUri = "content://media/external/audio/albumart/401",
                path = "/music/Adele/25/01_Hello.flac",
                modifiedDate = 1600000000000L,
                album = "25",
                albumId = 401L,
                genre = "Soul",
                genreId = 503L,
                artist = "Adele",
                artistId = 102L,
                releaseYear = "2015",
                cdTrackNumber = 1,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = 3002L,
                name = "Send My Love (To Your New Lover)",
                artWorkUri = "content://media/external/audio/albumart/401",
                path = "/music/Adele/25/02_SendMyLove.flac",
                modifiedDate = 1600000000000L,
                album = "25",
                albumId = 401L,
                genre = "Pop",
                genreId = 502L,
                artist = "Adele",
                artistId = 102L,
                releaseYear = "2015",
                cdTrackNumber = 2,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = 3003L,
                name = "When We Were Young",
                artWorkUri = "content://media/external/audio/albumart/401",
                path = "/music/Adele/25/04_When We Were Young.flac",
                modifiedDate = 1600000000000L,
                album = "25",
                albumId = 401L,
                genre = "Soul",
                genreId = 503L,
                artist = "Adele",
                artistId = 102L,
                releaseYear = "2015",
                cdTrackNumber = 4,
                discNumber = 1,
                source = "local",
            ),
            // ==========================================================
            // Group 3: Linkin Park
            // ==========================================================
            AudioItemModel(
                id = 4001L,
                name = "Numb",
                artWorkUri = "content://media/external/audio/albumart/501",
                path = "/music/LP/Meteora/13_Numb.mp3",
                modifiedDate = 1500000000000L,
                album = "Meteora",
                albumId = 601L,
                genre = "Alternative Rock",
                genreId = 504L,
                artist = "Linkin Park",
                artistId = 103L,
                releaseYear = "2003",
                cdTrackNumber = 13,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = 4002L,
                name = "Faint",
                artWorkUri = "content://media/external/audio/albumart/501",
                path = "/music/LP/Meteora/07_Faint.mp3",
                modifiedDate = 1500000000000L,
                album = "Meteora",
                albumId = 601L,
                genre = "Alternative Rock",
                genreId = 504L,
                artist = "Linkin Park",
                artistId = 103L,
                releaseYear = "2003",
                cdTrackNumber = 7,
                discNumber = 1,
                source = "local",
            ),
        )

    val playLists =
        listOf(
            PlayListItemModel(
                id = 1L,
                name = "Weekly Top Hits",
                artWorkUri = "https://picsum.photos/200/200?random=1",
                trackCount = 50,
                isFavoritePlayList = true,
            ),
            PlayListItemModel(
                id = 2L,
                name = "Deep Focus Coding",
                artWorkUri = null,
                trackCount = 120,
                isFavoritePlayList = false,
            ),
            PlayListItemModel(
                id = 3L,
                name = "Tech Talk Highlights",
                artWorkUri = "https://picsum.photos/200/200?random=2",
                trackCount = 5,
                isFavoritePlayList = true,
            ),
            PlayListItemModel(
                id = 4L,
                name = "Jazz & Soul Remastered",
                artWorkUri = "https://picsum.photos/200/200?random=3",
                trackCount = 89,
                isFavoritePlayList = false,
            ),
            PlayListItemModel(
                id = 5L,
                name = "New Empty Playlist",
                artWorkUri = null,
                trackCount = 0,
                isFavoritePlayList = false,
            ),
        )
}
