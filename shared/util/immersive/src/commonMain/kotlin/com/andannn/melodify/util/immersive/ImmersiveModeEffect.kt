/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.util.immersive

import androidx.compose.runtime.Composable

/**
 * Set immersive mode when this composable is active.
 * Set immersive mode off when this composable is disposed.
 */
@Composable
expect fun ImmersiveModeEffect()
