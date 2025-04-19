package com.codentmind.gemlens.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codentmind.gemlens.R
import com.codentmind.gemlens.domain.model.Message
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
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.no_message_icon),
                contentDescription = stringResource(R.string.no_message)
            )
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = stringResource(R.string.no_message),
                fontWeight = FontWeight.W500,
                fontSize = 15.sp
            )
        }
    }
    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}
