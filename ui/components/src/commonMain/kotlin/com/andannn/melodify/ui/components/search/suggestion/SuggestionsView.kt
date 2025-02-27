package com.andannn.melodify.ui.components.search.suggestion

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.andannn.melodify.core.data.model.MediaItemModel

/**
 * Content of the search bar when expanded.
 *
 * Show history search if the textFiled is empty.
 * Show possible suggestions if the textFiled is not empty.
 * Show best match Result under suggestions if the query string have 60% or more matched contents.
 *
 * @param modifier Modifier to apply to the content.
 * @param query The current query text.
 * @param onConfirmSearch Called when the user submits the query.
 * @param onClickBestMatchedItem Called when the best matched item is clicked.
 */
@Composable
internal fun SuggestionsView(
    query: String,
    modifier: Modifier = Modifier,
    onConfirmSearch: (String) -> Unit = {},
    onClickBestMatchedItem: (MediaItemModel) -> Unit = {},
) {
    val state by rememberSuggestionsStateHolder(query = query).state.collectAsState()
    Text(text = state.toString())
}
