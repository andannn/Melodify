package com.andannn.melodify.feature.common.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SmpTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    text: String? = null,
    enabled: Boolean = true,
    textAlpha: Float = 1f
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors()
    ) {
        if (imageVector != null) {
            Icon(imageVector = imageVector, contentDescription = null)
        }

        if (text != null) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                modifier = Modifier.alpha(textAlpha),
                text = text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun SmpTextButtonPreview() {
    MaterialTheme {
        SmpTextButton(
            imageVector = Icons.Rounded.PlayArrow,
            text = "Test",
            onClick = {}
        )
    }
}
