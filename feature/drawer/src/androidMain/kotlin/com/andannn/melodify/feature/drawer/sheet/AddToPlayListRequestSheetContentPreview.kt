package com.andannn.melodify.feature.drawer.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import com.andannn.melodify.feature.common.theme.MelodifyTheme

@Preview
@Composable
fun AddToPlayListRequestSheetContentPreview() {
    MelodifyTheme {
        Surface {
            AddToPlayListRequestSheetContent(
                audioList = List(10) {
                    AudioItemModel(it.toString(), "name $it", "", 0, "", "0", "", "0", 0, 0)
                },
                playLists = List(10) {
                    PlayListItemModel(
                        id = "$it",
                        name = "PlayList $it",
                        artWorkUri = "",
                        trackCount = 0
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddToPlayListRequestSheetPreview() {
    MelodifyTheme {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            onDismissRequest = {
            },
        ) {
            AddToPlayListRequestSheetContent(
                audioList = listOf(
                    AudioItemModel.DEFAULT
                ),
                playLists = List(10) {
                    PlayListItemModel(
                        id = "$it",
                        name = "PlayList $it",
                        artWorkUri = "",
                        trackCount = 0
                    )
                },
            )
        }
    }
}