package com.codentmind.gemlens.domain.model

import com.codentmind.gemlens.data.dataSource.db.MessageEntity
import java.util.Date
import java.util.UUID

enum class Mode {
    USER,
    GEMINI,
    ERROR
}

enum class FLOW {
    DEFAULT,
    RETRY,
}

data class Message(
    val cId: String = UUID.randomUUID().toString(),
    val id: Int = 0,
    var text: String,
    val mode: Mode = Mode.USER,
    var isGenerating: Boolean = false,
    val imageUris: List<String> = emptyList(),
    val time: Date = Date(),
    val isFavorite: Boolean = false,
    val msgEntity: MessageEntity? = null
)
