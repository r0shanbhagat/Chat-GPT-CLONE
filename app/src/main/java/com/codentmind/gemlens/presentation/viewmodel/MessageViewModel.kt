package com.codentmind.gemlens.presentation.viewmodel


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codentmind.gemlens.GemLensApp
import com.codentmind.gemlens.R
import com.codentmind.gemlens.core.BaseViewModel
import com.codentmind.gemlens.domain.model.AssistChipModel
import com.codentmind.gemlens.domain.model.FLOW
import com.codentmind.gemlens.domain.model.MediaModel
import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.domain.model.Mode
import com.codentmind.gemlens.domain.repository.GeminiAIRepo
import com.codentmind.gemlens.domain.repository.MessageRepository
import com.codentmind.gemlens.presentation.state.MessageUiState
import com.codentmind.gemlens.utils.Constant.ROLE_MODEL
import com.codentmind.gemlens.utils.Constant.ROLE_USER
import com.codentmind.gemlens.utils.Constant.TAG
import com.codentmind.gemlens.utils.clearImageDir
import com.codentmind.gemlens.utils.getAiMediaList
import com.codentmind.gemlens.utils.getBitmapFromFileName
import com.codentmind.gemlens.utils.isValidString
import com.codentmind.gemlens.utils.recycleBitmap
import com.google.firebase.ai.Chat
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel for managing messages and interacting with the Gemini AI model and message repository.
 *
 * @property geminiAIRepo Repository for interacting with the Gemini AI model.
 * @property repository Repository for managing messages in the local database.
 */
