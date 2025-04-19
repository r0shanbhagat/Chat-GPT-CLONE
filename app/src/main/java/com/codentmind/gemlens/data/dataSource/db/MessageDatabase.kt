package com.codentmind.gemlens.data.dataSource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codentmind.gemlens.domain.converter.ChatMessageConverters
import com.codentmind.gemlens.domain.converter.Converters
import com.codentmind.gemlens.domain.converter.ListConverters

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class,
    ListConverters::class,
    ChatMessageConverters::class
)
abstract class MessageDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null

        fun getInstance(context: Context): MessageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java,
                    "message.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}