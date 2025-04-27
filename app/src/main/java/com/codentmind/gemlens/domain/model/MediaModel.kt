package com.codentmind.gemlens.domain.model

import android.graphics.Bitmap
import android.net.Uri

/**
 * @Details :ImageModel
 * @Author Roshan Bhagat
 */
data class MediaModel(
    val imageUri: Uri = Uri.EMPTY,
    val fileName: String = "",
    val bitmap: Bitmap? = null
)