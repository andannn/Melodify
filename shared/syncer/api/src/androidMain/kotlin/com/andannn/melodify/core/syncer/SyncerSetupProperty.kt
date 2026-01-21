/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.syncer

data class SyncerSetupProperty(
    val type: ScannerType,
    val needNetwork: Boolean,
    val needLocalMediaPermission: Boolean,
    val backgroundSyncIntervalHour: Long,
) {
    companion object {
        fun buildPropertyByFlavor(flavor: String): SyncerSetupProperty =
            when (flavor) {
                "local" -> {
                    SyncerSetupProperty(
                        type = ScannerType.LOCAL,
                        needLocalMediaPermission = true,
                        needNetwork = false,
                        backgroundSyncIntervalHour = 12,
                    )
                }

                "monster_siren" -> {
                    SyncerSetupProperty(
                        type = ScannerType.MONSTER_SIREN,
                        needLocalMediaPermission = false,
                        needNetwork = true,
                        backgroundSyncIntervalHour = 240,
                    )
                }

                else -> {
                    error("Invalid $flavor")
                }
            }
    }
}
