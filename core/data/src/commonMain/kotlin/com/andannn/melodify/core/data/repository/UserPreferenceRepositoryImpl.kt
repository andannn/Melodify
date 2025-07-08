package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.model.UserSetting
import com.andannn.melodify.core.data.util.mapToCustomTabModel
import com.andannn.melodify.core.data.util.toEntity
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.database.entity.CustomTabType
import com.andannn.melodify.core.database.entity.SearchHistoryEntity
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.datastore.model.PreviewModeValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

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
            )
        }

    override val currentCustomTabsFlow: Flow<List<CustomTab>> =
        userDataDao
            .getCustomTabsFlow()
            .map { it.mapToCustomTabModel().filterNotNull() }

    override suspend fun updateCurrentCustomTabs(currentCustomTabs: List<CustomTab>) {
        userDataDao.clearAndInsertCustomTabs(
            currentCustomTabs.map { it.toEntity() },
        )
    }

    override suspend fun addNewCustomTab(tab: CustomTab) {
        val currentTabs = userDataDao.getCustomTabsFlow().first()
        userDataDao.clearAndInsertCustomTabs(currentTabs + tab.toEntity())
    }

    override suspend fun deleteCustomTab(tab: CustomTab) {
        val (type, id) =
            when (tab) {
                is CustomTab.AlbumDetail -> CustomTabType.ALBUM_DETAIL to tab.albumId
                CustomTab.AllMusic -> CustomTabType.ALL_MUSIC to null
                is CustomTab.ArtistDetail -> CustomTabType.ARTIST_DETAIL to tab.artistId
                is CustomTab.GenreDetail -> CustomTabType.GENRE_DETAIL to tab.genreId
                is CustomTab.PlayListDetail -> CustomTabType.PLAYLIST_DETAIL to tab.playListId
            }

        userDataDao.deleteCustomTab(type = type, externalId = id)
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

    override suspend fun getAllSearchHistory(limit: Int): List<String> {
        return userDataDao.getSearchHistories(limit).map { it.searchText }
    }

    override suspend fun getLastSuccessfulSyncTime(): Long? {
        return preferences.userDate.first().lastSuccessfulSyncTime
    }
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
