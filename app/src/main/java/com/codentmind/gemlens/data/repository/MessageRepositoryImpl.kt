package com.codentmind.gemlens.data.repository

import com.codentmind.gemlens.data.dataSource.db.MessageDatabase
import com.codentmind.gemlens.domain.mapper.toMessage
import com.codentmind.gemlens.domain.mapper.toMessageEntity
import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Message repository impl
 *
 * @constructor
 *
 * @param db
 */
class MessageRepositoryImpl(
    db: MessageDatabase
) : MessageRepository {

    private val dao = db.messageDao

    override suspend fun getMessageById(id: Int): Message {
        return dao.getMessageById(id).toMessage()
    }

    override fun getAllMessages(): Flow<List<Message>> {
        return dao.getAllMessage().map { it.map { msg -> msg.toMessage() } }
    }

    override suspend fun insertMessage(message: Message) {
        dao.insertMessage(message.toMessageEntity())
    }

    override suspend fun deleteAllMessages() {
        dao.deleteAllMessages()
    }

    override suspend fun deleteMessage(message: Message) {
        dao.delete(message.mode)
    }
}