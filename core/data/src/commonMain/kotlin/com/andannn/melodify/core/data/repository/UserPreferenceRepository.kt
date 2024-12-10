package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.UserSetting
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
     * update current custom tabs
     *
     * @param currentCustomTabs current custom tabs
     */
    suspend fun updateCurrentCustomTabs(currentCustomTabs: List<CustomTab>)

    /**
     * add new custom tab
     *
     * @param tab tab to add
     */
    suspend fun addNewCustomTab(tab: CustomTab)

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
}
