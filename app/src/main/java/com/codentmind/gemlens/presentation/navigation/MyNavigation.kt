package com.codentmind.gemlens.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codentmind.gemlens.presentation.screens.AboutScreen
import com.codentmind.gemlens.presentation.screens.ChatScreen
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel


@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun MyNavigation(
    viewModel: MessageViewModel,
    startDestination: String = Home.route
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    )
    {
        composable(Home.route) {
            ChatScreen(viewModel, navController)
        }
        composable(About.route) {
            AboutScreen(navController)
        }
//        composable(Settings.route) {
//            SettingsScreen(navController)
//        }
//        composable(SetApi.route) {
//            SetApiScreen(viewModel, navController)
//        }
    }
}