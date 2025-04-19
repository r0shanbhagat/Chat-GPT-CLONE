package com.codentmind.gemlens.domain.model

import android.graphics.Bitmap

/**
 * @Details :ImageModel
 * @Author Roshan Bhagat
 */
data class MediaModel(
    val bitmap: Bitmap,
    val imageUri: String = ""
)