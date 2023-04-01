package com.signez.signageproblemshooting.ui.analysis

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.components.BottomSingleFlatButton
import com.signez.signageproblemshooting.ui.components.ErrorModuleHeatMap
import com.signez.signageproblemshooting.ui.components.InFocusBlockButton
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.noRippleClickable

object ResultGridDestination : NavigationDestination {
    override val route = "ResultGridScreen"
    override val titleRes = "ResultGrid"
}

@Composable
fun ResultGridView(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    resultId: Long,
) {
    Log.d("ResultGrid", "resultId : ${resultId.toString()}")
    val focusManager = LocalFocusManager.current
    val modulesState = produceState(initialValue = null as List<ErrorModule>?, producer = {
        value = viewModel.getRelatedModule(resultId)
    })
    val modules = modulesState.value
    val signageState = produceState(initialValue = null as Signage?, producer = {
        value = viewModel.getSignageByResultId(resultId)
    })
    val signage = signageState.value
    val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
        value = viewModel.getCabinetByResultId(resultId)
    })
    val cabinet = cabinetState.value


    var threshold by remember { mutableStateOf(70) }
    var errorModuleFilteredList by remember {
        mutableStateOf(
            listOf(
                ErrorModule(
                    resultId = 0,
                    score = 20.0,
                    x = 1,
                    y = 1
                )
            )
        )
    }

    Log.d("사이니지 넘어오냐", "ResultGridView: ${signage}")
    Log.d("모듈 넘어오냐", "ResultGridView: ${modules}")

    var widthCabinetNumber = signage?.widthCabinetNumber ?: 2
    var heightCabinetNumber = signage?.heightCabinetNumber ?: 2
    var widthModuleNumber = cabinet?.moduleColCount ?: 2
    var heightModuleNumber = cabinet?.moduleRowCount ?: 2


    errorModuleFilteredList = modules?.filter {
        (it.score * 100) >= threshold
    } ?: listOf(
        ErrorModule(resultId = 0, score = 20.0, x = 1, y = 1),
    )

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "전체 도식화 보기",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomSingleFlatButton(title = "사진보기", isUsable = true) {
//                navController.navigate(ErrorImageDestination.route)
                // moduleClickEvent 활용해주세요~
            }
        }
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Column(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.89F),
                contentAlignment = Alignment.TopCenter
            ) {
                Column {
                    var parentWidth by remember { mutableStateOf(0) }
                    var parentHeight by remember { mutableStateOf(0) }

                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .weight(0.7f)
                            .onSizeChanged {
                                parentWidth = it.width
                                parentHeight = it.height
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Log.d(
                                "Width",
                                "ResultGridView: ${with(LocalDensity.current) { parentWidth.toDp() }}"
                            )
                            Log.d(
                                "Height",
                                "ResultGridView: ${with(LocalDensity.current) { parentHeight.toDp() }}"
                            )

                            var moduleSize = with(LocalDensity.current) {
                                kotlin.math.min(
                                    (parentWidth / ((widthCabinetNumber * widthModuleNumber) + (2 * widthModuleNumber))),
                                    (parentHeight / ((heightCabinetNumber * heightModuleNumber) + (2 * heightModuleNumber)))
                                ).toDp()
                            }

                            Log.d(
                                "moduleSize",
                                "ResultGridView: ${moduleSize}}"
                            )
                            if (signage != null && cabinet != null) {
                                ErrorModuleHeatMap(
                                    widthCabinetNumber = widthCabinetNumber,
                                    heightCabinetNumber = heightCabinetNumber,
                                    moduleRowCount = cabinet.moduleRowCount,
                                    moduleColCount = cabinet.moduleColCount,
                                    errorModuleList = errorModuleFilteredList,
                                    moduleSize = moduleSize,
                                    cabinetHeigth = (moduleSize * heightModuleNumber),
                                    cabinetWidth = (moduleSize * widthModuleNumber),
                                    viewModel = viewModel,
                                    navController = navController,
                                )
                            }
                        }
                    }

                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .weight(0.3f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Column(
                            Modifier
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Threshold 조정",
                                    style = MaterialTheme.typography.h3,
                                    color = MaterialTheme.colors.onSurface,
                                    modifier = Modifier
                                        .padding(start = 24.dp, top = 8.dp)
                                        .weight(0.6f)
                                )
                                Spacer(
                                    modifier = Modifier.weight(0.1f)
                                )
                                Column(
                                    modifier = Modifier.weight(0.3f)
                                ) {
                                    InFocusBlockButton(
                                        title = "버튼없어요",
                                        isVisible = false,
                                        onClickEvent = {}
                                    )
                                }
                            }

                            Text(
                                text = "목표 정확도 ${threshold}%",
                                style = MaterialTheme.typography.h4,
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.padding(
                                    start = 24.dp,
                                    bottom = 8.dp
                                )
                            )

                            Slider(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                                value = threshold.toFloat(),
                                onValueChange = { threshold = it.toInt() },
                                valueRange = 20f..100f,
                                onValueChangeFinished = {
                                    // launch some business logic update with the state you hold
                                    // viewModel.updateSelectedSliderValue(sliderPosition)
                                },
                                steps = 79,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colors.primary,
                                    disabledThumbColor = MaterialTheme.colors.primary,
                                    activeTrackColor = MaterialTheme.colors.primary,
                                    inactiveTrackColor = Color(0xFFABD5F8),
                                    activeTickColor = MaterialTheme.colors.primary,
                                    inactiveTickColor = Color(0xFFABD5F8),
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "20%",
                                    style = MaterialTheme.typography.h4,
                                    color = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        bottom = 8.dp
                                    )
                                )
                                Text(
                                    text = "100%",
                                    style = MaterialTheme.typography.h4,
                                    color = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.padding(
                                        end = 24.dp,
                                        bottom = 8.dp
                                    )
                                )
                            }

                        }

                        Spacer(modifier = Modifier.height(15.dp))
                    }


                }// 컬럼 끝

            } // 박스 끝
        }
    }
}

/*
x, y 좌표 고르면 해당 위치에 발생된
모든 에러 모듈 이미지 슬라이드로 navigate됨.
해당 페이지에서 사진을 보고 해당 모듈 삭제 가능.
-> 모듈에 달아 주세요~
 */
fun moduleClickEvent(
    x: Int,
    y: Int,
    resultId: Long,
    viewModel: AnalysisViewModel,
    navController: NavController
) {
    Log.d("moduleClickEvent", "${x}, ${y}, ${resultId}")
    viewModel.selectedModuleX.value = x
    viewModel.selectedModuleY.value = y
    navController.navigate(ErrorImageDestination.route + "/${x}/${y}/${resultId}")
}
