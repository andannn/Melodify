/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.TabKind
import com.andannn.melodify.domain.model.UserSetting
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    /**
     * user setting flow
     */
    val userSettingFlow: Flow<UserSetting>

    /**
     * current custom tabs flow
     */
    val currentCustomTabsFlow: Flow<List<CustomTab>>

    /**
     * add new custom tab
     *
     * @param externalId external id
     * @param tabName name of new custom tab
     * @param tabKind kind of new custom tab
     */
    suspend fun addNewCustomTab(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    )

    suspend fun isTabExist(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    ): Boolean

    /**
     * delete custom tab
     *
     * @param tab tab to delete
     */
    suspend fun deleteCustomTab(tab: CustomTab)

    /**
     * add library path
     *
     * @param path path to add
     * @return true if add success, false otherwise
     */
    suspend fun addLibraryPath(path: String): Boolean

    /**
     * delete library path
     *
     * @param path path to delete
     * @return true if delete success, false otherwise
     */
    suspend fun deleteLibraryPath(path: String): Boolean

    /**
     * add search history
     *
     * @param searchHistory search history to add
     */
    suspend fun addSearchHistory(searchHistory: String)

    /**
     * get all search history
     *
     * @return all search history
     */
    suspend fun getAllSearchHistory(limit: Int = 10): List<String>

    /**
     * get last successful sync time
     */
    suspend fun getLastSuccessfulSyncTime(): Long?

    suspend fun saveDefaultSortRule(
        isAudio: Boolean,
        preset: PresetDisplaySetting,
    )

    suspend fun saveSortRuleForTab(
        tab: CustomTab,
        displaySetting: DisplaySetting,
    )

    /**
     * get sort rule of tab.
     */
    fun getCurrentSortRule(tab: CustomTab): Flow<DisplaySetting>

    fun getDefaultPresetSortRule(isAudio: Boolean): Flow<DisplaySetting>

    /**
     * get custom sort rule of tab.
     */
    suspend fun getTabCustomSortRule(tab: CustomTab): DisplaySetting?

    /**
     * swap tab order
     *
     * @param from from tab
     * @param to to tab
     */
    suspend fun swapTabOrder(
        from: CustomTab,
        to: CustomTab,
    )
}
