package com.codentmind.gemlens.domain.model

import androidx.annotation.DrawableRes

data class AssistChipModel(
    @DrawableRes
    val icon: Int,
    val text: String,
)