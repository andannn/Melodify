/*
 * Copyright 2025, the Melodify project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package com.andannn.melodify.shared.compose.components.tab.content.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andannn.melodify.domain.model.CustomTab
import com.andannn.melodify.domain.model.DisplaySetting
import com.andannn.melodify.domain.model.GroupKey
import com.andannn.melodify.shared.compose.common.Presenter
import com.andannn.melodify.shared.compose.common.theme.MelodifyTheme
import com.andannn.melodify.shared.compose.common.widgets.CircleBorderImage

@Composable
internal fun GroupHeaderContainer(
    selectedTab: CustomTab?,
    displaySetting: DisplaySetting?,
    groupKey: GroupKey,
    parentHeaderGroupKey: GroupKey? = null,
    onGroupItemClick: (List<GroupKey?>) -> Unit = {},
) {
    val groupState =
        GroupInfo(
            groupKey = groupKey,
            parentHeaderGroupKey = parentHeaderGroupKey,
            displaySetting = displaySetting,
            selectedTab = selectedTab,
        )
    GroupHeader(
        groupInfo = groupState,
        isPrimary = parentHeaderGroupKey == null,
        onGroupHeaderClick = {
            onGroupItemClick.invoke(groupState.selection)
        },
    )
}

@Composable
private fun GroupHeader(
    isPrimary: Boolean,
    groupInfo: GroupInfo,
    modifier: Modifier = Modifier,
    presenter: Presenter<GroupHeaderState> = retainGroupHeaderPresenter(groupInfo),
    onGroupHeaderClick: () -> Unit = {},
) {
    val state = presenter.present()
    HeaderInfo(
        modifier = modifier,
        isPrimary = isPrimary,
        coverArtUri = state.cover,
        title = state.title,
        onOptionClick = {
            state.eventSink.invoke(GroupHeaderEvent.OnOptionClick)
        },
        onClick = onGroupHeaderClick,
    )
}

@Composable
private fun HeaderInfo(
    modifier: Modifier = Modifier,
    coverArtUri: String?,
    isPrimary: Boolean,
    title: String = "",
    onOptionClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Surface(
        modifier =
            modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier =
                Modifier
                    .height(IntrinsicSize.Min),
        ) {
            if (coverArtUri != null) {
                CircleBorderImage(
                    modifier =
                        Modifier
                            .align(Alignment.CenterVertically)
                            .size(48.dp),
                    model = coverArtUri,
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    Modifier.fillMaxHeight(),
                ) {
                    val style =
                        if (isPrimary) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.titleMedium
                        }
                    Text(
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = style.copy(fontWeight = FontWeight.Bold),
                    )

                    IconButton(
                        modifier = Modifier.align(Alignment.Bottom),
                        onClick = onOptionClick,
                    ) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "menu")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PrimaryHeaderInfoPreview() {
    MelodifyTheme {
        HeaderInfo(
            isPrimary = true,
            coverArtUri = null,
            title = "Primary Header",
        )
    }
}

@Preview
@Composable
private fun SecondaryHeaderInfoPreview() {
    MelodifyTheme {
        HeaderInfo(
            isPrimary = false,
            coverArtUri = null,
            title = "Primary Header",
        )
    }
}
