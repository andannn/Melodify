package com.andannn.melodify.ui.common.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DropDownMenuIconButton(
    options: List<StringResource>,
    onSelectIndex: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {
        Icon(Icons.Default.FilterAlt, contentDescription = "Filter")
    },
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier =
        modifier,
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            icon()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(stringResource(item)) },
                    onClick = {
                        onSelectIndex(index)
                        expanded = false
                    },
                )
            }
        }
    }
}
