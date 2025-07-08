package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.andannn.melodify.core.data.Repository
import com.andannn.melodify.core.data.model.MediaItemModel
import com.andannn.melodify.ui.components.common.LocalRepository
import com.andannn.melodify.ui.components.tabcontent.GroupType
import com.andannn.melodify.ui.components.tabcontent.HeaderKey
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import io.github.aakira.napier.Napier

private const val TAG = "GroupHeaderPresenter"

@Composable
fun rememberGroupHeaderPresenter(
    headerKey: HeaderKey,
    repository: Repository = LocalRepository.current,
) = remember(
    headerKey,
    repository,
) {
    GroupHeaderPresenter(
        headerKey,
        repository,
    )
}

class GroupHeaderPresenter(
    private val headerKey: HeaderKey,
    repository: Repository,
) : Presenter<GroupHeaderState> {
    private val groupType = headerKey.groupType
    private val headerId = headerKey.headerId
    private val mediaContentRepository = repository.mediaContentRepository

    @Composable
    override fun present(): GroupHeaderState {
        Napier.d(tag = TAG) { "GroupHeaderPresenter present $headerKey" }
        val mediaItem by produceRetainedState<MediaItemModel?>(null) {
            value =
                when (groupType) {
                    GroupType.ARTIST -> mediaContentRepository.getArtistByArtistId(headerId!!)
                    GroupType.ALBUM -> mediaContentRepository.getAlbumByAlbumId(headerId!!)
                    GroupType.NONE -> error("invalid group type")
                }
        }

        return GroupHeaderState(
            title = mediaItem?.name ?: "",
            cover = mediaItem?.artWorkUri ?: "",
            trackCount = mediaItem?.trackCount ?: 0,
        )
    }
}

data class GroupHeaderState(
    val title: String,
    val cover: String,
    val trackCount: Int,
) : CircuitUiState
