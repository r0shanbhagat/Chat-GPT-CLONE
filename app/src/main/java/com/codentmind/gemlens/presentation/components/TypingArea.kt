package com.codentmind.gemlens.presentation.components

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codentmind.gemlens.R
import com.codentmind.gemlens.domain.model.MediaModel
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import com.codentmind.gemlens.utils.speakToAdd


@Composable
fun TypingArea(
    viewModel: MessageViewModel,
    mediaList: SnapshotStateList<MediaModel>? = null,
    galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? = null,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>? = null
) {
    val chatUiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val isGenerating: Boolean? = chatUiState.messages.lastOrNull()?.isGenerating

    val speakLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val userMessage =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            text = TextFieldValue("")
            viewModel.makeMultiTurnQuery(
                context,
                userMessage.trim(),
                mediaList
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 10.dp,
                bottom = 10.dp,
                end = 10.dp,
                start = 0.dp
            )
            .background(colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {


        var expanded by remember { mutableStateOf(false) }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.background(colorScheme.secondaryContainer)
        ) {
            DropdownMenuItem(
                modifier = Modifier.background(colorScheme.secondaryContainer),
                onClick = {
                    expanded = false
                    permissionLauncher?.launch(Manifest.permission.CAMERA)
                },
                text = {
                    Row {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.add_camera_icon),
                            tint = colorScheme.primary,
                            contentDescription = "camera"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            color = colorScheme.primary,
                            text = "Camera", fontSize = 15.sp, fontWeight = FontWeight.W600
                        )
                    }
                },
            )


            DropdownMenuItem(
                modifier = Modifier.background(colorScheme.secondaryContainer),
                onClick = {
                    expanded = false
                    galleryLauncher?.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                text = {
                    Row {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.add_gallery_icon),
                            tint = colorScheme.primary,
                            contentDescription = "gallery"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            color = colorScheme.primary,
                            text = "Gallery", fontSize = 15.sp, fontWeight = FontWeight.W600
                        )
                    }
                }
            )
        }


        IconButton(onClick = {
            expanded = true
        }) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(id = R.drawable.add_icon),
                tint = colorScheme.primary,
                contentDescription = "add"
            )
        }

        OutlinedTextField(
            value = text,
            onValueChange = { newText -> text = newText },
            placeholder = {
                Text(
                    color = colorScheme.inversePrimary,
                    text = "Ask a question"
                )
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorScheme.background),
            shape = RoundedCornerShape(28),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorScheme.primary,
                unfocusedIndicatorColor = Color.LightGray,
                focusedContainerColor = colorScheme.background,
                unfocusedContainerColor = colorScheme.background,
                cursorColor = colorScheme.primary
            ),
            maxLines = 5,
            trailingIcon = {
                Box(
                    modifier = Modifier.padding(end = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating != true) {
                        Icon(
                            //    painter = painterResource(id = R.drawable.send_icon),
                            if (text.text.isNotBlank()) {
                                painterResource(R.drawable.send_icon)
                            } else {
                                painterResource(R.drawable.ic_voice)
                            },
                            contentDescription = stringResource(R.string.action_send),
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    if (text.text.trim().isNotEmpty()) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        viewModel.makeMultiTurnQuery(
                                            context,
                                            text.text.trim(),
                                            mediaList
                                        )
                                        text = TextFieldValue("")
                                    } else {
                                        context.speakToAdd(speakLauncher)
                                    }
                                },
                            tint = colorScheme.primary
                        )
                    } else {
                        val strokeWidth = 2.dp
                        CircularProgressIndicator(
                            modifier = Modifier
                                .drawBehind {
                                    drawCircle(
                                        Color.Black,
                                        radius = size.width / 2 - strokeWidth.toPx() / 2,
                                        style = Stroke(strokeWidth.toPx())
                                    )
                                }
                                .size(30.dp),
                            color = Color.LightGray,
                            strokeWidth = strokeWidth
                        )
                    }
                }
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 18.sp
            )
        )
    }
}