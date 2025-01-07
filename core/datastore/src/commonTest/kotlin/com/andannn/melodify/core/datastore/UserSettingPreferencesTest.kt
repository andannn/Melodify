package com.andannn.melodify.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.andannn.melodify.core.datastore.model.PreviewModeValues
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserSettingPreferencesTest {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val datastoreScope = testScope.backgroundScope

    private lateinit var preferences: UserSettingPreferences

    @BeforeTest
    fun setUp() {
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            scope = datastoreScope,
        ) {
            "${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/$dataStoreFileName".toPath()
        }
        preferences = UserSettingPreferences(dataStore)
    }

    @Test
    fun `set media preview mode`() = testScope.runTest {
        preferences.setMediaPreviewMode(PreviewModeValues.GRID_PREVIEW_VALUE)
        val data = preferences.userDate.first()

        assertEquals(
            expected = PreviewModeValues.GRID_PREVIEW_VALUE,
            actual = data.mediaPreviewMode
        )
    }
}