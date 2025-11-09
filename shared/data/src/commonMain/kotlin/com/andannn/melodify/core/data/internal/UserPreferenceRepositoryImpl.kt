/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import com.andannn.melodify.core.data.UserPreferenceRepository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.DisplaySetting
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.model.PresetDisplaySetting
import com.andannn.melodify.core.data.model.TabKind
import com.andannn.melodify.core.data.model.UserSetting
import com.andannn.melodify.core.data.model.isAudio
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.entity.CustomTabEntity
import com.andannn.melodify.core.database.entity.CustomTabType
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.datastore.model.DefaultPresetValues
import com.andannn.melodify.core.datastore.model.PreviewModeValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

internal class UserPreferenceRepositoryImpl(
    private val preferences: UserSettingPreferences,
    private val userDataDao: UserDataDao,
) : UserPreferenceRepository {
    override val userSettingFlow: Flow<UserSetting> =
        preferences.userDate.map {
            UserSetting(
                mediaPreviewMode = it.mediaPreviewMode.toMediaPreviewMode(),
                libraryPath = it.libraryPath,
                lastSuccessfulSyncTime = it.lastSuccessfulSyncTime,
                defaultAudioPresetDisplaySetting = it.defaultAudioSortRule?.toDefaultPresetRule(),
                defaultVideoPresetDisplaySetting = it.defaultVideoSortRule?.toDefaultPresetRule(),
            )
        }

    override val currentCustomTabsFlow: Flow<List<CustomTab>> =
        userDataDao
            .getCustomTabsFlow()
            .map { it.mapToCustomTabModel().filterNotNull() }

    override suspend fun addNewCustomTab(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    ) {
        userDataDao.insertCustomTab(
            CustomTabEntity(
                externalId = externalId,
                name = tabName,
                type = tabKind.toEntityName(),
                sortOrder = (userDataDao.getMaxSortOrder() ?: 1) + 1,
            ),
        )
    }

    override suspend fun isTabExist(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    ): Boolean =
        userDataDao.isTabExist(
            externalId = externalId,
            name = tabName,
            type = tabKind.toEntityName(),
        )

    override suspend fun deleteCustomTab(tab: CustomTab) {
        userDataDao.deleteCustomTab(tabId = tab.tabId)
    }

    override suspend fun addLibraryPath(path: String): Boolean {
        if (!isPathValid(path)) return false

        val currentPaths = preferences.userDate.first().libraryPath
        preferences.setLibraryPath(currentPaths + path)

        return true
    }

    override suspend fun deleteLibraryPath(path: String): Boolean {
        val currentPaths = preferences.userDate.first().libraryPath
        if (currentPaths.contains(path).not()) {
            return false
        }

        preferences.setLibraryPath(currentPaths - path)
        return true
    }

    override suspend fun addSearchHistory(searchHistory: String) {
        userDataDao.upsertSearchHistory(
            listOf(
                SearchHistoryEntity(
                    searchDate = Clock.System.now().toEpochMilliseconds(),
                    searchText = searchHistory,
                ),
            ),
        )
    }

    override suspend fun getAllSearchHistory(limit: Int): List<String> = userDataDao.getSearchHistories(limit).map { it.searchText }

    override suspend fun getLastSuccessfulSyncTime(): Long? = preferences.userDate.first().lastSuccessfulSyncTime

    override suspend fun saveDefaultSortRule(
        isAudio: Boolean,
        preset: PresetDisplaySetting,
    ) {
        if (isAudio) {
            preferences.setDefaultAudioPreset(preset.toIntValue())
        } else {
            preferences.setDefaultVideoPreset(preset.toIntValue())
        }
    }

    override suspend fun saveSortRuleForTab(
        tab: CustomTab,
        displaySetting: DisplaySetting,
    ) {
        userDataDao.upsertSortRuleEntity(entity = displaySetting.toEntity(tab.tabId))
    }

    override fun getCurrentSortRule(tab: CustomTab): Flow<DisplaySetting> {
        val defaultSortRuleFlow =
            userSettingFlow.map {
                if (tab.isAudio()) {
                    it.defaultAudioPresetDisplaySetting?.displaySetting
                } else {
                    it.defaultVideoPresetDisplaySetting?.displaySetting
                }
            }
        val customTabSortRuleFlow = userDataDao.getDisplaySettingFlowOfTab(tab.tabId)
        return combine(
            defaultSortRuleFlow,
            customTabSortRuleFlow,
        ) { default, custom ->
            val customDisplaySetting: DisplaySetting? = custom?.toModel()
            customDisplaySetting ?: default ?: run {
                if (tab.isAudio()) {
                    DisplaySetting.Preset.Audio.DefaultPreset
                } else {
                    DisplaySetting.Preset.Video.DefaultPreset
                }
            }
        }
    }

    override fun getDefaultPresetSortRule(isAudio: Boolean): Flow<DisplaySetting> =
        userSettingFlow.map {
            if (isAudio) {
                it.defaultAudioPresetDisplaySetting?.displaySetting
                    ?: DisplaySetting.Preset.Audio.DefaultPreset
            } else {
                it.defaultVideoPresetDisplaySetting?.displaySetting
                    ?: DisplaySetting.Preset.Video.DefaultPreset
            }
        }

    override suspend fun getTabCustomSortRule(tab: CustomTab): DisplaySetting? =
        userDataDao.getDisplaySettingFlowOfTab(tab.tabId).first()?.toModel()

    override suspend fun swapTabOrder(
        from: CustomTab,
        to: CustomTab,
    ) {
        userDataDao.swapTabOrder(from.tabId, toId = to.tabId)
    }
}

