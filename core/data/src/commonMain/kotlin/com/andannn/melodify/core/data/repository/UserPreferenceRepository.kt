package com.andannn.melodify.core.data.repository

import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.model.UserSetting
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    val userSettingFlow: Flow<UserSetting>

    val currentCustomTabsFlow: Flow<List<CustomTab>>

    suspend fun updateCurrentCustomTabs(currentCustomTabs: List<CustomTab>)
}