class MessageViewModel(
    private val geminiAIRepo: GeminiAIRepo,
    private val repository: MessageRepository,
    private val dispatcherIO: CoroutineDispatcher
) : BaseViewModel() {

    // Mutable state flow to hold the current UI state, which includes a list of messages.
    private val _uiState: MutableStateFlow<MessageUiState> =
        MutableStateFlow(MessageUiState(emptyList()))

    // Exposes the UI state as a read-only state flow for observation by the UI.
    val uiState: StateFlow<MessageUiState> = _uiState

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState: MutableStateFlow<ValidationState> = _validationState

    private val _isHomeVisit = MutableLiveData(false)
    val isHomeVisit: LiveData<Boolean> = _isHomeVisit

    private val model by lazy { geminiAIRepo.getGenerativeModel() }

    private var chat: Chat? = null
    private val chatMessageJob = SupervisorJob()
    private val chatMessageScope = CoroutineScope(dispatcherIO + chatMessageJob)
    private var databaseJob: Job? = null

    /**
     * Initializes the ViewModel by Collecting all messages from the repository and updating the UI state.
     * This ensures that the UI is populated with existing messages from the local database upon initialization.
     */
    init {
        viewModelScope.launch(dispatcherIO) {
            repository.getAllMessages().onEach {
                _uiState.value = MessageUiState(it)
            }.first()
        }
    }

    /**
     * Initiates a multi-turn query to the Gemini AI model.
     *
     * This function adds the user's prompt to the message list and then sends the prompt
     * along with any provided media to the AI model. It updates the UI state to reflect
     * the ongoing generation and handles the response.
     *
     */
    fun sendMessage(
        prompt: String,
        mediaList: MutableList<MediaModel>?,
        flow: FLOW = FLOW.DEFAULT,
    ) {
        chatMessageScope.launch {
            //Deleting Error if exists any
            deleteErrorItem()
            var aiMediaList = emptyList<MediaModel>()
            if (flow == FLOW.DEFAULT) {
                aiMediaList = mediaList.getAiMediaList()
                mediaList?.clear()

                _uiState.value.messages.add(
                    Message(
                        text = prompt,
                        imageUris = aiMediaList.map { it.fileName },
                        mode = Mode.USER
                    )
                )
            }

            _uiState.value.messages.add(
                Message(
                    text = GemLensApp.getInstance().getString(R.string.generating),
                    mode = Mode.GEMINI,
                    isGenerating = true
                )
            )

            if (chat == null) {
                chat = getChat()
            }

            // val msg = "Look at the image(s), and then answer the following question: $prompt"
            var inputContent: Any = prompt
            if (aiMediaList.isNotEmpty()) {
                inputContent = content {
                    aiMediaList.forEach { model ->
                        model.bitmap?.let { image(it) }
                    }
                    text(prompt)
                }
            }
            sendStreamQuery(_uiState.value.messages, inputContent, aiMediaList, flow)
        }
    }

    /**
     * send Stream Query to the Gemini AI model and handles the response.
     *
     * This function sends either a text prompt or a content object (including text and images)
     * to the AI model. It streams the response, updates the UI state with each chunk, and
     * finally updates the local database with the full response.
     */
    @SuppressLint("LogNotTimber")
    private suspend fun sendStreamQuery(
        result: MutableList<Message>,
        feed: Any,
        aiMediaList: List<MediaModel>,
        flow: FLOW = FLOW.DEFAULT
    ) {
        var output = ""
        try {
            val stream = when {
                feed is String -> chat?.sendMessageStream("What’s trending today?")
                else -> model.generateContentStream(feed as Content)
            }
            stream?.collect { chunk ->
                output += chunk.text.toString()
                output.trimStart()
                result[result.lastIndex] =
                    Message(text = output, mode = Mode.GEMINI, isGenerating = true)
            }
            Log.v(TAG, output)
            result[result.lastIndex] = Message(text = output, mode = Mode.GEMINI)

            //Updating into DB
            updateToDB(feed, output, aiMediaList, flow)
        } catch (ex: Exception) {
            Timber.e(ex) //Print logs
            val mode = if (output.isValidString()) Mode.GEMINI else Mode.ERROR

            //User cancelled the request
            if (ex is CancellationException) {
                result[result.lastIndex] =
                    Message(text = output, mode = mode)

            } else {
                //Updating into DB $FROM GEMINI
                result[result.lastIndex] = Message(
                    text = output,
                    mode = mode,
                )
            }

            //Updating into DB
            updateToDB(feed, output, aiMediaList, flow)
        }
    }

    /**
     * Updates the local database with the new message exchange.
     *
     * This function extracts text and image URIs from the input feed and creates a new
     * message entry in the database. It also adds a separate entry for the AI's response.
     * This ensures that the entire conversation history is stored locally for persistence.
     *
     * @param feed The input content sent to the AI model.
     * @param output The text response received from the AI model.
     */
    private fun updateToDB(
        feed: Any,
        output: String,
        aiMediaList: List<MediaModel>,
        flow: FLOW,
    ) {
        databaseJob = viewModelScope.launch(dispatcherIO) {
            var text = ""
            if (feed is Content) {
                feed.parts.filterIsInstance<TextPart>().forEach {
                    text += it.text
                }
//                feed.parts.filterIsInstance<ImagePart>().forEach {
//                    imageUris.add(it.image.getLocalImageUri())
//                }
            } else {
                text = feed.toString()
            }

            //Updating into DB $FROM USER
            if (flow == FLOW.DEFAULT) {
                repository.insertMessage(
                    Message(
                        text = text,
                        imageUris = aiMediaList.map { it.fileName })
                )
            }

            val mode = if (output.isValidString()) Mode.GEMINI else Mode.ERROR
            repository.insertMessage(Message(text = output, mode = mode))
            recycleBitmap(aiMediaList)
        }
    }

    internal fun onRetryMessage() {
        viewModelScope.launch {
            val message = uiState.value.messages[uiState.value.messages.lastIndex - 1]
            val mediaList: List<MediaModel> = message.imageUris.map {
                val fileName = it
                val bitmap = getBitmapFromFileName(fileName)
                MediaModel(bitmap = bitmap, fileName = fileName)
            }
            sendMessage(message.text, mediaList.toMutableList(), FLOW.RETRY)
        }
    }


    /**
     * Deletes the last error message from the UI state and the local database.
     *
     * This function checks if there are any messages in the UI state. If the last message
     * is an error message, it removes it from both the UI state and the local database.
     * This is typically used to clean up error messages after they have been acknowledged.
     */
    private fun deleteErrorItem() {
        if (uiState.value.messages.isNotEmpty()) {
            val message = uiState.value.messages.last()
            if (message.mode == Mode.ERROR) {
                _uiState.value.messages.remove(message)
                viewModelScope.launch(dispatcherIO) {
                    repository.deleteMessage(message)
                }
            }
        }

    }

    /**
     * Clears the current chat context and deletes all messages.
     *
     * This function resets the chat history by clearing the chat object and deleting all messages
     * from both the local database and the UI state. It also clears any cached data.
     */
    fun clearContext() {
        databaseJob?.cancel()
        chatMessageJob.cancelChildren()
        chat = getChat()
        viewModelScope.launch(dispatcherIO) {
            _uiState.value = MessageUiState()
            clearImageDir()
            delay(100)
            repository.deleteAllMessages()
        }
    }


    /**
     * Resets the validation state to idle.
     *
     * This function clears the current validation state, setting it back to the default
     * idle state, typically used after a validation attempt.
     */
    fun resetValidationState() {
        _validationState.value = ValidationState.Idle
    }


    /**
     * Retrieves the current chat instance. If no chat exists, a new one is created with the previous chat history.
     *
     * @return The chat instance.
     */
    private fun getChat() = model.startChat(generatePreviousChats())


    /**
     * Generates a list of Content objects representing the previous chat history.
     *
     * This function iterates through the existing messages in the UI state, creating a
     * Content object for each message. The role is set to "user" for user messages and
     * "model" for Gemini AI messages. This history is used to maintain context in
     * multi-turn conversations.
     *
     * @return A list of Content objects representing the chat history.
     */
    private fun generatePreviousChats(): List<Content> {
        val history = mutableListOf<Content>()
        for (message in _uiState.value.messages) {
            history.add(content(role = if (message.mode == Mode.USER) ROLE_USER else ROLE_MODEL) {
                text(
                    message.text
                )
            })
        }
        return history
    }


    /**
     * Marks the current interaction as a home visit.
     *
     * This function sets a flag indicating that the user is interacting with the app for
     * the first time or has returned to the home screen. It also resets the validation
     * state, typically used in conjunction with API key validation on the home screen.
     *
     */
    fun makeHomeVisit() {
        _isHomeVisit.value = true
        resetValidationState()
    }


    /**
     * Stops the content generation process by canceling any ongoing coroutines.
     *
     * This function is typically called when the user navigates away from the chat screen
     * or when the app is closed. It ensures that any ongoing content generation tasks are
     * properly terminated to avoid memory leaks or unnecessary processing.
     *
     */
    fun stopContentGeneration() {
        chatMessageJob.cancelChildren()
    }


    /**
     * Updates the quick prompt query in the UI state.
     *
     * This function sets the quick prompt query text in the UI state, typically used
     * to update the UI with a new prompt or suggestion for the user.
     *
     * @param model The AssistChipModel containing the query text.
     */
    fun quickPromptQuery(model: AssistChipModel) {
        _uiState.value.quickPromptQuery = model.queryText
    }


    /**
     * Clears the chat message scope and cancels any ongoing coroutines.
     *
     * This function is typically called when the ViewModel is no longer needed or
     * when the user navigates away from the chat screen. It ensures that any ongoing
     * tasks are properly terminated to avoid memory leaks or unnecessary processing.
     *
     */
    override fun onCleared() {
        super.onCleared()
        chatMessageScope.cancel()
    }


    sealed class ValidationState {
        data object Idle : ValidationState()
        data object Checking : ValidationState()
        data object Valid : ValidationState()
        data object Invalid : ValidationState()
    }
}
