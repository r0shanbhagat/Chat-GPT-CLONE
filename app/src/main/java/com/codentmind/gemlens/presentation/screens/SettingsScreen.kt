package com.codentmind.gemlens.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.codentmind.gemlens.R
import com.codentmind.gemlens.presentation.navigation.TopBar
import com.codentmind.gemlens.utils.AnalyticsHelper.logScreenView
import com.codentmind.gemlens.utils.Constant.Analytics.Companion.SCREEN_SETTINGS


@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(navController: NavHostController) {

    LaunchedEffect(Unit) {
        logScreenView(SCREEN_SETTINGS)
    }

    Scaffold(
        topBar = {
            TopBar(
                name = stringResource(id = R.string.settings),
                navController = navController
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = it.calculateTopPadding() * 2)
        ) {

        }
    }
}