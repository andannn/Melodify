package com.andannn.melodify.feature.drawer

import androidx.compose.runtime.Composable
import com.andannn.melodify.feature.drawer.model.SheetModel
import com.andannn.melodify.feature.drawer.sheet.MediaOptionBottomSheet
import com.andannn.melodify.feature.drawer.sheet.SleepTimerCountingBottomSheet
import com.andannn.melodify.feature.drawer.sheet.SleepTimerOptionBottomSheet

@Composable
fun BottomDrawerContainer(
    bottomSheet: SheetModel?,
    onEvent: (DrawerEvent) -> Unit = {},
) {
    if (bottomSheet != null) {
        when (bottomSheet) {
            is SheetModel.MediaOptionSheet -> {
                MediaOptionBottomSheet(
                    optionSheet = bottomSheet,
                    onClickOption = {
                        onEvent(DrawerEvent.OnMediaOptionClick(bottomSheet, it))
                    },
                    onToggleFavorite = { id ->
                        onEvent(DrawerEvent.OnToggleFavorite(id))
                    },
                    onRequestDismiss = {
                        onEvent(DrawerEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }

            SheetModel.TimerOptionSheet -> {
                SleepTimerOptionBottomSheet(
                    onSelectOption = {
                        onEvent(DrawerEvent.OnTimerOptionClick(it))
                    },
                    onRequestDismiss = {
                        onEvent(DrawerEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }

            is SheetModel.TimerRemainTimeSheet -> {
                SleepTimerCountingBottomSheet(
                    onCancelTimer = {
                        onEvent(DrawerEvent.OnCancelTimer)
                    },
                    onRequestDismiss = {
                        onEvent(DrawerEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }
        }
    }
}
