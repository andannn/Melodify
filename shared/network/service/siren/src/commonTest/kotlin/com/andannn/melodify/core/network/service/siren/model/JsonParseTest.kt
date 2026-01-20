/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.network.service.siren.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonParseTest {
    private val jsonParser =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

    @Test
    fun testParseAlbumsResponseJson() {
        val json =
            """
            {
                "code": 0,
                "msg": "",
                "data": [
                    {
                        "cid": "0235",
                        "name": "雅赛努斯复仇记OST",
                        "coverUrl": "https://web.hycdn.cn/siren/pic/20260111/96f46b87053ff801a4780a16e112f74c.jpg",
                        "artistes": [
                            "塞壬唱片-MSR"
                        ]
                    },
                    {
                        "cid": "8921",
                        "name": "Wanna Know Me? (Too Bad)",
                        "coverUrl": "https://web.hycdn.cn/siren/pic/20260109/7d2b4d1d8dfa8dd9b26532ea35447712.png",
                        "artistes": [
                            "塞壬唱片-MSR"
                        ]
                    }
                ]
            }
            """.trimIndent()
        jsonParser.decodeFromString<MonsterSirenResponse<List<Album>>>(json).also {
            assertEquals(0, it.code)
            assertEquals("", it.msg)
            assertEquals(2, it.data.size)
            assertEquals("0235", it.data[0].cid)
            assertEquals("雅赛努斯复仇记OST", it.data[0].name)
            assertEquals("8921", it.data[1].cid)
            assertEquals("Wanna Know Me? (Too Bad)", it.data[1].name)
        }
    }

    @Test
    fun testParseAlbumDetail() {
        val json =
            """
            {
                "code": 0,
                "msg": "",
                "data": {
                    "cid": "0235",
                    "name": "雅赛努斯复仇记OST",
                    "intro": "也总有一些言语无法慰藉的苦痛，需要一场盛大的复仇来纾解。",
                    "belong": "arknights",
                    "coverUrl": "https://web.hycdn.cn/siren/pic/20260111/96f46b87053ff801a4780a16e112f74c.jpg",
                    "coverDeUrl": "https://web.hycdn.cn/siren/pic/20260111/1451764d4d9ba252cdf36c6e6d3845bc.jpg",
                    "songs": [
                        {
                            "cid": "461118",
                            "name": "Beneath The Cloudless Sky",
                            "artistes": [
                                "塞壬唱片-MSR"
                            ]
                        },
                        {
                            "cid": "125036",
                            "name": "Dance Of Athenius",
                            "artistes": [
                                "塞壬唱片-MSR"
                            ]
                        }
                    ]
                }
            }
            """.trimIndent()
        jsonParser.decodeFromString<MonsterSirenResponse<Album>>(json).also {
            assertEquals(0, it.code)
            assertEquals("", it.msg)
            assertEquals("0235", it.data.cid)
            assertEquals("雅赛努斯复仇记OST", it.data.name)
            assertEquals(
                "https://web.hycdn.cn/siren/pic/20260111/96f46b87053ff801a4780a16e112f74c.jpg",
                it.data.coverUrl,
            )

            assertEquals(2, it.data.songs.size)

            assertEquals("461118", it.data.songs[0].cid)
            assertEquals("Beneath The Cloudless Sky", it.data.songs[0].name)
            assertEquals("塞壬唱片-MSR", it.data.songs[0].artistes[0])

            assertEquals("125036", it.data.songs[1].cid)
            assertEquals("Dance Of Athenius", it.data.songs[1].name)
            assertEquals("塞壬唱片-MSR", it.data.songs[1].artistes[0])
        }
    }

    @Test
    fun testParseSource() {
        val json =
            """
            {
                "code": 0,
                "msg": "",
                "data": {
                    "cid": "461118",
                    "name": "Beneath The Cloudless Sky",
                    "albumCid": "0235",
                    "sourceUrl": "https://res01.hycdn.cn/a3b8a177e6a7fc1481b533a01e51af99/696F3A78/siren/audio/20260111/4f558b6849cbe70f4cb8320a0725661f.wav",
                    "lyricUrl": null,
                    "mvUrl": null,
                    "mvCoverUrl": null,
                    "artists": [
                        "塞壬唱片-MSR"
                    ]
                }
            }
            """.trimIndent()
        jsonParser.decodeFromString<MonsterSirenResponse<Song>>(json).also {
            assertEquals(0, it.code)
            assertEquals("", it.msg)
            assertEquals("461118", it.data.cid)
            assertEquals(
                "https://res01.hycdn.cn/a3b8a177e6a7fc1481b533a01e51af99/696F3A78/siren/audio/20260111/4f558b6849cbe70f4cb8320a0725661f.wav",
                it.data.sourceUrl,
            )
        }
    }
}
