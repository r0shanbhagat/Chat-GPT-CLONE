package com.codentmind.gemlens.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(imageVector: ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(
                colorScheme.onSurface.copy(alpha = 0.1f),
                CircleShape
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "CustomIconButton",
            tint = colorScheme.onSurface
        )
    }
}