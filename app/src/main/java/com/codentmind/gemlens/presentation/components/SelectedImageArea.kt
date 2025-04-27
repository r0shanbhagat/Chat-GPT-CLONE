package com.codentmind.gemlens.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.codentmind.gemlens.domain.model.MediaModel
import com.codentmind.gemlens.presentation.theme.DecentRed

@Composable
fun SelectedImageArea(mediaList: SnapshotStateList<MediaModel>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(mediaList.size) { index ->
            val mediaModel = mediaList[index]
            val iconSize = 20.dp
            val offsetInPx =
                LocalDensity.current.run { ((iconSize - 5.dp) / 2).roundToPx() }

            Box(
                modifier = Modifier
                    .padding(iconSize / 2)
            ) {
                Card {
                    Image(
                        painter = rememberAsyncImagePainter(mediaModel.imageUri),
                        contentDescription = "image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )
                }
                IconButton(
                    onClick = { mediaList.remove(mediaModel) },
                    modifier = Modifier
                        .offset {
                            IntOffset(x = +offsetInPx, y = -offsetInPx)
                        }
                        .clip(CircleShape)
                        .background(DecentRed)
                        .size(iconSize)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        modifier = Modifier.padding(3.dp),
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
