package com.signez.signageproblemshooting.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.analysis.moduleClickEvent
import com.signez.signageproblemshooting.ui.theme.SignEzTheme
import kotlin.math.roundToInt

@Composable
fun ErrorModuleHeatMap(
    heightCabinetNumber: Int = 10,
    widthCabinetNumber: Int = 10,
    moduleRowCount: Int,
    moduleColCount: Int,
    errorModuleList: List<ErrorModule>,
    moduleSize: Dp,
    cabinetWidth: Dp,
    cabinetHeigth: Dp,
    viewModel: AnalysisViewModel,
    threshold: Int = 70,
) {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
//    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        if (scale <= 1f) scale = 1f
//        rotation += rotationChange
        offset += offsetChange
    }

    val cabinetRowCount = heightCabinetNumber
    val cabinetColCount = widthCabinetNumber

    val cabinetMatrix =
        Array(cabinetRowCount + 1) {
            IntArray(cabinetColCount + 1)
        }


//    Array<ErrorModule>
//    for (errormoudle in errorModuleList) {
//        val crow = (errormoudle.x / moduleRowCount) + 1
//        val ccol = (errormoudle.y / moduleColCount) + 1
//    }

    cabinetMatrix[1][1] = 1

    Box(
        modifier = Modifier
            // apply other transformations like rotation and zoom
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
//                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
//         add transformable to listen to multitouch transformation events
//         after offset
            .transformable(state = state)
            .border(width = moduleSize / 2, color = Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(all = moduleSize / 2)
        ) {
            for (cabinetR in 1..cabinetRowCount) {
                Row() {
                    for (cabinetC in 1..cabinetColCount) {
                        Box(
                            modifier = Modifier
                                .border(width = moduleSize / 8, color = Color(0xFF555657))
                        ) {
                            Column(
                                Modifier
                                    .width(cabinetWidth + (moduleSize / 4))
                                    .height(cabinetHeigth + (moduleSize / 4))
                                    .padding(all = moduleSize / 8),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val errorCount = errorModuleList.count { errorModule ->
                                    (errorModule.x - 1) / moduleColCount + 1 == cabinetC &&
                                            (errorModule.y - 1) / moduleRowCount + 1 == cabinetR
                                }

                                if (errorCount == 0) {
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxSize()
                                    ) {
                                        //draw shapes here
                                        drawRoundRect(
                                            color = Color(0xFFECECEC)
                                        )
                                    }
                                } else {
                                    var signageMatrix =
                                        Array(moduleRowCount + 1) { IntArray(moduleColCount + 1) }
                                    //
                                    for (moduleR in 1..moduleRowCount) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
//                                        modifier = Modifier.padding(vertical = 1.dp)
                                        ) {
                                            for (moduleC in 1..moduleColCount) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(5))
                                                ) {
                                                    Column(
                                                        Modifier
                                                            .size(moduleSize)
                                                    ) {
                                                        val errorModules =
                                                            errorModuleList.filter { errorModule ->
                                                                (errorModule.x - 1) / moduleColCount + 1 == cabinetC &&
                                                                        (errorModule.y - 1) / moduleRowCount + 1 == cabinetR &&
                                                                        (errorModule.x - 1) % moduleColCount + 1 == moduleC &&
                                                                        (errorModule.y - 1) % moduleRowCount + 1 == moduleR
                                                            }.sortedByDescending {
                                                                it.score
                                                            }

                                                        Canvas(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .clickable(
                                                                    enabled = errorCount >= 1
                                                                ) {
                                                                    if (errorModules.isNotEmpty()) {
                                                                        viewModel.isModuleClicked.value =
                                                                            true
                                                                        moduleClickEvent(
                                                                            cabinetX = (errorModules[0].x - 1) / moduleColCount + 1,
                                                                            cabinetY = (errorModules[0].y - 1) / moduleRowCount + 1,
                                                                            moduleX = (errorModules[0].x - 1) % moduleColCount + 1,
                                                                            moduleY = (errorModules[0].y - 1) % moduleRowCount + 1,
                                                                            x = (errorModules[0].x),
                                                                            y = (errorModules[0].y),
                                                                            selectedModuleXforEvent = (errorModules[0].x),
                                                                            selectedModuleYforEvent = (errorModules[0].y),
                                                                            viewModel = viewModel,
                                                                            threshold = threshold
                                                                        )
                                                                    }
                                                                }
//
                                                        ) {
                                                            //draw shapes here
                                                            if (errorModules.isNotEmpty()) {
                                                                val maxErrorScore =
                                                                    (errorModules.get(0).score * 100).roundToInt()
                                                                drawRoundRect(
                                                                    color =
                                                                    when (maxErrorScore) {
                                                                        in 0..19 -> Color(0xFFECECEC)
                                                                        in 20..30 -> Color(
                                                                            0xFFFFB5B5
                                                                        )
                                                                        in 31..50 -> Color(
                                                                            0xFFFF8080
                                                                        )
                                                                        in 51..70 -> Color(
                                                                            0xFFFF5B5B
                                                                        )
                                                                        in 70..90 -> Color(
                                                                            0xFFFF1B1B
                                                                        )
                                                                        else -> Color(0xFFFF0000)
                                                                    },
                                                                )
                                                                if (viewModel.selectedModuleXforEvent.value == errorModules[0].x
                                                                    && viewModel.selectedModuleYforEvent.value == errorModules[0].y
                                                                ) {
                                                                    drawRoundRect(
                                                                        color = Color(0x4D000000),
                                                                    )
                                                                }
                                                            } else {
                                                                drawRoundRect(
                                                                    color =
                                                                    Color(0xFFECECEC),
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    //
                                }
                            } // Col
                        } // for cabinet C
                    }
                } // Row
            } // for cabinet R
        } // Col

    }

//    signageMatrix[0][0][0][0] = 3
//    val ints = signageMatrix[0][0][0][0]
//    Text(text = ints.toString())

}

@Preview
@Composable
fun HeatMapPreview() {
    SignEzTheme(darkTheme = false) {
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

        // 히트맵에 보내주는 에러모듈 리스트를 스레스홀드 기준으로
//        threshold = 70
        Column() {

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.9f)
            ) {
                errorModuleFilteredList = listOf(
                    ErrorModule(resultId = 1, score = 75.1, x = 4, y = 1),
                    ErrorModule(resultId = 2, score = 45.0, x = 1, y = 1),
                    ErrorModule(resultId = 3, score = 89.0, x = 4, y = 4),
                    ErrorModule(resultId = 4, score = 25.0, x = 9, y = 9),
                    ErrorModule(resultId = 5, score = 30.0, x = 9, y = 9),
                    ErrorModule(resultId = 6, score = 35.0, x = 9, y = 9),
                    ErrorModule(resultId = 7, score = 40.0, x = 9, y = 9),
                    ErrorModule(resultId = 8, score = 45.0, x = 9, y = 9),
                    ErrorModule(resultId = 9, score = 50.0, x = 9, y = 9),
                    ErrorModule(resultId = 10, score = 55.0, x = 9, y = 9),
                    ErrorModule(resultId = 11, score = 60.0, x = 9, y = 9),
                    ErrorModule(resultId = 12, score = 70.0, x = 9, y = 9),
                    ErrorModule(resultId = 13, score = 75.0, x = 9, y = 9),
                    ErrorModule(resultId = 14, score = 80.0, x = 9, y = 9),
//                ErrorModule(resultId = 15, score = 73.0, x = 30, y = 31),
                ).filter {
                    it.score >= threshold
                }

//                ErrorModuleHeatMap(
//                    widthCabinetNumber = 11,
//                    heightCabinetNumber = 19,
//                    moduleRowCount = 4,
//                    moduleColCount = 4,
//                    errorModuleList = errorModuleFilteredList,
//                    moduleSize = 20.dp,
//                    cabinetHeigth = 10.dp,
//                    cabinetWidth = 10.dp,
//                )
            }

            Text(text = threshold.toString())
            Slider(
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

        }

    }
}