private fun TabKind.toEntityName(): String =
    when (this) {
        TabKind.ALBUM -> CustomTabType.ALBUM_DETAIL
        TabKind.ARTIST -> CustomTabType.ARTIST_DETAIL
        TabKind.GENRE -> CustomTabType.GENRE_DETAIL
        TabKind.PLAYLIST -> CustomTabType.PLAYLIST_DETAIL
        TabKind.ALL_MUSIC -> CustomTabType.ALL_MUSIC
    }

expect fun isPathValid(path: String): Boolean

private fun MediaPreviewMode.toIntValue(): Int =
    when (this) {
        MediaPreviewMode.LIST_PREVIEW -> PreviewModeValues.LIST_PREVIEW_VALUE
        MediaPreviewMode.GRID_PREVIEW -> PreviewModeValues.GRID_PREVIEW_VALUE
    }

private fun Int.toMediaPreviewMode(): MediaPreviewMode =
    when (this) {
        PreviewModeValues.LIST_PREVIEW_VALUE -> MediaPreviewMode.LIST_PREVIEW
        PreviewModeValues.GRID_PREVIEW_VALUE -> MediaPreviewMode.GRID_PREVIEW

        // Default
        else -> MediaPreviewMode.GRID_PREVIEW
    }

private fun PresetDisplaySetting.toIntValue() =
    when (this) {
        PresetDisplaySetting.AlbumAsc -> DefaultPresetValues.ALBUM_ASC_VALUE
        PresetDisplaySetting.ArtistAsc -> DefaultPresetValues.ARTIST_ASC_VALUE
        PresetDisplaySetting.TitleNameAsc -> DefaultPresetValues.TITLE_ASC_VALUE
        PresetDisplaySetting.ArtistAlbumASC -> DefaultPresetValues.ARTIST_ALBUM_ASC_VALUE
        PresetDisplaySetting.VideoBucketNameASC -> DefaultPresetValues.BUCKET_NAME_ASC_VALUE
    }

private fun Int.toDefaultPresetRule() =
    when (this) {
        DefaultPresetValues.ALBUM_ASC_VALUE -> PresetDisplaySetting.AlbumAsc
        DefaultPresetValues.ARTIST_ASC_VALUE -> PresetDisplaySetting.ArtistAsc
        DefaultPresetValues.TITLE_ASC_VALUE -> PresetDisplaySetting.TitleNameAsc
        DefaultPresetValues.ARTIST_ALBUM_ASC_VALUE -> PresetDisplaySetting.ArtistAlbumASC
        DefaultPresetValues.BUCKET_NAME_ASC_VALUE -> PresetDisplaySetting.VideoBucketNameASC
        else -> error("Invalid preset value: $this")
    }
