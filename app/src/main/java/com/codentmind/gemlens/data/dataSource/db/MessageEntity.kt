package com.codentmind.gemlens.data.dataSource.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codentmind.gemlens.domain.model.Mode
import java.util.Date

@Entity(tableName = "Message")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cId: String,
    val text: String,
    val mode: Mode,
    val time: Date,
    val isFavorite: Boolean,
    val isGenerating: Boolean = false,
    val imageUris: List<String>,
)