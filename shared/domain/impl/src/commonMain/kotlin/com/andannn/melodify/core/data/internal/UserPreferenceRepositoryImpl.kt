/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.core.data.internal

import androidx.sqlite.SQLiteException
import com.andannn.melodify.core.database.CustomTabType
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.entity.CustomTabSettingEntity
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.database.entity.TabEntity
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.datastore.model.PreviewModeValues
import com.andannn.melodify.domain.UserPreferenceRepository
import com.andannn.melodify.domain.impl.mapToCustomTabModel
import com.andannn.melodify.domain.impl.toEntity
import com.andannn.melodify.domain.impl.toModel
import com.andannn.melodify.domain.model.AudioTrackStyle
import com.andannn.melodify.domain.model.ContentSortType
import com.andannn.melodify.domain.model.MediaPreviewMode
import com.andannn.melodify.domain.model.PresetDisplaySetting
import com.andannn.melodify.domain.model.Tab
import com.andannn.melodify.domain.model.TabKind
import com.andannn.melodify.domain.model.TabSortRule
import com.andannn.melodify.domain.model.UserSetting
import com.andannn.melodify.domain.model.contentSortType
import com.andannn.melodify.domain.model.defaultPresetSetting
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "UserPreferenceRepositor"

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
                defaultPlayListPresetDisplaySetting = it.defaultPlayListSortRule?.toDefaultPresetRule(),
            )
        }

    override val currentTabsFlow: Flow<List<Tab>> =
        userDataDao
            .getCustomTabsFlow()
            .map { it.mapToCustomTabModel().filterNotNull() }

    override suspend fun addNewCustomTab(
        externalId: String,
        tabName: String,
        tabKind: TabKind,
    ) {
        userDataDao.insertCustomTab(
            TabEntity(
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
        when (tabKind) {
            TabKind.ALL_MUSIC,
            TabKind.ALL_VIDEO,
            -> {
                userDataDao.isTabKindExist(tabKind.toEntityName())
            }

            else -> {
                userDataDao.isTabExist(
                    externalId = externalId,
                    name = tabName,
                    type = tabKind.toEntityName(),
                )
            }
        }

    override suspend fun deleteCustomTab(tab: Tab) {
        userDataDao.deleteCustomTab(tabId = tab.tabId)
    }

    override suspend fun addLibraryPath(path: String): Boolean {
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

    @OptIn(ExperimentalTime::class)
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
        type: ContentSortType,
        preset: PresetDisplaySetting,
    ) {
        when (type) {
            ContentSortType.Audio -> preferences.setDefaultAudioPreset(preset.toIntValue())
            ContentSortType.Video -> preferences.setDefaultVideoPreset(preset.toIntValue())
            ContentSortType.PlayList -> preferences.setDefaultPlaylistPreset(preset.toIntValue())
        }
    }

    override fun getCurrentSortRule(tab: Tab): Flow<TabSortRule> {
        val defaultSortRuleFlow =
            getDefaultPresetDisplaySettingFlow(tab.contentSortType()).map { it.tabSortRule }
        val tabSortRuleFlow = getTabSortRuleFlow(tab)
        return combine(
            defaultSortRuleFlow,
            tabSortRuleFlow,
        ) { default, tabSortRule ->
            tabSortRule ?: default
        }
    }

    override fun getDefaultPresetSortRule(contentSortType: ContentSortType): Flow<PresetDisplaySetting> =
        getDefaultPresetDisplaySettingFlow(contentSortType)

    override suspend fun selectTabPresetDisplaySetting(
        tabId: Long,
        preset: PresetDisplaySetting,
    ) {
        userDataDao.selectTabPresetDisplaySettingEntity(tabId, preset = preset.toIntValue())
    }

    override suspend fun selectTabCustomDisplaySetting(
        tabId: Long,
        sortRule: TabSortRule,
        audioEntryStyle: AudioTrackStyle,
        isShowVideoProgress: Boolean,
    ) {
        userDataDao.selectTabCustomSettingEntity(
            tabId,
            tabSettingEntity =
                CustomTabSettingEntity(
                    customTabId = tabId,
                    isShowVideoProgress = isShowVideoProgress,
                    audioEntryStyle = audioEntryStyle.toDBValue(),
                ),
            tabSortRuleEntity = sortRule.toEntity(tabId),
        )
    }

    override suspend fun swapTabOrder(
        from: Tab,
        to: Tab,
    ) {
        userDataDao.swapTabOrder(from.tabId, toId = to.tabId)
    }

    override suspend fun markVideoCompleted(videoId: Long) {
        val isRecordExist = userDataDao.getPlayProgressFlow(videoId).first() != null
        if (!isRecordExist) {
            savePlayProgress(videoId, 0)
        }
        userDataDao.markVideoAsWatched(videoId)
    }

    override suspend fun savePlayProgress(
        videoId: Long,
        progressMs: Long,
    ) {
        try {
            userDataDao.savePlayProgress(videoId, progressMs)
        } catch (e: SQLiteException) {
            // when try to save progress of video which is not exist, it will throw exception.
            // We can ignore this exception here.
            Napier.e(tag = TAG) { "Failed to save play progress: $e" }
        }
    }

    override fun getResumePointMsFlow(videoId: Long): Flow<Pair<Long, Boolean>?> {
        return userDataDao.getPlayProgressFlow(videoId).map {
            if (it == null) {
                return@map null
            }

            it.progressMs to it.getIsVideoFinished()
        }
    }

    override suspend fun setIsShowVideoProgress(
        tab: Tab,
        isShow: Boolean,
    ) {
        val oldSetting = userDataDao.getCustomTabSettingFlow(tab.tabId).first()
        if (oldSetting == null) {
            userDataDao.insertTabSettingEntity(
                CustomTabSettingEntity(
                    customTabId = tab.tabId,
                    isShowVideoProgress = isShow,
                    audioEntryStyle =
                        tab
                            .contentSortType()
                            .defaultPresetSetting()
                            .audioTrackStyle
                            .toDBValue(),
                ),
            )
            return
        }
        userDataDao.insertTabSettingEntity(oldSetting.copy(isShowVideoProgress = isShow))
    }

    override fun getIsShowVideoProgressFlow(tab: Tab): Flow<Boolean> {
        val defaultSettingFlow =
            getDefaultPresetDisplaySettingFlow(tab.contentSortType()).map { it.isShowVideoProgress }
        val tabSettingFlow = getTabIsShowVideoProgressFlow(tab)

        return combine(
            defaultSettingFlow,
            tabSettingFlow,
        ) { default, custom ->
            custom ?: default
        }.distinctUntilChanged()
    }

    override fun getAudioTrackStyleFlow(tab: Tab): Flow<AudioTrackStyle> {
        val defaultSettingFlow =
            getDefaultPresetDisplaySettingFlow(tab.contentSortType()).map { it.audioTrackStyle }
        val tabSettingFlow = getTabAudioTrackStyleFlow(tab)
        return combine(
            defaultSettingFlow,
            tabSettingFlow,
        ) { default, custom ->
            custom ?: default
        }.distinctUntilChanged()
    }

    private fun getDefaultPresetDisplaySettingFlow(type: ContentSortType) =
        userSettingFlow.map {
            when (type) {
                ContentSortType.Audio -> {
                    it.defaultAudioPresetDisplaySetting
                }

                ContentSortType.Video -> {
                    it.defaultVideoPresetDisplaySetting
                }

                ContentSortType.PlayList -> {
                    it.defaultPlayListPresetDisplaySetting
                }
            }
                ?: type.defaultPresetSetting()
        }

    private fun getTabSortRuleFlow(tab: Tab): Flow<TabSortRule?> {
        val tabPresetFlow =
            userDataDao.getTabPresetDisplaySettingFlow(tab.tabId).map { entityOrNull ->
                entityOrNull?.preset?.let { PresetDisplaySetting.fromValue(it) }?.tabSortRule
            }
        val tabCustomSortRuleFlow =
            userDataDao.getCustomTabSortRuleFlow(tab.tabId).map {
                it?.toModel()
            }
        return combine(
            tabPresetFlow,
            tabCustomSortRuleFlow,
        ) { preset, custom ->
            custom ?: preset
        }
    }

    private fun getTabAudioTrackStyleFlow(tab: Tab): Flow<AudioTrackStyle?> {
        val tabPresetFlow =
            userDataDao.getTabPresetDisplaySettingFlow(tab.tabId).map { entityOrNull ->
                entityOrNull?.preset?.let { PresetDisplaySetting.fromValue(it) }?.audioTrackStyle
            }
        val tabCustomFlow =
            userDataDao.getCustomTabSettingFlow(tab.tabId).map {
                it?.audioEntryStyle?.toDomainValue()
            }
        return combine(
            tabPresetFlow,
            tabCustomFlow,
        ) { preset, custom ->
            custom ?: preset
        }
    }

    private fun getTabIsShowVideoProgressFlow(tab: Tab): Flow<Boolean?> {
        val tabPresetFlow =
            userDataDao.getTabPresetDisplaySettingFlow(tab.tabId).map { entityOrNull ->
                entityOrNull?.preset?.let { PresetDisplaySetting.fromValue(it) }?.isShowVideoProgress
            }
        val tabCustomFlow =
            userDataDao.getCustomTabSettingFlow(tab.tabId).map {
                it?.isShowVideoProgress
            }
        return combine(
            tabPresetFlow,
            tabCustomFlow,
        ) { preset, custom ->
            custom ?: preset
        }
    }
}

private fun AudioTrackStyle.toDBValue() =
    when (this) {
        AudioTrackStyle.ALBUM_COVER -> com.andannn.melodify.core.database.entity.AudioEntryStyle.ALBUM_COVER
        AudioTrackStyle.TRACK_NUMBER -> com.andannn.melodify.core.database.entity.AudioEntryStyle.TRACK_NUMBER
    }

private fun Long.toDomainValue() =
    when (this) {
        com.andannn.melodify.core.database.entity.AudioEntryStyle.ALBUM_COVER -> AudioTrackStyle.ALBUM_COVER
        com.andannn.melodify.core.database.entity.AudioEntryStyle.TRACK_NUMBER -> AudioTrackStyle.TRACK_NUMBER
        else -> error("Invalid audio entry style: $this")
    }

private fun TabKind.toEntityName(): String =
    when (this) {
        TabKind.ALBUM -> CustomTabType.ALBUM_DETAIL
        TabKind.ARTIST -> CustomTabType.ARTIST_DETAIL
        TabKind.GENRE -> CustomTabType.GENRE_DETAIL
        TabKind.PLAYLIST -> CustomTabType.PLAYLIST_DETAIL
        TabKind.ALL_MUSIC -> CustomTabType.ALL_MUSIC
        TabKind.ALL_VIDEO -> CustomTabType.ALL_VIDEO
        TabKind.VIDEO_BUCKET -> CustomTabType.VIDEO_BUCKET
    }

private fun Int.toMediaPreviewMode(): MediaPreviewMode =
    when (this) {
        PreviewModeValues.LIST_PREVIEW_VALUE -> MediaPreviewMode.LIST_PREVIEW

        PreviewModeValues.GRID_PREVIEW_VALUE -> MediaPreviewMode.GRID_PREVIEW

        // Default
        else -> MediaPreviewMode.GRID_PREVIEW
    }

private fun PresetDisplaySetting.toIntValue() = this.value

private fun Int.toDefaultPresetRule() = PresetDisplaySetting.fromValue(this) ?: error("Invalid preset value: $this")
