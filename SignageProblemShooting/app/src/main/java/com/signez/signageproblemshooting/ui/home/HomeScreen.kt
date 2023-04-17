package com.signez.signageproblemshooting.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.inputs.MainViewModel
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = "SignEz"
}

@Composable
fun HomeScreen(
    navigateToPicture: () -> Unit,
    navigateToVideo: () -> Unit,
    navigateToSignageList: () -> Unit,
    viewModel: AnalysisViewModel,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var signage by remember { mutableStateOf(Signage(0, "", 0, 0, 0.0, 0.0, 0L)) }
    var cabinet by remember { mutableStateOf(Cabinet(0, "", 0.0, 0.0, 0, 0)) }
    LaunchedEffect(viewModel.signageId.value) {
        signage = viewModel.getSignageById(viewModel.signageId.value)
        cabinet = viewModel.getCabinet(viewModel.signageId.value)
    }

    androidx.compose.material.Scaffold(
        topBar = {
            SignEzTopAppBar(
                title = "SignEz",
                canNavigateBack = false
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp),
            ) {
                VideoAnalysisBtn(navigateToVideo)
                PictureAnalysisBtn(navigateToPicture)
            }
        }
    ) { innerPadding -> // default Scaffold 내부 다른 구조와 겹치지 않는 적절한 값.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
            ) {

                Spacer(modifier = Modifier.padding(5.dp))
                PastResult(
                    modifier = Modifier,
                    navController = navController,
                    viewModel = viewModel
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    if (viewModel.signageId.value > -1) {
                        SignEzSpec(
                            navigateToSignageList = navigateToSignageList,
                            signage = signage
                        )
                        CabinetSpec(cabinet = cabinet)
                    } else {
                        SignEzSpec(
                            navigateToSignageList = navigateToSignageList,
                            signage = null
                        )
                        CabinetSpec(cabinet = null)
                    }
                }
            }
        }
    }
}