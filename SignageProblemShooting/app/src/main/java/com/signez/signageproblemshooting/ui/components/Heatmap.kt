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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.ui.theme.SignEzTheme

@Composable
fun ErrorModuleHeatMap(
//    signageWidth: Double,
//    signageHeight: Double,
//    cabinetWidth: Double,
//    cabinetHeight: Double,
    moduleRowCount: Int,
    moduleColCount: Int,
    heightCabinetNumber: Int,
    widthCabinetNumber: Int,
    errorModuleList: List<ErrorModule>
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


//    val cabinetRowCount = (signageHeight / cabinetHeight).toInt()
//    val cabinetColCount = (signageWidth / cabinetWidth).toInt()
    val cabinetRowCount = heightCabinetNumber
    val cabinetColCount = widthCabinetNumber

    val signageMatrix =
        Array(cabinetRowCount + 1) {
            Array(cabinetColCount + 1) {
                Array(moduleRowCount + 1) { IntArray(moduleColCount + 1) }
            }
        }

//    Array<ErrorModule>
    for (errormoudle in errorModuleList) {
        val crow = (errormoudle.x / moduleRowCount) + 1
        val ccol = (errormoudle.y / moduleColCount) + 1
        var mrow = (errormoudle.x % moduleRowCount)
        var mcol = (errormoudle.y % moduleColCount)
        if (mrow == 0) {
            mrow = moduleRowCount
        }
        if (mcol == 0) {
            mcol = moduleColCount
        }
        signageMatrix[crow][ccol][mrow][mcol] += 1
    }


    Column(
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
            .transformable(state = state),
    ) {
        for (cabinetR in 1..cabinetRowCount) {
            Row() {
                for (cabinetC in 1..cabinetColCount) {
                    Column(
                        Modifier
                            .border(width = 0.5.dp, color = MaterialTheme.colors.onSurface)
                            .padding(0.5.dp)
                    ) {
                        for (moduleR in 1..moduleRowCount) {
                            Row(
                                modifier = Modifier.padding(vertical = 1.dp)
                            ) {
                                for (moduleC in 1..moduleColCount) {
                                    val errorCount = errorModuleList.count { errorModule ->
                                        errorModule.x / moduleRowCount + 1 == cabinetR &&
                                                errorModule.y / moduleColCount + 1 == cabinetC &&
                                                errorModule.x % moduleRowCount + 1 == moduleR &&
                                                errorModule.y % moduleColCount + 1 == moduleC
                                    }
                                    Canvas(
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .width(5.dp)
                                            .height(5.dp)
//                                            .clip(RoundedCornerShape(15))
                                            .clickable(
                                                enabled = errorCount >= 1
                                            ) {

                                            }
                                    ) {
                                        //draw shapes here
                                        drawRoundRect(
                                            color =
                                            when (errorCount) {
                                                0 -> Color(0xFFE3E3E3)
                                                1, 2, 3, 4 -> Color(0xFFFFB5B5)
                                                5, 6, 7, 8, 9 -> Color(0xFFFF6767)
                                                else -> Color(0xFFFF1414)
                                            },
                                            cornerRadius = CornerRadius(5f, 5f)
                                        )
                                    }
//                                    Box(
//                                        modifier = Modifier
//                                            .padding(horizontal = 1.dp)
//                                            .width(15.dp)
//                                            .height(15.dp)
//                                            .clip(RoundedCornerShape(15))
//                                            .clickable(
//                                                enabled = errorCount >= 1
//                                            ) {
//
//                                            }
//                                            .background(
//                                                color =
//                                                when (errorCount) {
//                                                    0 -> MaterialTheme.colors.secondary
//                                                    1,2,3,4 -> Color(0xFFFFB5B5)
//                                                    5,6,7,8,9-> Color(0xFFFF6767)
//                                                    else -> Color(0xFFFF1414)
//                                                }
//
////                                                if (errorCount >= 1)
////                                                    MaterialTheme.colors.primary
////                                                else
////                                                    MaterialTheme.colors.secondary
//                                            )
//                                    )
                                }
//                                for (moduleC in 1..moduleColCount) {
//                                    Column(
//                                        modifier = Modifier.padding(horizontal = 1.dp)
//                                    ) {
//
//                                        Box(
//                                            modifier = Modifier
//                                                .width(5.dp)
//                                                .height(5.dp)
//                                                .clip(RoundedCornerShape(15))
//                                                .clickable { }
//                                                .background(
//                                                    color =
//                                                    if (signageMatrix[cabinetR][cabinetC][moduleR][moduleC] >= 1)
//                                                        MaterialTheme.colors.primary
//                                                    else
//                                                        MaterialTheme.colors.secondary
//                                                )
//                                        )
//                                        {
//                                        }
////                                        Text(text = signageMatrix[cabinetR][cabinetC][moduleR][moduleC].toString())
//                                    } // Col
//                                } // for module C
                            } // Row
                        } // for module R
                    } // Col
                } // for cabinet C
            } // Row
        } // for cabinet R
    } // Col


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
            errorModuleFilteredList = listOf(
                ErrorModule(resultId = 1, score = 75.1, x = 13, y = 14),
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
            ).filter {
                it.score >= threshold
            }

            ErrorModuleHeatMap(
                widthCabinetNumber = 11,
                heightCabinetNumber = 19,
                moduleRowCount = 4,
                moduleColCount = 4,
                errorModuleList = errorModuleFilteredList
            )

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