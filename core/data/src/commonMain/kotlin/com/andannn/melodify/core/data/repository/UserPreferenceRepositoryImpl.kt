/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.model.PresetSortRule
import com.andannn.melodify.core.data.model.SortRule
import com.andannn.melodify.core.data.model.TabKind
import com.andannn.melodify.core.data.model.UserSetting
import com.andannn.melodify.core.data.util.mapToCustomTabModel
import com.andannn.melodify.core.data.util.toEntity
import com.andannn.melodify.core.data.util.toModel
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class UserPreferenceRepositoryImpl(
    private val preferences: UserSettingPreferences,
    private val userDataDao: UserDataDao,
) : UserPreferenceRepository {
    override val userSettingFlow: Flow<UserSetting> =
        preferences.userDate.map {
            UserSetting(
                mediaPreviewMode = it.mediaPreviewMode.toMediaPreviewMode(),
                libraryPath = it.libraryPath,
                lastSuccessfulSyncTime = it.lastSuccessfulSyncTime,
                defaultPresetSortRule = it.defaultSortRule?.toDefaultPresetRule(),
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

    override suspend fun saveDefaultSortRule(sortRule: SortRule) {
        val preset = PresetSortRule.entries.first { it.sortRule == sortRule }
        preferences.setDefaultPreset(
            preset.toIntValue(),
        )
    }

    override suspend fun saveSortRuleForTab(
        tab: CustomTab,
        sortRule: SortRule,
    ) {
        userDataDao.upsertSortRuleEntity(entity = sortRule.toEntity(tab.tabId))
    }

    override fun getCurrentSortRule(tab: CustomTab?): Flow<SortRule> {
        val defaultSortRuleFlow = userSettingFlow.map { it.defaultPresetSortRule?.sortRule }
        val customTabSortRuleFlow =
            if (tab != null) {
                userDataDao.getDisplaySettingFlowOfTab(tab.tabId)
            } else {
                flowOf(null)
            }
        return combine(
            defaultSortRuleFlow,
            customTabSortRuleFlow,
        ) { default, custom ->
            val customSortRule: SortRule? = custom?.toModel()
            customSortRule ?: default ?: SortRule.Preset.DefaultPreset
        }
    }

    override suspend fun getTabCustomSortRule(tab: CustomTab): SortRule? =
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

private fun PresetSortRule.toIntValue() =
    when (this) {
        PresetSortRule.AlbumAsc -> DefaultPresetValues.ALBUM_ASC_VALUE
        PresetSortRule.ArtistAsc -> DefaultPresetValues.ARTIST_ASC_VALUE
        PresetSortRule.TitleNameAsc -> DefaultPresetValues.TITLE_ASC_VALUE
        PresetSortRule.ArtistAlbumASC -> DefaultPresetValues.ARTIST_ALBUM_ASC_VALUE
    }

private fun Int.toDefaultPresetRule() =
    when (this) {
        DefaultPresetValues.ALBUM_ASC_VALUE -> PresetSortRule.AlbumAsc
        DefaultPresetValues.ARTIST_ASC_VALUE -> PresetSortRule.ArtistAsc
        DefaultPresetValues.TITLE_ASC_VALUE -> PresetSortRule.TitleNameAsc
        DefaultPresetValues.ARTIST_ALBUM_ASC_VALUE -> PresetSortRule.ArtistAlbumASC

        // Default
        else -> PresetSortRule.AlbumAsc
    }
