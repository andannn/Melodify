/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.andannn.melodify.core.datastore.model.PlatModeValues
import com.andannn.melodify.core.datastore.model.PreferencesKeyName
import com.andannn.melodify.core.datastore.model.PreviewModeValues
import com.andannn.melodify.core.datastore.model.UserSettingPref
import kotlinx.coroutines.flow.map

class UserSettingPreferences(
    private val preferences: DataStore<Preferences>,
) {
    val userDate =
        preferences.data
            .map { preferences ->
                UserSettingPref(
                    playMode =
                        preferences[intPreferencesKey(PreferencesKeyName.PLAY_MODE_KEY_NAME)]
                            ?: PlatModeValues.PLAT_MODE_REPEAT_ONE_VALUE,
                    isShuffle =
                        preferences[booleanPreferencesKey(PreferencesKeyName.IS_SHUFFLE_KEY_NAME)]
                            ?: false,
                    mediaPreviewMode =
                        preferences[intPreferencesKey(PreferencesKeyName.MEDIA_PREVIEW_MODE_KEY_NAME)]
                            ?: PreviewModeValues.LIST_PREVIEW_VALUE,
                    libraryPath =
                        preferences[stringSetPreferencesKey(PreferencesKeyName.LIBRARY_PATH_KEY_NAME)]
                            ?: emptySet(),
                    lastSuccessfulSyncTime =
                        preferences[longPreferencesKey(PreferencesKeyName.LAST_SUCCESSFUL_SYNC_TIME_KEY_NAME)],
                    defaultSortRule =
                        preferences[stringPreferencesKey(PreferencesKeyName.DEFAULT_SORT_RULE_KEY_NAME)],
                )
            }

    suspend fun setMediaPreviewMode(mediaPreviewMode: Int) {
        preferences.edit { preferences ->
            preferences[intPreferencesKey(PreferencesKeyName.MEDIA_PREVIEW_MODE_KEY_NAME)] =
                mediaPreviewMode
        }
    }

    suspend fun setLibraryPath(libraryPathList: Set<String>) {
        preferences.edit { preferences ->
            preferences[stringSetPreferencesKey(PreferencesKeyName.LIBRARY_PATH_KEY_NAME)] =
                libraryPathList
        }
    }

    suspend fun setLastSuccessfulSyncTime(lastSuccessfulSyncTime: Long) {
        preferences.edit { preferences ->
            preferences[longPreferencesKey(PreferencesKeyName.LAST_SUCCESSFUL_SYNC_TIME_KEY_NAME)] =
                lastSuccessfulSyncTime
        }
    }

    suspend fun setDefaultSortRule(defaultSortRule: String) {
        preferences.edit { preferences ->
            preferences[stringPreferencesKey(PreferencesKeyName.DEFAULT_SORT_RULE_KEY_NAME)] = defaultSortRule
        }
    }
}
