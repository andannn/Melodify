/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.andannn.melodify.ui.core

import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retainRetainedValuesStoreRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class RetainedModelTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun retainModel_canceled_while_exit_composition_with_RetainedValuesStore_exist() {
        var model: MockRetainedModel? = null
        val tag = "tag"
        rule.setContent {
            val registry = retainRetainedValuesStoreRegistry()
            var showModel by remember {
                mutableStateOf(true)
            }
            registry.LocalRetainedValuesStoreProvider("Test") {
                Button(
                    modifier =
                        Modifier.testTag(tag),
                    onClick = {
                        showModel = !showModel
                    },
                ) {
                    if (showModel) {
                        Surface {
                            model =
                                retainRetainedModel {
                                    MockRetainedModel()
                                }
                        }
                    }
                }
            }
        }
        rule.waitForIdle()
        assertFalse(model!!.isCleared)
        rule.onNodeWithTag(tag).performClick()

        rule.waitForIdle()
        assertTrue(model.isCleared)
    }

    @Test
    fun retainModel_not_canceled_while_exit_composition_with_RetainedValuesStore_disappear() {
        var model: MockRetainedModel? = null
        val tag = "tag"
        rule.setContent {
            val registry = retainRetainedValuesStoreRegistry()
            var showModel by remember {
                mutableStateOf(true)
            }
            Button(
                modifier =
                    Modifier.testTag(tag),
                onClick = {
                    showModel = !showModel
                },
            ) {
                if (showModel) {
                    registry.LocalRetainedValuesStoreProvider("Test") {
                        Surface {
                            model =
                                retainRetainedModel {
                                    MockRetainedModel()
                                }
                        }
                    }
                }
            }
        }
        rule.waitForIdle()
        assertFalse(model!!.isCleared)
        rule.onNodeWithTag(tag).performClick()

        rule.waitForIdle()
        assertFalse(model.isCleared)
    }

    @Test
    fun retainModel_canceled_while_remove_retainedStore() {
        var model: MockRetainedModel? = null
        val tag = "tag"
        rule.setContent {
            val registry = retainRetainedValuesStoreRegistry()
            var showModel by remember {
                mutableStateOf(true)
            }
            Button(
                modifier =
                    Modifier.testTag(tag),
                onClick = {
                    showModel = !showModel
                },
            ) {
                if (showModel) {
                    DisposableEffect(Unit) {
                        onDispose {
                            registry.clearChild("Test")
                        }
                    }
                    registry.LocalRetainedValuesStoreProvider("Test") {
                        Surface {
                            model =
                                retainRetainedModel {
                                    MockRetainedModel()
                                }
                        }
                    }
                }
            }
        }
        rule.waitForIdle()
        assertFalse(model!!.isCleared)
        rule.onNodeWithTag(tag).performClick()

        rule.waitForIdle()
        assertTrue(model.isCleared)
    }
}

private class MockRetainedModel : RetainedModel() {
    var isCleared = false

    override fun onClear() {
        isCleared = true
    }
}
