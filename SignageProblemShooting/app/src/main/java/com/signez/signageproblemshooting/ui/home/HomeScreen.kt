package com.signez.signageproblemshooting.ui.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
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
    mainViewModel:MainViewModel
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
        value = viewModel.getCabinet(1)
    })
    val cabinet = cabinetState.value
    val signageState by viewModel.getSignage().collectAsState()
    val appSettingsResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onAppSettingsClosed()
        checkAndRequestPermissions(context,mainViewModel)
    }

    androidx.compose.material.Scaffold(
        topBar = {
            SignEzTopAppBar(
                title = "SignEz",
                canNavigateBack = false
            )
        },
        // 하단 양쪽 버튼 예시
//        bottomBar = {
//            BottomDoubleFlatButton(
//                leftTitle = "취소",
//                rightTitle = "확인",
//                isLeftUsable = true,
//                isRightUsable = false,
//                leftOnClickEvent = { /*TODO*/ },
//                rightOnClickEvent = { /*TODO*/ }
//            )
//        }
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp),
            ) {
                VideoAnalysisBtn(navigateToVideo)
                PictureAnalysisBtn(navigateToPicture)
            }
        }
        // 플로팅 버튼 예시
//        floatingActionButton = {
//            SignEzFloatingButton(
//                onClickEvent = {}
//            )
//        }
    ) { innerPadding -> // default Scaffold 내부 다른 구조와 겹치지 않는 적절한 값.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if ( !mainViewModel.permissionsGranted.value) {
                Column(
                    modifier = Modifier
                    .align(alignment = Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    PermissionInfo()
                    Button(onClick = {openAppSettings(context, appSettingsResultLauncher)}) {
                        Text(text = "권한 설정")
                    }
                }

            }
            else {
                Column(
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                ) {

                    Spacer(modifier = Modifier.padding(5.dp))
                    PastResult(modifier = Modifier)
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
                            SignEzSpec(modifier = Modifier, navigateToSignageList, signageState.signage)
                            CabinetSpec(modifier = Modifier, cabinet)
                        } else {
                            SignEzSpec(modifier = Modifier, navigateToSignageList, null)
                            CabinetSpec(modifier = Modifier, null)
                        }
                    }
                }
            }
        }
    }
}


//                    Text(
//                        text = "분석 데이터 선택",
//                        modifier = Modifier.padding(16.dp),
//                        fontSize = 40.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = "사이니지 정보",
//                        modifier = Modifier.padding(12.dp),
//                        fontSize = 30.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        color = Color(0xFF0c4da2)
//                    )
//                    Text(text = "사이니지 사이즈 (mm)")
//                    EditNumberField(
//                        // 가로 길이
//                        head = "W : ",
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { focusManager.clearFocus() }
//                        ),
//                        value = sWidth.value,
//                        onValueChange = { sWidth.value = it },
//                    )
//                    EditNumberField(
//                        // 세로 길이
//                        head = "H : ",
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done
//
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { focusManager.clearFocus() }
//                        ),
//                        value = sHeight.value,
//                        onValueChange = { sHeight.value = it },
//                    )
//                    Spacer(modifier = Modifier.padding(10.dp))
//
//                    Text(text = "디스플레이 사이즈 (mm)")
//                    EditNumberField(
//                        // 가로 길이
//                        head = "W : ",
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { focusManager.clearFocus() }
//                        ),
//                        value = dWidth.value,
//                        onValueChange = { dWidth.value = it },
//                    )
//                    EditNumberField(
//                        // 세로 길이
//                        head = "H : ",
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { focusManager.clearFocus() }
//                        ),
//                        value = dHeight.value,
//                        onValueChange = { dHeight.value = it },
//                    )
//                    Spacer(modifier = Modifier.padding(10.dp))
// 일단 찍기, 불러오기 uri 따로 분리했는데 합쳐도 될듯.