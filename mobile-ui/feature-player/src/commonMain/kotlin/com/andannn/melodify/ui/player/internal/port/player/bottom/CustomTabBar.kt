/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.ui.player.internal.port.player.bottom

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import melodify.shared.compose.resource.generated.resources.Res
import melodify.shared.compose.resource.generated.resources.lyrics
import melodify.shared.compose.resource.generated.resources.play_queue
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CustomTabBar(
    showIndicator: Boolean,
    items: ImmutableList<SheetTab>,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    onItemPressed: (SheetTab) -> Unit = {},
    onItemClick: (SheetTab) -> Unit = {},
) {
    val defaultIndicator: @Composable TabIndicatorScope.() -> Unit = @Composable {
        TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
        )
    }

    SecondaryTabRow(
        modifier =
            modifier
                .fillMaxWidth(),
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = if (showIndicator) defaultIndicator else emptyIndicator,
        divider = if (showIndicator) defaultDivider else emptyDivider,
    ) {
        items.forEachIndexed { index, item ->
            val source = remember { MutableInteractionSource() }
            LaunchedEffect(source, showIndicator) {
                if (showIndicator) return@LaunchedEffect

                source.interactions.collect {
                    when (it) {
                        is PressInteraction.Press -> onItemPressed(item)
                        else -> Unit
                    }
                }
            }

            Tab(
                selected = index == selectedTabIndex,
                selectedContentColor =
                    if (showIndicator) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = @Composable {
                    Text(
                        text = stringResource(item.getLabel()),
                    )
                },
                interactionSource = source,
                onClick = {
                    onItemClick(item)
                },
            )
        }
    }
}

private val emptyIndicator: @Composable TabIndicatorScope.() -> Unit = {}
private val defaultDivider: @Composable () -> Unit = @Composable { HorizontalDivider() }
private val emptyDivider: @Composable () -> Unit = @Composable { }

private fun SheetTab.getLabel() =
    when (this) {
        SheetTab.NEXT_SONG -> Res.string.play_queue
        SheetTab.LYRICS -> Res.string.lyrics
    }

@Preview
@Composable
private fun CustomTabBarPreview() {
    MelodifyTheme {
        Surface {
            CustomTabBar(
                modifier = Modifier.padding(vertical = 12.dp),
                showIndicator = true,
                items = SheetTab.entries.toImmutableList(),
                selectedTabIndex = 0,
            )
        }
    }
}

@Preview
@Composable
private fun CustomTabBarPreview2() {
    MelodifyTheme {
        Surface {
            CustomTabBar(
                showIndicator = false,
                items = SheetTab.entries.toImmutableList(),
                selectedTabIndex = 0,
            )
        }
    }
}
