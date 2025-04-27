package com.codentmind.gemlens.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.codentmind.gemlens.R
import com.codentmind.gemlens.domain.model.MediaModel
import com.codentmind.gemlens.presentation.components.CameraPermissionSettingsDialog
import com.codentmind.gemlens.presentation.components.ConversationArea
import com.codentmind.gemlens.presentation.components.SelectedImageArea
import com.codentmind.gemlens.presentation.components.ShowCustomAlert
import com.codentmind.gemlens.presentation.components.TypingArea
import com.codentmind.gemlens.presentation.navigation.DrawerNav
import com.codentmind.gemlens.presentation.navigation.MainTopBar
import com.codentmind.gemlens.presentation.navigation.items
import com.codentmind.gemlens.presentation.viewmodel.MessageViewModel
import com.codentmind.gemlens.utils.AnalyticsHelper.logScreenView
import com.codentmind.gemlens.utils.Constant.Analytics.Companion.SCREEN_CHAT
import com.codentmind.gemlens.utils.getTempCameraFile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatScreen(viewModel: MessageViewModel, navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    var clearDataDialog by rememberSaveable { mutableStateOf(false) }
    var cameraPermissionDialog by rememberSaveable { mutableStateOf(false) }

    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
    viewModel.makeHomeVisit()

    selectedItemIndex = items.indexOfFirst { it.title == currentRoute }

    val mediaList: SnapshotStateList<MediaModel> = remember { mutableStateListOf() }
    val context = LocalContext.current
    val photoFile = remember { getTempCameraFile(context) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(key1 = true) {
        logScreenView(SCREEN_CHAT)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            mediaList.add(MediaModel(cameraImageUri!!))
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraImageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            ).also {
                cameraLauncher.launch(it)
            }
        } else {
            cameraPermissionDialog = true
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
    ) {
        it.forEach { uri ->
            mediaList.add(MediaModel(uri))
        }
    }

    ShowCustomAlert(
        showDialog = clearDataDialog,
        titleText = stringResource(R.string.delete_chat),
        message = stringResource(R.string.confirm_delete),
        onConfirm = {
            viewModel.uiState.value.isClearData = true
            clearDataDialog = false
        },
        onDismiss = {
            clearDataDialog = false
        }
    )

    ModalNavigationDrawer(
        drawerContent = {
            DrawerNav(
                selectedItemIndex = selectedItemIndex,
                onItemSelect = { selectedItemIndex = it },
                onCloseDrawer = { scope.launch { drawerState.close() } },
                navController = navController
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                MainTopBar(scope, drawerState) {
                    clearDataDialog = true
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
                    .fillMaxHeight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    ConversationArea(viewModel)
                }
                SelectedImageArea(mediaList = mediaList)
                TypingArea(
                    viewModel = viewModel,
                    mediaList = mediaList,
                    galleryLauncher = galleryLauncher,
                    permissionLauncher = permissionLauncher
                )
            }
        }
    }

    CameraPermissionSettingsDialog(
        showDialog = cameraPermissionDialog,
        onDismiss = { cameraPermissionDialog = false }
    )
}
