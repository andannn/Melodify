package com.andannn.melodify.ui.components.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.andannn.melodify.ui.common.util.getUiRetainedScope
import com.andannn.melodify.ui.components.menu.model.SheetModel
import com.andannn.melodify.ui.components.menu.sheet.AddToPlayListRequestSheet
import com.andannn.melodify.ui.components.menu.sheet.MediaOptionBottomSheet
import com.andannn.melodify.ui.components.menu.sheet.SleepTimerCountingBottomSheet
import com.andannn.melodify.ui.components.menu.sheet.SleepTimerOptionBottomSheet
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun ActionMenuContainer(
    drawerController: DrawerController = getUiRetainedScope()?.get() ?: getKoin().get(),
) {
    val bottomSheetModel by drawerController.bottomSheetModel.collectAsState(null)
    ActionMenu(
        bottomSheet = bottomSheetModel,
        onEvent = { event ->
            drawerController.onEvent(event)
        }
    )
}

@Composable
expect fun ActionMenu(
    bottomSheet: SheetModel?,
    onEvent: (DrawerEvent) -> Unit = {},
)

@Composable
internal fun BottomDrawer(
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

            is SheetModel.AddToPlayListSheet -> {
                AddToPlayListRequestSheet(
                    sheet = bottomSheet,
                    onRequestDismiss = {
                        onEvent(DrawerEvent.OnDismissSheet(bottomSheet))
                    },
                    onAddToPlay = { playList, audioList ->
                        onEvent(DrawerEvent.OnAddToPlayList(playList, audioList))
                    },
                    onCreateNewPlayList = {
                        onEvent(DrawerEvent.OnCreateNewPlayList(bottomSheet.source))
                    }
                )
            }
        }
    }
}
