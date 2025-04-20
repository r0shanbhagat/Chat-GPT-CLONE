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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
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
import com.codentmind.gemlens.utils.AnalyticsHelper.logButtonClick
import com.codentmind.gemlens.utils.speakToAdd
import com.codentmind.gemlens.utils.vibrate


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

    fun sendQuery(userMessage: String) {
        keyboardController?.hide()
        focusManager.clearFocus()
        logButtonClick("SendQuery")
        viewModel.makeMultiTurnQuery(
            context,
            userMessage.trim(),
            mediaList
        )
        text = TextFieldValue("")
        mediaList?.clear()

        context.vibrate()
    }

    val speakLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val userMessage =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            sendQuery(userMessage)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
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

        OutlinedTextField(
            value = text,
            onValueChange = { newText -> text = newText },
            placeholder = {
                Text(
                    color = colorScheme.inversePrimary,
                    text = stringResource(R.string.message_placeholder)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            expanded = true
                        },
                    tint = colorScheme.primary
                )
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(colorScheme.background),
            shape = RoundedCornerShape(30),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray,
                focusedContainerColor = colorScheme.background,
                unfocusedContainerColor = colorScheme.background,
                cursorColor = Color.DarkGray
            ),
            maxLines = 5,
            trailingIcon = {
                Box(
                    modifier = Modifier.padding(end = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating != true) {
                        if (text.text.isBlank()) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = stringResource(R.string.action_mic),
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        context.speakToAdd(speakLauncher)
                                    },
                                tint = colorScheme.primary
                            )
                        } else {
                            IconButton(Icons.AutoMirrored.Filled.Send) {
                                sendQuery(text.text)
                            }
                        }
                    } else {
                        IconButton(Icons.Default.Stop) {
                            viewModel.stopGenerating()
                        }
                        context.vibrate()
                    }
                }
            },
            textStyle = TextStyle(
                fontWeight = FontWeight.W400,
                fontSize = 17.sp
            )
        )
    }

}
