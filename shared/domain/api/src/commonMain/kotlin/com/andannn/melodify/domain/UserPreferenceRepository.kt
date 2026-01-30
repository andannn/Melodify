/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.domain

import com.andannn.melodify.domain.model.AudioTrackStyle
import com.andannn.melodify.domain.model.ContentSortType
import com.andannn.melodify.domain.model.CustomDisplaySetting
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabKind
import com.andannn.melodify.domain.model.TabSortRule
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
    val currentTabsFlow: Flow<List<Tab>>

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
    suspend fun deleteCustomTab(tab: Tab)

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
        type: ContentSortType,
        preset: PresetDisplaySetting,
    )

    /**
     * Return flow of current sort rule.
     * If there is no custom sort rule, return saved default sort rule.
     */
    fun getCurrentSortRule(tab: Tab): Flow<TabSortRule>

    /**
     * Get flow of saved default sort rule for content sort type.
     */
    fun getDefaultPresetSortRule(contentSortType: ContentSortType): Flow<PresetDisplaySetting>

    fun getTabCustomDisplaySettingFlow(tab: Tab): Flow<CustomDisplaySetting?>

    fun getTabPresetDisplaySettingFlow(tab: Tab): Flow<PresetDisplaySetting?>

    suspend fun selectTabPresetDisplaySetting(
        tabId: Long,
        preset: PresetDisplaySetting,
    )

    suspend fun selectTabCustomDisplaySetting(
        tabId: Long,
        displaySetting: CustomDisplaySetting,
    )

    /**
     * swap tab order
     *
     * @param from from tab
     * @param to to tab
     */
    suspend fun swapTabOrder(
        from: Tab,
        to: Tab,
    )

    /**
     * mark video as watched
     *
     * @param videoId video id
     */
    suspend fun markVideoCompleted(videoId: Long)

    /**
     * get play progress of video
     *
     * @param videoId video id
     * @param progressMs progress in ms
     */
    suspend fun savePlayProgress(
        videoId: Long,
        progressMs: Long,
    )

    /**
     * get play progress flow of video
     *
     * @param videoId video id
     * @return flow of play progress and is finished
     */
    fun getResumePointMsFlow(videoId: Long): Flow<Pair<Long, Boolean>?>

    /**
     * set is show video progress
     *
     * @param tab tab
     * @param isShow true if show, false otherwise
     */
    suspend fun setIsShowVideoProgress(
        tab: Tab,
        isShow: Boolean,
    )

    fun getIsShowVideoProgressFlow(tab: Tab): Flow<Boolean>

    fun getAudioTrackStyleFlow(tab: Tab): Flow<AudioTrackStyle>
}
