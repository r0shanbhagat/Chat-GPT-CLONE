package com.codentmind.gemlens.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.domain.model.Mode
import com.codentmind.gemlens.presentation.screens.ImagePreviewScreen
import com.codentmind.gemlens.presentation.theme.BgColor
import com.codentmind.gemlens.presentation.theme.TextColor
import com.codentmind.gemlens.utils.getAbsolutePath

@Composable
fun ChatBubbleItem(
    chatMessage: Message,
    onRetryClick: () -> Unit,
) {
    val isGEMINIMessage = chatMessage.mode == Mode.GEMINI
    val backgroundColor = when (chatMessage.mode) {
        Mode.GEMINI -> MaterialTheme.colorScheme.primaryContainer
        Mode.USER -> BgColor
        Mode.ERROR -> Color.Transparent
    }
    val textColor = when (chatMessage.mode) {
        Mode.GEMINI -> TextColor
        Mode.USER -> Color.White
        Mode.ERROR -> Color.Transparent
    }

    val bubbleShape = if (isGEMINIMessage) {
        RoundedCornerShape(0.dp, 0.dp, 0.dp, 0.dp)
    } else {
        RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp)
    }

    val horizontalAlignment = if (isGEMINIMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = horizontalAlignment,
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(chatMessage.imageUris) { imageUri ->
                RoundedCornerAsyncImage(imageUri.getAbsolutePath())
            }
        }

        BoxWithConstraints {
            Card(
                shape = bubbleShape,
                colors = CardDefaults.cardColors(
                    backgroundColor
                ),
                modifier = Modifier
                    .padding(top = 5.dp)
                    .widthIn(0.dp, maxWidth * 0.9f)
            ) {
                Column {
                    if (chatMessage.mode == Mode.ERROR) {
                        ErrorMessageUI(onRetryClick)
                    } else {
                        SelectionContainer {
                            Text(
                                modifier = Modifier.padding(
                                    start = 14.dp,
                                    end = 14.dp,
                                    top = 7.dp,
                                    bottom = 7.dp
                                ),
                                fontWeight = FontWeight.W500,
                                color = textColor,
                                fontSize = 13.sp,
                                text = formatCode(chatMessage.text)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun formatCode(text: String): AnnotatedString {
    val boldRegex = """\*\*(.*?)\*\*""".toRegex()
    val codeRegex = """```([\s\S]*?)```""".toRegex()
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        val matches = boldRegex.findAll(text) + codeRegex.findAll(text)
        matches.sortedBy { it.range.first }.forEach { matchResult ->
            // Apply style based on the matched pattern
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1
            if (startIndex > lastIndex) {
                // Append regular text
                append(text.substring(lastIndex, startIndex))
            }
            if (matchResult.groupValues.size >= 2) {
                // Handle bold text
                if (matchResult.value.startsWith("**") && matchResult.value.endsWith("**")) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(matchResult.groupValues[1])
                    }
                }
                // Handle code block
                else if (matchResult.value.startsWith("```") && matchResult.value.endsWith("```")) {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = FontFamily.SansSerif,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(matchResult.groupValues[1])
                    }
                }
            }
            lastIndex = endIndex
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
    return annotatedString
}


@Composable
fun RoundedCornerAsyncImage(imageUrl: String) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        ImagePreviewScreen(imageUrl, {
            showDialog.value = false
        }, {}, {})

    }
    AsyncImage(
        model = imageUrl,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            //  .defaultMinSize(200.dp, 260.dp)
            .height(141.dp)
            .widthIn(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    showDialog.value = true
                }),
        contentDescription = "AsyncImage",
    )
}