/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.common.mock

import com.andannn.melodify.domain.model.AudioItemModel

object MockData {
    val medias =
        listOf(
            // ==========================================================
            // Group 1: The Weeknd (Same Artist, Different Albums)
            // 验证点：点击歌手 "The Weeknd" 应该能看到 "Starboy" 和 "After Hours" 两个专辑
            // ==========================================================
            // Album: Starboy
            AudioItemModel(
                id = "2001",
                name = "Starboy",
                artWorkUri = "content://media/external/audio/albumart/301",
                path = "/music/TheWeeknd/Starboy/01_Starboy.mp3",
                modifiedDate = 1672531200000L,
                album = "Starboy",
                albumId = "alb_starboy", // 关键：ID相同
                genre = "R&B",
                genreId = "gen_rnb",
                artist = "The Weeknd",
                artistId = "art_weeknd", // 关键：ID相同
                releaseYear = "2016",
                cdTrackNumber = 1,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = "2002",
                name = "I Feel It Coming",
                artWorkUri = "content://media/external/audio/albumart/301",
                path = "/music/TheWeeknd/Starboy/02_IFeelItComing.mp3",
                modifiedDate = 1672531200000L,
                album = "Starboy",
                albumId = "alb_starboy",
                genre = "R&B",
                genreId = "gen_rnb",
                artist = "The Weeknd",
                artistId = "art_weeknd",
                releaseYear = "2016",
                cdTrackNumber = 2,
                discNumber = 1,
                source = "local",
            ),
            // Album: After Hours (同歌手，不同专辑)
            AudioItemModel(
                id = "2003",
                name = "Blinding Lights",
                artWorkUri = "content://media/external/audio/albumart/302",
                path = "/music/TheWeeknd/AfterHours/09_BlindingLights.mp3",
                modifiedDate = 1682531200000L,
                album = "After Hours",
                albumId = "alb_after_hours", // 不同的专辑ID
                genre = "Synth-pop",
                genreId = "gen_pop",
                artist = "The Weeknd",
                artistId = "art_weeknd", // 相同的歌手ID
                releaseYear = "2020",
                cdTrackNumber = 9,
                discNumber = 1,
                source = "local",
            ),
            // ==========================================================
            // Group 2: Adele (Same Album, Multiple Tracks)
            // 验证点：进入 "25" 专辑详情页，应该按 cdTrackNumber 排序显示这3首歌
            // ==========================================================
            AudioItemModel(
                id = "3001",
                name = "Hello",
                artWorkUri = "content://media/external/audio/albumart/401",
                path = "/music/Adele/25/01_Hello.flac",
                modifiedDate = 1600000000000L,
                album = "25",
                albumId = "alb_25",
                genre = "Soul",
                genreId = "gen_soul",
                artist = "Adele",
                artistId = "art_adele",
                releaseYear = "2015",
                cdTrackNumber = 1,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = "3002",
                name = "Send My Love (To Your New Lover)",
                artWorkUri = "content://media/external/audio/albumart/401",
                path = "/music/Adele/25/02_SendMyLove.flac",
                modifiedDate = 1600000000000L,
                album = "25",
                albumId = "alb_25",
                genre = "Pop",
                genreId = "gen_pop",
                artist = "Adele",
                artistId = "art_adele",
                releaseYear = "2015",
                cdTrackNumber = 2,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = "3003",
                name = "When We Were Young",
                artWorkUri = "content://media/external/audio/albumart/401",
                path = "/music/Adele/25/04_WhenWeWereYoung.flac",
                modifiedDate = 1600000000000L,
                album = "25",
                albumId = "alb_25",
                genre = "Soul",
                genreId = "gen_soul",
                artist = "Adele",
                artistId = "art_adele",
                releaseYear = "2015",
                cdTrackNumber = 4,
                discNumber = 1,
                source = "local",
            ),
            // ==========================================================
            // Group 3: Linkin Park (Another Artist check)
            // 验证点：列表滑动时，不同的 Artist 应该正确分组
            // ==========================================================
            AudioItemModel(
                id = "4001",
                name = "Numb",
                artWorkUri = "content://media/external/audio/albumart/501",
                path = "/music/LP/Meteora/13_Numb.mp3",
                modifiedDate = 1500000000000L,
                album = "Meteora",
                albumId = "alb_meteora",
                genre = "Alternative Rock",
                genreId = "gen_rock",
                artist = "Linkin Park",
                artistId = "art_lp",
                releaseYear = "2003",
                cdTrackNumber = 13,
                discNumber = 1,
                source = "local",
            ),
            AudioItemModel(
                id = "4002",
                name = "Faint",
                artWorkUri = "content://media/external/audio/albumart/501",
                path = "/music/LP/Meteora/07_Faint.mp3",
                modifiedDate = 1500000000000L,
                album = "Meteora",
                albumId = "alb_meteora",
                genre = "Alternative Rock",
                genreId = "gen_rock",
                artist = "Linkin Park",
                artistId = "art_lp",
                releaseYear = "2003",
                cdTrackNumber = 7,
                discNumber = 1,
                source = "local",
            ),
        )
}
