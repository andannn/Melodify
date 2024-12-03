package com.andannn.melodify.ui.components.menu

import androidx.compose.runtime.Composable
import com.andannn.melodify.ui.components.menu.model.SheetModel

@Composable
actual fun ActionMenu(
    bottomSheet: SheetModel?,
    onEvent: (MenuEvent) -> Unit
) = BottomDrawer(bottomSheet, onEvent)