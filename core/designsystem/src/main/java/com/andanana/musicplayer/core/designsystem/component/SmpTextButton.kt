package com.andanana.musicplayer.core.designsystem.component

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max

@Composable
fun SmpTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector? = null,
    text: String
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors()
    ) {
        if (imageVector != null) {
            Icon(imageVector = imageVector, contentDescription = null)
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, maxLines = 1)
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
