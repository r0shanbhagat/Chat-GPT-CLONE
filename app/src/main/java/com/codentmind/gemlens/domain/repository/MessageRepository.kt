package com.codentmind.gemlens.domain.repository

import com.codentmind.gemlens.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    /**
     * Retrieves a message from the database based on the provided ID.
     *
     * @param id The unique identifier of the message to retrieve.
     * @return A Flow emitting a list of MessageEntity objects matching the ID.
     */
    suspend fun getMessageById(id: Int): Message

    /**
     * Retrieves all messages from the database.
     *
     * @return A Flow emitting a list of all MessageEntity objects.
     */
    fun getAllMessages(): Flow<List<Message>>

    /**
     * Inserts a new message into the database.
     *
     * @param message The MessageEntity object to be inserted.
     */
    suspend fun insertMessage(message: Message)

    /**
     * Deletes all messages from the database.
     *
     * This operation removes all entries from the messages table.
     */
    suspend fun deleteAllMessages()

    /**
     * Deletes Specific messages from the database.
     *
     * This operation removes single entry from the messages table.
     */
    suspend fun deleteMessage(message: Message)

}