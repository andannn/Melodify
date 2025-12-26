/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.andannn.melodify.util.volumn

import kotlinx.cinterop.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.AVFAudio.*
import platform.CoreGraphics.CGRectZero
import platform.Foundation.*
import platform.MediaPlayer.*
import platform.UIKit.*
import platform.darwin.NSObject
import platform.darwin.sel_registerName

// TODO: Test volumn controller
@OptIn(ExperimentalForeignApi::class)
class IosVolumeController : VolumeController {
    init {
        // iOS 需要激活 AudioSession 才能准确获取音量信息
        val session = AVAudioSession.sharedInstance()
        try {
            session.setActive(true, null)
        } catch (e: Exception) {
            // 记录错误
            println("Failed to activate audio session: $e")
        }
    }

    // iOS 音量范围是 0.0 - 1.0。
    // 为了适配 Android 的整数逻辑，我们将 0.0-1.0 映射为 0-100。
    override fun getMaxVolume(): Int = 100

    override fun getCurrentVolume(): Int {
        val session = AVAudioSession.sharedInstance()
        // 将 float (0.5) 转为 int (50)
        return (session.outputVolume * 100).toInt()
    }

    override fun getCurrentVolumeFlow(): Flow<Int> =
        callbackFlow {
            // 初始值
            trySend(getCurrentVolume())

            val observer =
                object : NSObject() {
                    // 定义回调函数
                    @Suppress("UNUSED_PARAMETER")
                    fun onVolumeChanged(notification: NSNotification) {
                        // 当收到通知时，读取最新音量
                        val currentVol = getCurrentVolume()
                        trySend(currentVol)
                    }
                }

            // iOS 没有公开的 "Volume Changed" 通用通知，
            // 但 "AVSystemController_SystemVolumeDidChangeNotification" 是业界通用的“私有”通知名，
            // 几乎所有 iOS 音量监听库都用它。
            // 另一种正规方法是 KVO 监听 AVAudioSession.outputVolume，但在 Kotlin Native 中写 KVO 比较繁琐。
            val notificationName = "AVSystemController_SystemVolumeDidChangeNotification"

            NSNotificationCenter.defaultCenter.addObserver(
                observer,
                selector = sel_registerName("onVolumeChanged:"),
                name = notificationName,
                `object` = null,
            )

            awaitClose {
                NSNotificationCenter.defaultCenter.removeObserver(observer)
            }
        }

    override fun setVolume(volumeIndex: Int) {
        // 1. 将 0-100 映射回 0.0-1.0
        val safeVolume = volumeIndex.coerceIn(0, 100).toFloat() / 100f

        // 2. iOS Hack: 使用 MPVolumeView 修改系统音量
        // MPVolumeView 是系统提供的音量滑块。我们要找到里面的 UISlider 并修改它的 value。
        // 这必须在主线程执行。
        NSOperationQueue.mainQueue.addOperationWithBlock {
            val volumeView = MPVolumeView(frame = CGRectZero.readValue())

            // 遍历子视图找到 UISlider
            for (view in volumeView.subviews) {
                if (view is UISlider) {
                    // 设置音量
                    // 注意：这不会显示系统自带的音量 HUD（弹窗），这通常是期望的行为
                    view.setValue(safeVolume, animated = false)
                    // 触发事件以确保系统应用该更改
                    view.sendActionsForControlEvents(UIControlEventTouchUpInside)
                    break
                }
            }
        }
    }
}
