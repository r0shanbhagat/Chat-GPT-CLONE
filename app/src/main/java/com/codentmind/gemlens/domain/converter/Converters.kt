package com.codentmind.gemlens.domain.converter

import androidx.room.TypeConverter
import com.codentmind.gemlens.data.dataSource.db.MessageEntity
import com.codentmind.gemlens.utils.NetworkUtil.Companion.jsonToModel
import com.codentmind.gemlens.utils.NetworkUtil.Companion.modelToJson
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date?): Long? {
        return date?.time
    }
}

class ChatMessageConverters {
    //private var gson: Gson = Gson()

    @TypeConverter
    fun stringToChatMessageList(data: String): List<MessageEntity> {
        return jsonToModel<List<MessageEntity>>(data)
    }

    @TypeConverter
    fun chatMessageListToString(someObjects: List<MessageEntity>): String {
        return modelToJson(someObjects)
    }
}


class ListConverters {
    @TypeConverter
    fun stringToStringList(data: String): List<String> {
        return jsonToModel<List<String>>(data)
    }

    @TypeConverter
    fun stringListToString(someObjects: List<String>): String {
        return modelToJson(someObjects)
    }
}