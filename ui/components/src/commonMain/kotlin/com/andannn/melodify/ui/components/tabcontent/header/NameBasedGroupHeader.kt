package com.andannn.melodify.ui.components.tabcontent.header

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andannn.melodify.ui.components.tabcontent.HeaderItem

@Composable
fun NameBasedGroupHeader(
    item: HeaderItem.Name,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = "# " + item.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
            )
        }
    }
}
