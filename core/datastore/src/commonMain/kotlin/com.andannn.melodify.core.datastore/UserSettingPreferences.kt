package com.andannn.melodify.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
                )
            }


    suspend fun setPlayMode(playMode: Int) {
        preferences.edit { preferences ->
            preferences[intPreferencesKey(PreferencesKeyName.PLAY_MODE_KEY_NAME)] = playMode
        }
    }

    suspend fun setIsShuffle(isShuffle: Boolean) {
        preferences.edit { preferences ->
            preferences[booleanPreferencesKey(PreferencesKeyName.IS_SHUFFLE_KEY_NAME)] = isShuffle
        }
    }

    suspend fun setMediaPreviewMode(mediaPreviewMode: Int) {
        preferences.edit { preferences ->
            preferences[intPreferencesKey(PreferencesKeyName.MEDIA_PREVIEW_MODE_KEY_NAME)] =
                mediaPreviewMode
        }
    }
}