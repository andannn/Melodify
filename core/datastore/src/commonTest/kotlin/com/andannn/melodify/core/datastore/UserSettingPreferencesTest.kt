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

    private val randomInt
        get() = (0..1000).random()

    @BeforeTest
    fun setUp() {
        val dataStore =
            PreferenceDataStoreFactory.createWithPath(
                scope = datastoreScope,
            ) {
                "${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/${randomInt}_$DATA_STORE_FILENAME".toPath()
            }
        preferences = UserSettingPreferences(dataStore)
    }

    @Test
    fun `set media preview mode`() =
        testScope.runTest {
            preferences.setMediaPreviewMode(PreviewModeValues.GRID_PREVIEW_VALUE)
            val data = preferences.userDate.first()

            assertEquals(
                expected = PreviewModeValues.GRID_PREVIEW_VALUE,
                actual = data.mediaPreviewMode,
            )
        }

    @Test
    fun `set last successful sync`() =
        testScope.runTest {
            preferences.setLastSuccessfulSyncTime(123456789)
            val data = preferences.userDate.first()
            assertEquals(
                expected = 123456789,
                actual = data.lastSuccessfulSyncTime,
            )
        }

    @Test
    fun `get last successful when not set`() =
        testScope.runTest {
            val data = preferences.userDate.first()
            assertEquals(
                expected = null,
                actual = data.lastSuccessfulSyncTime,
            )
        }
}
