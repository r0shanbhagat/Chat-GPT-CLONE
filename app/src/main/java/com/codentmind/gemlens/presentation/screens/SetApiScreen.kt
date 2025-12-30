package com.codentmind.gemlens.presentation.screens

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codentmind.gemlens.R
import com.codentmind.gemlens.data.dataSource.remote.RemoteConfigHelper
import com.codentmind.gemlens.presentation.navigation.Home
import com.codentmind.gemlens.presentation.navigation.SetApi
import com.codentmind.gemlens.presentation.navigation.TopBar
import com.codentmind.gemlens.presentation.theme.DecentBlue
import com.codentmind.gemlens.presentation.theme.DecentGreen
import com.codentmind.gemlens.presentation.theme.DecentRed
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import com.codentmind.gemlens.utils.AnalyticsHelper.logButtonClick
import com.codentmind.gemlens.utils.AnalyticsHelper.logScreenView
import com.codentmind.gemlens.utils.Constant.Analytics.Companion.SCREEN_API
import com.codentmind.gemlens.utils.Constant.CONFIG_API_KEY
import com.codentmind.gemlens.utils.datastore
import com.codentmind.gemlens.utils.getApiKey
import com.codentmind.gemlens.utils.isValidString
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetApiScreen(
    viewModel: MessageViewModel,
    navController: NavHostController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val validationState by viewModel.validationState.collectAsState()
    val context = LocalContext.current

    var text by remember { mutableStateOf(TextFieldValue(runBlocking { context.datastore.getApiKey() })) }
    LaunchedEffect(Unit) {
        logScreenView(SCREEN_API)
    }

    Scaffold(
        topBar = {
            TopBar(
                name = stringResource(id = R.string.set_api),
                navController = navController,
                showNavigationIcon = viewModel.isHomeVisit.value
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                singleLine = true,
                value = text,
                onValueChange = { newText ->
                    text = newText
                    viewModel.resetValidationState()
                },
                placeholder = {
                    Text(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        text = "Enter your API Key"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                shape = RoundedCornerShape(28),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.LightGray,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W500,
                    fontSize = 18.sp
                )
            )

            Button(
                enabled = text.text.isValidString(),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    if (validationState == MessageViewModel.ValidationState.Valid) {
                        if (viewModel.isHomeVisit.value == true) {
                            navController.navigateUp()
                        } else {
                            navController.popBackStack(SetApi.route, true)
                            navController.navigate(Home.route)
                        }
                    } else if (validationState == MessageViewModel.ValidationState.Idle) {
                        viewModel.validate(context, text.text)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (validationState) {
                        MessageViewModel.ValidationState.Checking -> Color.DarkGray
                        MessageViewModel.ValidationState.Idle -> DecentBlue
                        MessageViewModel.ValidationState.Invalid -> DecentRed
                        MessageViewModel.ValidationState.Valid -> DecentGreen
                    },
                    contentColor = Color.White
                )
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = when (validationState) {
                        MessageViewModel.ValidationState.Checking -> "Validating..."
                        MessageViewModel.ValidationState.Idle -> "Validate"
                        MessageViewModel.ValidationState.Invalid -> "Invalid Key"
                        MessageViewModel.ValidationState.Valid -> "Continue"
                    },
                    fontSize = 15.sp
                )
            }

            TempApiKeyGenerator {
                text = TextFieldValue(it)
                viewModel.validate(context, it)
            }

            Spacer(modifier = Modifier.padding(25.dp))
            ApiSetupHelper()

        }
    }
}


@Composable
fun TempApiKeyGenerator(onComplete: (String) -> Unit) {
    val remoteConfig = koinInject<RemoteConfigHelper>()
    val isLoading = remember { mutableStateOf(false) }
    val context = LocalActivity.current
    val linkedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("Don't have an API key? ")
        }
        pushStringAnnotation(
            tag = "click",
            annotation = "Generate Temporary Key."
        )
        withStyle(
            style = SpanStyle(
                color = Color(0xFF267BC4),
                textDecoration = TextDecoration.None
            )
        ) {
            append("Generate Temporary Key.")
        }
    }
    val loaderString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("Don't have an API key? ")
        }
    }
    val annotatedString = remember { mutableStateOf(linkedString) }

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isLoading.value) Arrangement.Start else Arrangement.SpaceEvenly
    ) {
        ClickableText(
            modifier = Modifier.padding(10.dp),
            text = annotatedString.value,
            style = MaterialTheme.typography.labelMedium,
            onClick = { offset ->
                annotatedString.value.getStringAnnotations(
                    tag = "click",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    isLoading.value = true
                    logButtonClick("Generate Temp Key")
                    remoteConfig.fetchConfigData(CONFIG_API_KEY) {
                        isLoading.value = false
                        annotatedString.value = linkedString
                        onComplete(it)
                    }
                }
            }
        )

        if (isLoading.value) {
            annotatedString.value = loaderString
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = Color.Black,
                modifier = Modifier.size(20.dp),
            )
        }

    }
}

@Composable
fun ApiSetupHelper() {
    val uriHandler = LocalUriHandler.current

    val apiSetup = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("Learn how to set up your own API key. ")
        }
        pushStringAnnotation(
            tag = "click",
            annotation = stringResource(id = R.string.api_setup_link)
        )
        withStyle(
            style = SpanStyle(
                color = Color(0xFF267BC4),
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Click here")
        }
        pop()
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append(" for details.")
        }
    }

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            modifier = Modifier.size(50.dp),
            painter = painterResource(id = R.drawable.about_icon),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "help"
        )
        ClickableText(
            modifier = Modifier.padding(10.dp),
            text = apiSetup,
            style = MaterialTheme.typography.titleMedium,
            onClick = { offset ->
                apiSetup.getStringAnnotations(
                    tag = "click",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    logButtonClick("Learn More About API")
                    uriHandler.openUri(it.item)
                }
            }
        )
    }
}

