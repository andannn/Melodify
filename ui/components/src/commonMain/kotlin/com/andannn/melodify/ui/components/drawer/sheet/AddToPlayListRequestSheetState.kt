package com.andannn.melodify.ui.components.drawer.sheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.getAudios
import com.andannn.melodify.core.data.model.AudioItemModel
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.core.data.model.PlayListItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAddToPlayListSheetState(
    source: MediaItemModel,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(
    scope,
    source,
    sheetState
) {
    AddToPlayListSheetState(
        scope = scope,
        source = source,
        sheetState = sheetState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
class AddToPlayListSheetState(
    val scope: CoroutineScope,
    source: MediaItemModel,
    repository: Repository = getKoin().get<Repository>(),
    val sheetState: SheetState,
) {
    val audioListState = mutableStateListOf<AudioItemModel>()

    val playListState = mutableStateListOf<PlayListItemModel>()

    init {
        scope.launch {
            val audioList = repository.getAudios(source)
            audioListState.addAll(audioList)
        }

        scope.launch {
            repository.playListRepository.getAllPlayListFlow()
                .distinctUntilChanged()
                .collect {
                    playListState.clear()
                    playListState.addAll(it)
                }
        }
    }
}