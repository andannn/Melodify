/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.datastore

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.andannn.melodify.core.datastore.model.PreferencesKeyName
import okio.Path.Companion.toPath

internal fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() },
        migrations =
            listOf(
                CustomTabsMigration(),
            ),
    )

internal const val DATA_STORE_FILENAME = "dice.preferences_pb"

internal class CustomTabsMigration : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData[stringPreferencesKey(PreferencesKeyName.CUSTOM_TABS_KEY_NAME)] != null

    override suspend fun migrate(currentData: Preferences): Preferences {
        val newPref =
            currentData.toMutablePreferences().apply {
                remove(stringPreferencesKey(PreferencesKeyName.CUSTOM_TABS_KEY_NAME))
            }
        return newPref
    }

    override suspend fun cleanUp() {
        // no-op
    }
}
