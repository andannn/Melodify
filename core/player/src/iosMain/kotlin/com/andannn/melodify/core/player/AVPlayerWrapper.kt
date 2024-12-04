package com.andannn.melodify.core.player

import io.github.aakira.napier.Napier
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.play
import platform.Foundation.NSURL

private const val TAG = "AVPlayerWrapper"

interface AVPlayerWrapper {
    fun playMediaList(mediaList: List<String>, index: Int)
}

class AVPlayerWrapperWrapperImpl: AVPlayerWrapper {
    override fun playMediaList(mediaList: List<String>, index: Int) {
        Napier.d(tag = TAG) { "playMediaList mediaList $mediaList, index: $index" }
        val player = AVPlayer(
            NSURL(string = mediaList[index])
        )
        player.play()
    }
}