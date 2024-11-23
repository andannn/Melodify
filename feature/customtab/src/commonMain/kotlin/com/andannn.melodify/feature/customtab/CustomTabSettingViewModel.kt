package com.andannn.melodify.feature.customtab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.repository.DefaultCustomTabs
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import melodify.feature.common.generated.resources.Res
import melodify.feature.common.generated.resources.album_page_title
import melodify.feature.common.generated.resources.artist_page_title
import melodify.feature.common.generated.resources.genre_title
import melodify.feature.common.generated.resources.home
import melodify.feature.common.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.StringResource

internal sealed interface UiEvent {
    data class OnSelectedChange(val tab: CustomTab, val isSelected: Boolean) : UiEvent
    data class OnUpdateTabs(val newTabs: List<CustomTab>) : UiEvent
    data object OnResetClick : UiEvent
}

internal class CustomTabSettingViewModel(
    private val playListRepository: PlayListRepository,
    private val contentRepository: MediaContentRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                getInitialState()
            }

            userPreferenceRepository.currentCustomTabsFlow
                .distinctUntilChanged()
                .collect { currentTabs ->
                    _state.update {
                        if (it !is UiState.Ready) return@update it
                        it.copy(
                            currentTabs = currentTabs
                        )
                    }
                }
        }
    }

    internal fun onEvent(event: UiEvent) {
        val state = _state.value
        if (state !is UiState.Ready) return

        when (event) {
            is UiEvent.OnSelectedChange -> {
                val (tab, selected) = event
                viewModelScope.launch {
                    if (selected) {
                        userPreferenceRepository.updateCurrentCustomTabs(
                            state.currentTabs + tab
                        )
                    } else {
                        userPreferenceRepository.updateCurrentCustomTabs(
                            state.currentTabs - tab
                        )
                    }
                }
            }

            is UiEvent.OnUpdateTabs -> {
                viewModelScope.launch {
                    userPreferenceRepository.updateCurrentCustomTabs(
                        event.newTabs
                    )
                }
            }

            UiEvent.OnResetClick -> {
                viewModelScope.launch {
                    userPreferenceRepository.updateCurrentCustomTabs(
                        DefaultCustomTabs.customTabs
                    )
                }
            }
        }
    }

    private suspend fun getInitialState() = coroutineScope {
        val currentSettingDeferred = async {
            userPreferenceRepository.currentCustomTabsFlow.first()
        }
        val albumsDeferred = async {
            contentRepository.getAllAlbumsFlow().first()
        }
        val allArtistDeferred = async {
            contentRepository.getAllArtistFlow().first()
        }
        val allGenreDeferred = async {
            contentRepository.getAllGenreFlow().first()
        }
        val allPlayListDeferred = async {
            playListRepository.getAllPlayListFlow().first()
        }

        UiState.Ready(
            currentTabs = currentSettingDeferred.await(),
            allAvailableTabSectors = mutableListOf<TabSector>()
                .apply {
                    add(
                        TabSector(
                            Res.string.home,
                            listOf(
                                CustomTab.AllMusic,
                                CustomTab.AllPlayList,
                                CustomTab.AllAlbum,
                                CustomTab.AllArtist,
                                CustomTab.AllGenre,
                            )
                        )
                    )


                    val albumTabs = albumsDeferred.await().map {
                        CustomTab.AlbumDetail(it.id, it.name)
                    }
                    add(
                        TabSector(
                            Res.string.album_page_title,
                            albumTabs
                        )
                    )

                    val playListTabs = allPlayListDeferred.await().map {
                        CustomTab.PlayListDetail(it.id, it.name)
                    }
                    add(
                        TabSector(
                            Res.string.playlist_page_title,
                            playListTabs
                        )
                    )

                    val artistTabs = allArtistDeferred.await().map {
                        CustomTab.ArtistDetail(it.id, it.name)
                    }
                    add(
                        TabSector(
                            Res.string.artist_page_title,
                            artistTabs
                        )
                    )

                    val genreTabs = allGenreDeferred.await().map {
                        CustomTab.GenreDetail(it.id, it.name)
                    }
                    add(
                        TabSector(
                            Res.string.genre_title,
                            genreTabs
                        )
                    )
                }
                .toList()
        )
    }
}

internal sealed interface UiState {
    data object Loading : UiState

    data class Ready(
        val currentTabs: List<CustomTab> = emptyList(),
        val allAvailableTabSectors: List<TabSector> = emptyList(),
    ) : UiState
}

internal data class TabSector(
    val sectorTitle: StringResource,
    val sectorContent: List<CustomTab>
)
