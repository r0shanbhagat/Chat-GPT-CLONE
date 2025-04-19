package com.codentmind.gemlens.presentation.state

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.toMutableStateList
import com.codentmind.gemlens.domain.model.Message

class MessageUiState(
    messages: List<Message> = emptyList()
) {
    private val _messages: MutableList<Message> = messages.toMutableStateList()
    val messages: MutableList<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(msg)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isGenerating = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }
}