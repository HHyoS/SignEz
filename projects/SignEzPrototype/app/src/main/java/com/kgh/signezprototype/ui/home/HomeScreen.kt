package com.kgh.signezprototype.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.ui.components.SignEzFloatingButton
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme
import com.kgh.signezprototype.ui.navigation.NavigationDestination


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = "SignEz"
}

@Composable
fun HomeScreen(
    navigateToPicture: () -> Unit,
    navigateToVideo: () -> Unit,
    navigateToSignageList: () -> Unit,
    ) {
    val focusManager = LocalFocusManager.current
    val sWidth = remember { mutableStateOf("") } // 사이니지
    val sHeight = remember { mutableStateOf("") } // 사이니지

    val dWidth = remember { mutableStateOf("") } // 디스플레이
    val dHeight = remember { mutableStateOf("") } // 디스플레이

    androidx.compose.material.Scaffold(
        topBar = {
            SignEzTopAppBar(
                title = "SignEz",
                canNavigateBack = false
            )
        },
        // 플로팅 버튼 예시
//        floatingActionButton = {
//            SignEzFloatingButton(
//                onClickEvent = {}
//            )
//        }
    ){ innerPadding -> // default Scaffold 내부 다른 구조와 겹치지 않는 적절한 값.
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
//            .background(androidx.compose.material.MaterialTheme.colors.onSurface)
        ) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxHeight(),
//                    .background(androidx.compose.material.MaterialTheme.colors.primary),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Spacer(modifier = Modifier.padding(5.dp))
                PastResult(modifier = Modifier)
                Spacer(modifier = Modifier.padding(8.dp))
                SignEzSpec(modifier = Modifier,navigateToSignageList)
//                Spacer(modifier = Modifier.padding(8.dp))
                CabinetSpec(modifier = Modifier)
                Spacer(modifier = Modifier.padding(110.dp))
                VideoAnalysisBtn(navigateToVideo)
                PictureAnalysisBtn(navigateToPicture)
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