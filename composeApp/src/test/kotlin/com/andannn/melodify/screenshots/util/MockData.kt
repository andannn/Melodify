package com.andannn.melodify.screenshots.util

import com.andannn.melodify.core.data.model.AlbumItemModel
import com.andannn.melodify.core.data.model.AudioItemModel

val album1 =
    AlbumItemModel.DEFAULT.copy(
        id = "7466598606566714508",
        name = "Xroniàl Xéro",
        artWorkUri = "content://media/external/audio/albums/3010241435219009771",
        trackCount = 17,
    )

val album2 =
    AlbumItemModel.DEFAULT.copy(
        id = "570547186712440806",
        name = "夏草が邪魔をする",
        artWorkUri = "content://media/external/audio/albums/570547186712440806",
        trackCount = 2,
    )

val albumList =
    listOf(
        album1,
        album2,
    )

val audioList1 =
    listOf(
        AudioItemModel.DEFAULT.copy(
            id = "1000008185",
            name = "Introduction - Xursed divinitiY",
            artWorkUri = "content://media/external/audio/albums/3010241435219009771",
            modifiedDate = 1732458968,
            album = "Xroniàl Xéro",
            albumId = "3010241435219009771",
            artist = "かめりあ",
            artistId = "4435211495462583764",
            cdTrackNumber = 1,
        ),
        AudioItemModel.DEFAULT.copy(
            id = "1000008177",
            name = "Xéroa (\"préconnaiXance\" Long ver.)",
            artWorkUri = "content://media/external/audio/albums/3010241435219009771",
            modifiedDate = 1732458965,
            album = "Xroniàl Xéro",
            albumId = "3010241435219009771",
            artist = "かめりあ",
            artistId = "4435211495462583764",
            cdTrackNumber = 2,
            source = "content://media/external/audio/media/1000008177",
            extraUniqueId = "1000008177",
        ),
        AudioItemModel.DEFAULT.copy(
            id = "1000008184",
            name = "El=Dorado=",
            artWorkUri = "content://media/external/audio/albums/3010241435219009771",
            modifiedDate = 1732458968,
            album = "Xroniàl Xéro",
            albumId = "3010241435219009771",
            artist = "かめりあ",
            artistId = "4435211495462583764",
            cdTrackNumber = 3,
        ),
        AudioItemModel.DEFAULT.copy(
            id = "10000081",
            name = "Lowermost Revolt (\"Jeremiad\" Long ver.)",
            artWorkUri = "content://media/external/audio/albums/3010241435219009771",
            modifiedDate = 1732458962,
            album = "Xroniàl Xéro",
            albumId = "3010241435219009771",
            artist = "かめりあ feat. 歌劇派学生 ",
            artistId = "1387416126051424542",
            cdTrackNumber = 4,
        ),
        AudioItemModel.DEFAULT.copy(
            id = "100000818",
            name = "Abyss of Frobenioid",
            artWorkUri = "content://media/external/audio/albums/3010241435219009771",
            modifiedDate = 173245896,
            album = "Xroniàl Xéro",
            albumId = "3010241435219009771",
            artist = "かめり",
            artistId = "4435211495462583764",
            cdTrackNumber = 5,
        ),
        AudioItemModel.DEFAULT.copy(
            id = "1000008178",
            name = "Rip It Off (Now) Rip It Off",
            artWorkUri = "content://media/external/audio/albums/3010241435219009771",
            modifiedDate = 173245896,
            album = "Xroniàl Xéro",
            albumId = "3010241435219009771",
            artist = "Quarks",
            artistId = "1301074465823454527",
            cdTrackNumber = 6,
        ),
    )

val audioList2 =
    listOf(
        AudioItemModel.DEFAULT.copy(
            id = "100000823",
            name = "言って。",
            artWorkUri = "content://media/external/audio/albums/570547186712440806",
            modifiedDate = 17331203,
            album = "夏草が邪魔をする",
            albumId = "570547186712440806",
            artist = "ヨルシカ",
            artistId = "67465681098",
            cdTrackNumber = 3,
        ),
        AudioItemModel.DEFAULT.copy(
            id = "1000008229",
            name = "靴の花火",
            artWorkUri = "content://media/external/audio/albums/570547186712440806",
            modifiedDate = 17331203,
            album = "夏草が邪魔をする",
            albumId = "570547186712440806",
            artist = "ヨルシカ",
            artistId = "67465681098",
            cdTrackNumber = 6,
        ),
    )
