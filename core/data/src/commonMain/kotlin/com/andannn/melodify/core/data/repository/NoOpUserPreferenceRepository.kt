package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.UserSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class NoOpUserPreferenceRepository : UserPreferenceRepository {
    override val userSettingFlow: Flow<UserSetting> = flowOf()

    override val currentCustomTabsFlow: Flow<List<CustomTab>> = flowOf()

    override suspend fun updateCurrentCustomTabs(currentCustomTabs: List<CustomTab>) {}

    override suspend fun addNewCustomTab(tab: CustomTab) {}

    override suspend fun deleteCustomTab(tab: CustomTab) {}

    override suspend fun addLibraryPath(path: String): Boolean = false

    override suspend fun deleteLibraryPath(path: String): Boolean = false

    override suspend fun addSearchHistory(searchHistory: String) {}

    override suspend fun getAllSearchHistory(limit: Int): List<String> = emptyList()

    override suspend fun getLastSuccessfulSyncTime(): Long? = null
}
