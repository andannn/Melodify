package com.andannn.melodify.feature.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun FavoriteIconButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        if (isFavorite) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "",
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.FavoriteBorder,
                contentDescription = "",
            )
        }
    }
}