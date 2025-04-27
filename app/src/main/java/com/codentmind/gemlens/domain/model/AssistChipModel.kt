package com.codentmind.gemlens.domain.model

import androidx.annotation.DrawableRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AssistChipModel(
    @SerialName("id")
    val id: Int,
    @SerialName("drawableName")
    val drawableName: String,
    @SerialName("label")
    val label: String,
    @SerialName("queryText")
    val queryText: String,
    @DrawableRes
    var drawableId: Int = 0
)