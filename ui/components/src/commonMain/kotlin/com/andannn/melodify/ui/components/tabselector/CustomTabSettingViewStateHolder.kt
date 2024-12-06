package com.andannn.melodify.ui.components.tabselector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.andannn.melodify.core.data.model.CustomTab
import com.andannn.melodify.core.data.repository.MediaContentRepository
import com.andannn.melodify.core.data.repository.PlayListRepository
import com.andannn.melodify.core.data.repository.UserPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import melodify.ui.common.generated.resources.Res
import melodify.ui.common.generated.resources.album_page_title
import melodify.ui.common.generated.resources.artist_page_title
import melodify.ui.common.generated.resources.genre_title
import melodify.ui.common.generated.resources.playlist_page_title
import org.jetbrains.compose.resources.StringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun rememberCustomTabSettingViewStateHolder(
    playListRepository: PlayListRepository = getKoin().get<PlayListRepository>(),
    contentRepository: MediaContentRepository = getKoin().get<MediaContentRepository>(),
    userPreferenceRepository: UserPreferenceRepository = getKoin().get<UserPreferenceRepository>(),
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember(
    playListRepository,
    contentRepository,
    userPreferenceRepository,
) {
    CustomTabSettingViewStateHolder(
        playListRepository,
        contentRepository,
        userPreferenceRepository,
        scope,
    )
}

sealed interface UiEvent {
    data class OnSelectedChange(
        val tab: CustomTab,
        val isSelected: Boolean,
    ) : UiEvent

    data class OnUpdateTabs(
        val newTabs: List<CustomTab>,
    ) : UiEvent
}

private const val TAG = "CustomTabSettingViewState"

class CustomTabSettingViewStateHolder(
    playListRepository: PlayListRepository,
    contentRepository: MediaContentRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    scope: CoroutineScope,
) : CoroutineScope by scope {
    val state =
        combine(
            userPreferenceRepository.currentCustomTabsFlow,
            contentRepository.getAllAlbumsFlow(),
            contentRepository.getAllArtistFlow(),
            contentRepository.getAllGenreFlow(),
            playListRepository.getAllPlayListFlow(),
        ) { tabs, albums, artists, genre, playlist ->
            TabUiState(
                currentTabs = tabs,
                allAvailableTabSectors =
                    mutableListOf<TabSector>()
                        .apply {
                            val albumTabs =
                                albums.map {
                                    CustomTab.AlbumDetail(it.id, it.name)
                                }
                            add(
                                TabSector(
                                    Res.string.album_page_title,
                                    albumTabs,
                                ),
                            )

                            val playListTabs =
                                playlist.map {
                                    CustomTab.PlayListDetail(it.id, it.name)
                                }
                            add(
                                TabSector(
                                    Res.string.playlist_page_title,
                                    playListTabs,
                                ),
                            )

                            val artistTabs =
                                artists.map {
                                    CustomTab.ArtistDetail(it.id, it.name)
                                }
                            add(
                                TabSector(
                                    Res.string.artist_page_title,
                                    artistTabs,
                                ),
                            )

                            val genreTabs =
                                genre.map {
                                    CustomTab.GenreDetail(it.id, it.name)
                                }
                            add(
                                TabSector(
                                    Res.string.genre_title,
                                    genreTabs,
                                ),
                            )
                        }.toList(),
            )
        }.stateIn(scope = scope, SharingStarted.WhileSubscribed(), TabUiState())

    fun onEvent(event: UiEvent) {
        val state = state.value

        when (event) {
            is UiEvent.OnSelectedChange -> {
                val (tab, selected) = event
                launch {
                    if (selected) {
                        userPreferenceRepository.updateCurrentCustomTabs(
                            state.currentTabs + tab,
                        )
                    } else {
                        userPreferenceRepository.updateCurrentCustomTabs(
                            state.currentTabs - tab,
                        )
                    }
                }
            }

            is UiEvent.OnUpdateTabs -> {
                launch {
                    userPreferenceRepository.updateCurrentCustomTabs(
                        event.newTabs,
                    )
                }
            }
        }
    }
}

data class TabUiState(
    val currentTabs: List<CustomTab> = emptyList(),
    val allAvailableTabSectors: List<TabSector> = emptyList(),
)

data class TabSector(
    val sectorTitle: StringResource,
    val sectorContent: List<CustomTab>,
)
