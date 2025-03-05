package com.andannn.melodify.ui.common.widgets

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.andannn.melodify.ui.common.icons.SmpIcon

@Composable
fun SmpIcon(item: SmpIcon, contentDescription: String = "") {
    when (item) {
        is SmpIcon.ImageVectorIcon -> {
            Icon(imageVector = item.imageVector, contentDescription = contentDescription)
        }
    }
}