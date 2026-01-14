package com.andannn.melodify.shared.compose.common.mock

import com.andannn.melodify.domain.model.AudioItemModel

object MockData {
    val medias = listOf(
        AudioItemModel(
            id = "1001",
            name = "Anti-Hero",
            artWorkUri = "content://media/external/audio/albumart/111",
            path = "/storage/emulated/0/Music/Midnights/01_Anti_Hero.mp3",
            modifiedDate = 1672531200000L, // 2023-01-01
            album = "Midnights",
            albumId = "alb_midnights",
            genre = "Pop",
            genreId = "gen_pop",
            artist = "Taylor Swift",
            artistId = "art_ts",
            releaseYear = "2022",
            cdTrackNumber = 1,
            discNumber = 1,
            source = "local_file",
            extraUniqueId = "uid_001",
            trackCount = 1
        ),

        AudioItemModel(
            id = "1002",
            name = "Bohemian Rhapsody",
            artWorkUri = "content://media/external/audio/albumart/222",
            path = "/storage/emulated/0/Music/Queen/Bohemian_Rhapsody.flac",
            modifiedDate = 1609459200000L,
            album = "A Night at the Opera",
            albumId = "alb_opera",
            genre = "Rock",
            genreId = "gen_rock",
            artist = "Queen",
            artistId = "art_queen",
            releaseYear = "1975",
            cdTrackNumber = 11,
            discNumber = 1,
            source = "local_file",
            extraUniqueId = null,
            trackCount = 1
        ),

        AudioItemModel(
            id = "1003",
            name = "Podcast Episode #42",
            artWorkUri = null, // 测试 UI 加载占位符
            path = "/storage/emulated/0/Podcasts/tech_talk_42.mp3",
            modifiedDate = 1704067200000L,
            album = "Tech Talk Daily",
            albumId = "alb_podcast",
            genre = "Podcast",
            genreId = "gen_podcast",
            artist = "Tech Host",
            artistId = "art_host",
            releaseYear = "2024",
            cdTrackNumber = 42,
            discNumber = 1,
            source = "download",
            trackCount = 1
        ),

        AudioItemModel(
            // ID 前缀匹配 companion object 中的 INVALID_ID_PREFIX
            id = "${AudioItemModel.INVALID_ID_PREFIX}9999",
            name = "Deleted Song",
            artWorkUri = null,
            path = "/storage/emulated/0/Music/deleted_file.mp3",
            modifiedDate = 0L,
            album = "Unknown Album",
            albumId = "unknown",
            genre = "Unknown",
            genreId = "unknown",
            artist = "Unknown Artist",
            artistId = "unknown",
            releaseYear = "0",
            cdTrackNumber = 0,
            discNumber = 0,
            source = "cache",
            trackCount = 0
        )
    )
}