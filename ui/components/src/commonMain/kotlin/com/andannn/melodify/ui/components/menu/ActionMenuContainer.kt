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
    menuController: MenuController = getUiRetainedScope()?.get() ?: getKoin().get(),
) {
    val bottomSheetModel by menuController.bottomSheetModel.collectAsState(null)
    ActionMenu(
        bottomSheet = bottomSheetModel,
        onEvent = { event ->
            menuController.onEvent(event)
        }
    )
}

@Composable
expect fun ActionMenu(
    bottomSheet: SheetModel?,
    onEvent: (MenuEvent) -> Unit = {},
)

@Composable
internal fun BottomDrawer(
    bottomSheet: SheetModel?,
    onEvent: (MenuEvent) -> Unit = {},
) {
    if (bottomSheet != null) {
        when (bottomSheet) {
            is SheetModel.MediaOptionSheet -> {
                MediaOptionBottomSheet(
                    optionSheet = bottomSheet,
                    onClickOption = {
                        onEvent(MenuEvent.OnMediaOptionClick(bottomSheet, it))
                    },
                    onToggleFavorite = { id ->
                        onEvent(MenuEvent.OnToggleFavorite(id))
                    },
                    onRequestDismiss = {
                        onEvent(MenuEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }

            SheetModel.TimerOptionSheet -> {
                SleepTimerOptionBottomSheet(
                    onSelectOption = {
                        onEvent(MenuEvent.OnTimerOptionClick(it))
                    },
                    onRequestDismiss = {
                        onEvent(MenuEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }

            is SheetModel.TimerRemainTimeSheet -> {
                SleepTimerCountingBottomSheet(
                    onCancelTimer = {
                        onEvent(MenuEvent.OnCancelTimer)
                    },
                    onRequestDismiss = {
                        onEvent(MenuEvent.OnDismissSheet(bottomSheet))
                    }
                )
            }

            is SheetModel.AddToPlayListSheet -> {
                AddToPlayListRequestSheet(
                    sheet = bottomSheet,
                    onRequestDismiss = {
                        onEvent(MenuEvent.OnDismissSheet(bottomSheet))
                    },
                    onAddToPlay = { playList, audioList ->
                        onEvent(MenuEvent.OnAddToPlayList(playList, audioList))
                    },
                    onCreateNewPlayList = {
                        onEvent(MenuEvent.OnCreateNewPlayList(bottomSheet.source))
                    }
                )
            }
        }
    }
}
