package com.codentmind.gemlens.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codentmind.gemlens.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun MainTopBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
    refresh: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(80.dp),
                    painter = painterResource(id = R.drawable.toolbar_ai),
                    contentDescription = "logo"
                )
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = "AI",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                )

            }
        },
        actions = {
            IconButton(onClick = {
                refresh.invoke()
                scope.launch {
                    drawerState.close()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.refresh),
                    contentDescription = "Refresh",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_menu_24),
                    contentDescription = "Menu",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}