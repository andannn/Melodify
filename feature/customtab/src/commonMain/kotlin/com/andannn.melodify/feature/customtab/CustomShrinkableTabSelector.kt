package com.andannn.melodify.feature.customtab

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.feature.common.util.getCategoryResource
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.audio_page_title
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CustomTabSelector(
    modifier: Modifier  = Modifier,
) {
    val stateHolder = rememberCustomTabSettingViewStateHolder()
    val state by stateHolder.state.collectAsState()
    val expandState = remember {
        mutableStateOf<Map<TabSector, Boolean>>(emptyMap())
    }

    LazyColumn(
        modifier = modifier.fillMaxHeight(),
    ) {
        item {
            val selected by rememberUpdatedState(state.currentTabs.contains(CustomTab.AllMusic))
            SelectableNavigationDrawerItem(
                label = stringResource(Res.string.audio_page_title),
                selected = selected,
                onClick = {
                    stateHolder.onEvent(
                        UiEvent.OnSelectedChange(
                            CustomTab.AllMusic,
                            !selected
                        )
                    )
                }
            )
        }

        state.allAvailableTabSectors.forEach { sector ->
            val expand = expandState.value[sector] ?: false
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (expand) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = ""
                            )
                        },
                        label = { Text(stringResource(sector.sectorTitle)) },
                        onClick = {
                            expandState.value = expandState.value.toMutableMap().apply {
                                this[sector] = !(this[sector] ?: false)
                            }
                        },
                        selected = false,
                    )
                }
            }

            item {
                AnimatedContent(expand) { isExpandContent ->
                    if (!isExpandContent) return@AnimatedContent Spacer(Modifier)

                    Column {
                        sector.sectorContent.forEach { tab ->
                            val itemSelected by rememberUpdatedState(
                                state.currentTabs.contains(
                                    tab
                                )
                            )

                            SelectableNavigationDrawerItem(
                                label = getCategoryResource(tab),
                                selected = itemSelected,
                                onClick = {
                                    stateHolder.onEvent(
                                        UiEvent.OnSelectedChange(
                                            tab,
                                            !itemSelected
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableNavigationDrawerItem(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        modifier = modifier,
        icon = {
            Spacer(modifier = Modifier.size(24.dp))
        },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = label
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = ""
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = ""
                    )
                }
            }
        },
        onClick = onClick,
        selected = false,
    )
}
