package com.codentmind.gemlens.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.codentmind.gemlens.presentation.navigation.Home
import com.codentmind.gemlens.presentation.navigation.MyNavigation
import com.codentmind.gemlens.presentation.navigation.SetApi
import com.codentmind.gemlens.presentation.theme.GemiTheme
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import com.codentmind.gemlens.utils.datastore
import com.codentmind.gemlens.utils.getApiKey
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private val viewModel: MessageViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GemiTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (runBlocking {
                            applicationContext.datastore.getApiKey().isEmpty()
                        }) {
                        MyNavigation(viewModel = viewModel, startDestination = SetApi.route)
                    } else {
                        MyNavigation(viewModel = viewModel, startDestination = Home.route)
                    }
                }
            }
        }
    }
}


