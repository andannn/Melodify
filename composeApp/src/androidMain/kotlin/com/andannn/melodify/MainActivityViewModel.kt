package com.andannn.melodify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.feature.common.GlobalUiController
import com.andannn.melodify.feature.common.DeleteMediaItemEventProvider
import com.andannn.melodify.core.player.MediaBrowserManager
import io.github.aakira.napier.Napier
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel(
    private val controller: GlobalUiController,
    private val mediaBrowserManager: MediaBrowserManager,
) : DeleteMediaItemEventProvider by controller,
    ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Init)

    val state: StateFlow<MainUiState>
        get() = _state

    init {
        viewModelScope.launch {
            try {
                mediaBrowserManager.connect()
                _state.value = MainUiState.Ready
            } catch (e: TimeoutCancellationException) {
                Napier.d(tag = TAG) { "connect error:  $e" }
                _state.value = MainUiState.Error(e)
            }
        }
    }

    override fun onCleared() {
        mediaBrowserManager.disConnect()
    }
}

sealed class MainUiState {
    data class Error(private val e: Exception) : MainUiState()

    data object Ready : MainUiState()

    data object Init : MainUiState()
}

