package com.codentmind.gemlens.domain.mapper

import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.data.dataSource.db.MessageEntity
import java.util.Date

fun MessageEntity.toMessage(): Message {
    return Message(
        isGenerating = isGenerating,
        text = text,
        mode = mode,
        id = id,
        cId = cId,
        time = time,
        imageUris = imageUris,
        isFavorite = isFavorite
    )
}

fun Message.toMessageEntity(): MessageEntity {
    return MessageEntity(
        cId = cId,
        mode = mode,
        text = text,
        time = Date(),
        isGenerating = false,
        isFavorite = false,
        imageUris = imageUris
    )
}