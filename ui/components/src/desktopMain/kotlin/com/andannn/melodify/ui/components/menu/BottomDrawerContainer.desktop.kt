package com.andannn.melodify.ui.components.menu

import androidx.compose.material.CursorDropdownMenu
import androidx.compose.runtime.Composable
import com.andannn.melodify.ui.components.menu.model.SheetModel
import com.andannn.melodify.ui.components.menu.model.SheetOptionItem
import com.andannn.melodify.ui.components.menu.sheet.SheetItem

@Composable
actual fun ActionMenu(
    bottomSheet: SheetModel?,
    onEvent: (MenuEvent) -> Unit
) {
    if (bottomSheet != null) {
        when (bottomSheet) {
            is SheetModel.MediaOptionSheet -> {
                CursorDropDownOptionMenu(
                    optionSheet = bottomSheet,
                    onClickOption = {
                        onEvent(MenuEvent.OnMediaOptionClick(bottomSheet, it))
                    },
                    onRequestDismiss = {
                        onEvent(MenuEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }

            else -> {
                // no-op
            }
        }
    }
}

@Composable
fun CursorDropDownOptionMenu(
    onRequestDismiss: () -> Unit,
    optionSheet: SheetModel.MediaOptionSheet,
    onClickOption: (SheetOptionItem) -> Unit = {},
) {
    CursorDropdownMenu(
        expanded = true,
        onDismissRequest = onRequestDismiss
    ) {
        optionSheet.options.map { item ->
            SheetItem(
                item = item,
                onClick = {
                    onClickOption.invoke(item)
                },
            )
        }
    }
}