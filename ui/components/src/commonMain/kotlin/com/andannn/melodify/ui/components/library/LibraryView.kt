package com.andannn.melodify.ui.components.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.SendAndArchive
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.common.widgets.SmpIcon
import org.jetbrains.compose.resources.stringResource

@Composable
fun LibraryView(
    modifier: Modifier = Modifier,
    onBackKeyPressed: () -> Unit = {},
    navigateToSearch: () -> Unit = {}
) {
    LibraryContent(
        modifier = modifier,
        onBackKeyPressed = onBackKeyPressed,
        navigateToSearch = navigateToSearch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryContent(
    modifier: Modifier = Modifier,
    onBackKeyPressed: () -> Unit,
    navigateToSearch: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackKeyPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Text(text = "Library")
                },
                actions = {
                    IconButton(onClick = navigateToSearch) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                        )
                    }
                }
            )
        },
    ) {
        LazyVerticalGrid(
            modifier = Modifier.padding(it).padding(4.dp),
            columns = GridCells.Adaptive(minSize = 180.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = ShortcutItem.entries.toTypedArray(),
                key = { it }
            ) { item ->
                ShortcutItem(
                    shortcutItem = item,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    modifier: Modifier = Modifier,
    shortcutItem: ShortcutItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmpIcon(
                shortcutItem.iconRes,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(shortcutItem.textRes),
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}
