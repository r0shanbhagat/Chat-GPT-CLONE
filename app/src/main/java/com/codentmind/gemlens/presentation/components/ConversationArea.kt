package com.codentmind.gemlens.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.presentation.screens.ChatScreenDefault
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel

@Composable
fun ConversationArea(viewModel: MessageViewModel) {
    val chatUiState by viewModel.uiState.collectAsState()
    val chatMessages: List<Message> = chatUiState.messages
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (chatMessages.isEmpty()) {
            ChatScreenDefault(viewModel)
        }
    }
    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message) {
                viewModel.onRetry()
            }
        }
    }
}