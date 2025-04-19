package com.codentmind.gemlens.data.dataSource.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Upsert
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM message")
    fun getAllMessage(): Flow<List<MessageEntity>>

    @Query("DELETE FROM message")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM message WHERE id=:id")
    fun getAllMessage(id: String): Flow<List<MessageEntity>>
}