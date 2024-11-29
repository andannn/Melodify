package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.CurrentCustomTabs
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.MediaPreviewMode
import com.andannn.melodify.core.data.model.UserSetting
import com.andannn.melodify.core.data.util.mapToCustomTabModel
import com.andannn.melodify.core.data.util.toEntity
import com.andannn.melodify.core.database.dao.UserDataDao
import com.andannn.melodify.core.datastore.UserSettingPreferences
import com.andannn.melodify.core.datastore.model.PreviewModeValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val DefaultCustomTabs = CurrentCustomTabs(
    listOf(
        CustomTab.AllMusic,
        CustomTab.AllAlbum,
        CustomTab.AllPlayList,
        CustomTab.AllGenre,
        CustomTab.AllArtist,
    )
)

class UserPreferenceRepositoryImpl(
    private val preferences: UserSettingPreferences,
    private val userDataDao: UserDataDao
) : UserPreferenceRepository {
    override val userSettingFlow: Flow<UserSetting> = preferences.userDate.map {
        UserSetting(
            mediaPreviewMode = it.mediaPreviewMode.toMediaPreviewMode(),
        )
    }

    override val currentCustomTabsFlow: Flow<List<CustomTab>> =
        userDataDao
            .getCustomTabsFlow()
            .map { it.mapToCustomTabModel() }

    override suspend fun updateCurrentCustomTabs(currentCustomTabs: List<CustomTab>) {
        userDataDao.clearAndInsertCustomTabs(
            currentCustomTabs.map { it.toEntity() }
        )
    }
}

private fun MediaPreviewMode.toIntValue(): Int = when (this) {
    MediaPreviewMode.LIST_PREVIEW -> PreviewModeValues.LIST_PREVIEW_VALUE
    MediaPreviewMode.GRID_PREVIEW -> PreviewModeValues.GRID_PREVIEW_VALUE
}

private fun Int.toMediaPreviewMode(): MediaPreviewMode = when (this) {
    PreviewModeValues.LIST_PREVIEW_VALUE -> MediaPreviewMode.LIST_PREVIEW
    PreviewModeValues.GRID_PREVIEW_VALUE -> MediaPreviewMode.GRID_PREVIEW

    // Default
    else -> MediaPreviewMode.GRID_PREVIEW
}

