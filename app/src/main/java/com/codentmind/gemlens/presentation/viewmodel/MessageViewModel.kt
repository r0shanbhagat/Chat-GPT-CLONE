package com.codentmind.gemlens.presentation.viewmodel


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codentmind.gemlens.R
import com.codentmind.gemlens.core.BaseViewModel
import com.codentmind.gemlens.domain.model.MediaModel
import com.codentmind.gemlens.domain.model.Message
import com.codentmind.gemlens.domain.model.Mode
import com.codentmind.gemlens.domain.repository.GeminiAIRepo
import com.codentmind.gemlens.domain.repository.MessageRepository
import com.codentmind.gemlens.presentation.state.MessageUiState
import com.codentmind.gemlens.utils.Constant.ROLE_MODEL
import com.codentmind.gemlens.utils.Constant.ROLE_USER
import com.codentmind.gemlens.utils.clearCacheDir
import com.codentmind.gemlens.utils.datastore
import com.codentmind.gemlens.utils.getLocalImageUri
import com.codentmind.gemlens.utils.storeApiKey
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.ImagePart
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for managing messages and interacting with the Gemini AI model and message repository.
 *
 * @property geminiAIRepo Repository for interacting with the Gemini AI model.
 * @property repository Repository for managing messages in the local database.
 */
class MessageViewModel(
    private val geminiAIRepo: GeminiAIRepo,
    private val repository: MessageRepository
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

    /**
     * Initializes the ViewModel by collecting all messages from the repository and updating the UI state.
     * This ensures that the UI is populated with existing messages from the local database upon initialization.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllMessages().collect {
                _uiState.value = MessageUiState(it)
            }
        }
    }


    /**
     * Initiates a multi-turn query to the Gemini AI model.
     *
     * This function adds the user's prompt to the message list and then sends the prompt
     * along with any provided media to the AI model. It updates the UI state to reflect
     * the ongoing generation and handles the response.
     *
     * @param context The application context.
     */
    fun makeMultiTurnQuery(context: Context, prompt: String, mediaList: List<MediaModel>? = null) {
        _uiState.value.messages.add(Message(text = prompt, mode = Mode.USER))
        _uiState.value.messages.add(
            Message(
                text = context.getString(R.string.generating),
                mode = Mode.GEMINI,
                isGenerating = true
            )
        )
        viewModelScope.launch {

            if (chat == null) {
                chat = getChat()
            }

            // val msg = "Look at the image(s), and then answer the following question: $prompt"
            var inputContent: Any = prompt
            if (mediaList?.isNotEmpty() == true) {
                inputContent = content {
                    mediaList.forEach {
                        image(it.bitmap)
                    }
                    text(prompt)
                }
            }
            makeGeneralQuery(_uiState.value.messages, inputContent)
        }
    }

    /**
     * Clears the current chat context and deletes all messages.
     *
     * This function resets the chat history by clearing the chat object and deleting all messages
     * from both the local database and the UI state. It also clears any cached data.
     */
    fun clearContext() {
        Timber.d("ViewModel Loader")
        chat = getChat()
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMessages()
            _uiState.value = MessageUiState()
            clearCacheDir()
        }
    }

    /**
     * Validates the provided API key by making a test request to the Gemini AI model.
     *
     * This function attempts to initialize the Gemini AI model with the given API key and
     * sends a simple request. If the request is successful, the key is considered valid and
     * stored; otherwise, it's considered invalid.
     *
     */
    fun validate(context: Context, apiKey: String) {
        viewModelScope.launch {
            _validationState.value = ValidationState.Checking
            try {
                val model = geminiAIRepo.getGenerativeModel(apiKey = apiKey)
                val res = model.generateContent("Hi")
                if (res.text?.isNotEmpty() == true) {
                    _validationState.value = ValidationState.Valid
                    context.datastore.storeApiKey(apiKey)
                } else _validationState.value = ValidationState.Invalid
            } catch (e: Exception) {
                _validationState.value = ValidationState.Invalid
            }
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
     * Makes a general query to the Gemini AI model and handles the response.
     *
     * This function sends either a text prompt or a content object (including text and images)
     * to the AI model. It streams the response, updates the UI state with each chunk, and
     * finally updates the local database with the full response.
     */
    @SuppressLint("LogNotTimber")
    private suspend fun makeGeneralQuery(
        result: MutableList<Message>,
        feed: Any
    ) {
        var output = ""
        try {
            val stream = when {
                feed is String -> chat?.sendMessageStream(feed)
                else -> model.generateContentStream(feed as Content)
            }
            stream?.collect { chunk ->
                output += chunk.text.toString()
                output.trimStart()
                result[result.lastIndex] =
                    Message(text = output, mode = Mode.GEMINI, isGenerating = true)
            }
            Log.v("GemLens", output)
            result[result.lastIndex] =
                Message(text = output, mode = Mode.GEMINI, isGenerating = false)

            //Updating into DB
            updateToDb(feed, output)
        } catch (e: Exception) {
            result[result.lastIndex] = Message(
                text = e.message.toString(),
                mode = Mode.ERROR,
            )
        }
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
     * Updates the local database with the new message exchange.
     *
     * This function extracts text and image URIs from the input feed and creates a new
     * message entry in the database. It also adds a separate entry for the AI's response.
     * This ensures that the entire conversation history is stored locally for persistence.
     *
     * @param feed The input content sent to the AI model.
     * @param output The text response received from the AI model.
     */
    private fun updateToDb(feed: Any, output: String) {
        viewModelScope.launch {
            var text = ""
            val imageUris = mutableListOf<String>()
            if (feed is Content) {
                feed.parts.filterIsInstance<TextPart>().forEach {
                    text += it.text
                }
                feed.parts.filterIsInstance<ImagePart>().forEach {
                    imageUris.add(it.image.getLocalImageUri())
                }
            } else {
                text = feed.toString()
            }
            //Updating into DB
            repository.insertMessage(Message(text = text, imageUris = imageUris))
            repository.insertMessage(Message(text = output, mode = Mode.GEMINI))
        }
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

    sealed class ValidationState {
        data object Idle : ValidationState()
        data object Checking : ValidationState()
        data object Valid : ValidationState()
        data object Invalid : ValidationState()
    }
}