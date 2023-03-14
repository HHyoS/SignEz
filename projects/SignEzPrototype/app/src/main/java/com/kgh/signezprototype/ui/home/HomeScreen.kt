package com.kgh.signezprototype.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgh.signezprototype.fields.EditNumberField
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
    ) {
    val focusManager = LocalFocusManager.current
    val sWidth = remember { mutableStateOf("") } // 사이니지
    val sHeight = remember { mutableStateOf("") } // 사이니지

    val dWidth = remember { mutableStateOf("") } // 디스플레이
    val dHeight = remember { mutableStateOf("") } // 디스플레이

    val step = remember { mutableStateOf(1) } //진행단계

    SignEzPrototypeTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                if (step.value == 1) {
                    Text(
                        text = "분석 데이터 선택",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "사이니지 정보",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0c4da2)
                    )
                    Text(text = "사이니지 사이즈 (mm)")
                    EditNumberField(
                        // 가로 길이
                        head = "W : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = sWidth.value,
                        onValueChange = { sWidth.value = it },
                    )
                    EditNumberField(
                        // 세로 길이
                        head = "H : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done

                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = sHeight.value,
                        onValueChange = { sHeight.value = it },
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Text(text = "디스플레이 사이즈 (mm)")
                    EditNumberField(
                        // 가로 길이
                        head = "W : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = dWidth.value,
                        onValueChange = { dWidth.value = it },
                    )
                    EditNumberField(
                        // 세로 길이
                        head = "H : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = dHeight.value,
                        onValueChange = { dHeight.value = it },
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Row {
                        OutlinedButton(
                            onClick = navigateToVideo,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text("영상 분석")
                        }

                        OutlinedButton(
                            onClick = navigateToPicture,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text("사진 분석")
                        }
                    }

                }

            } // 바닥 버튼 조건문

//            SelectBox() , 모델 고르기 잠시 중단.
        } // 가장 바같 박스 닫는 괄호
    }
}
// 일단 찍기, 불러오기 uri 따로 분리했는데 합쳐도 될듯.