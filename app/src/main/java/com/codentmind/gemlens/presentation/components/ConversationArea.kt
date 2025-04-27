package com.codentmind.gemlens.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.presentation.screens.ChatScreenIntro
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import com.codentmind.gemlens.utils.isNetworkConnected
import kotlinx.coroutines.launch

@Composable
fun ConversationArea(viewModel: MessageViewModel) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToBottom by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }
    var showSettingsDialog by remember { mutableStateOf(false) }

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
            ChatScreenIntro(viewModel)
        }
    }
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            items(chatMessages.asReversed()) { message ->
                ChatBubbleItem(message) {
                    if (!isNetworkConnected()) {
                        showSettingsDialog = true
                        return@ChatBubbleItem
                    }
                    viewModel.onRetryMessage()
                }
            }
        }


        AnimatedVisibility(
            visible = showScrollToBottom && !listState.isScrollInProgress,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CircleFABWithIcon(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                icon = Icons.Default.KeyboardArrowDown
            )
        }
    }

    NoInternetConnectionDialog(
        showDialog = showSettingsDialog,
        onDismiss = { showSettingsDialog = false }
    )
}
