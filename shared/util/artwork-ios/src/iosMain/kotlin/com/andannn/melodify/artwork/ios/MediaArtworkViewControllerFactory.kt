/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.artwork.ios

import platform.UIKit.UIViewController

interface MediaArtworkViewControllerFactory {
    fun createMediaArtworkViewController(persistentID: Long): UIViewController
}
