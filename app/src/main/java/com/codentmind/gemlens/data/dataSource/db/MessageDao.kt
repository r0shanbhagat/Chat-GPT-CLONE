package com.codentmind.gemlens.data.dataSource.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.codentmind.gemlens.domain.model.Mode
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Upsert
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM message")
    fun getAllMessage(): Flow<List<MessageEntity>>

    @Query("DELETE FROM message")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM message WHERE mode = :mode")
    suspend fun delete(mode: Mode)

    @Query("SELECT * FROM message WHERE id=:id")
    suspend fun getMessageById(id: Int): MessageEntity
}