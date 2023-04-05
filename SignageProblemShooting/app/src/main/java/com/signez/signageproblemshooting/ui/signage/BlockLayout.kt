package com.signez.signageproblemshooting.ui.signage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object BlockLayoutDestination : NavigationDestination {
    override val route = "BlockLayout"
    override val titleRes = "BlockLayout"
}

@Composable
fun LayoutScreen(
    signageId:String,
    cabinetId:String,
    sViewModel:SignageDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavController,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val viewModel = viewModel<BlockViewModel>()
    val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
        value = sViewModel.getCabinetById(cabinetId.toLong())
    })
    val cabinet = cabinetState.value
    val signageState = produceState(initialValue = null as Signage?, producer = {
        value = sViewModel.getSignage(signageId.toLong())
    })
    val signage = signageState.value

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "캐비닛 배치 변경",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomDoubleFlatButton(
                leftTitle = "취소",
                rightTitle = "저장",
                isLeftUsable = true,
                isRightUsable = viewModel.finish.value ,
                leftOnClickEvent = { navController.popBackStack() },
                rightOnClickEvent = {
                    navController.popBackStack()
                    // 추후 기능 확장시 추가.
                }
            )
        }
    ) { innerPadding ->
        Spacer(modifier = Modifier.padding(innerPadding))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BlocksArea(viewModel)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    if (signage != null) {
                        Text(text = "배치 크기 제한 : ${signage.width} x ${signage.height}")
                    }
                    if (cabinet != null) {
                        Text(text = "캐비닛 사이즈 : ${cabinet.cabinetWidth} x ${cabinet.cabinetHeight}")
                    }
                }
                Button(onClick = { viewModel.clear()}) {
                    Text("Clear")
                }
            }


            // cabinet type 여러개 받는 식으로 개량되면 반복문으로 버튼 생성. 및 버튼 색 차별화
            if (cabinet != null && signage != null) {
                AddBlockButton(
                    viewModel = viewModel,
                    cabinet = cabinet,
                    widthLimit = signage.width.toInt(),
                    heightLimit = signage.height.toInt()
                )
            }


        }
    }


}

@Composable
fun DraggableBlock(
    block: Block,
    viewModel:BlockViewModel,
    viewRatio:Int = 15,
    onDragEnd: (Block) -> Unit,
) {
    val density = LocalDensity.current
    val offsetX = remember { mutableStateOf(with(density) { block.x.toDp() / viewRatio }) }
    val offsetY = remember { mutableStateOf(with(density) { block.y.toDp() / viewRatio }) }
    val width = with(density) { block.width.toDp() / viewRatio }
    val height = with(density) { block.height.toDp() / viewRatio }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    offsetX.value.roundToPx(),
                    offsetY.value.roundToPx()
                )
            }
            .size(width, height)
            .background(Color.Blue)
            .border(width = 1.dp, color = Color.Black)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
//                        offsetX.value += dragAmount.x.toDp()
//                        offsetY.value += dragAmount.y.toDp()
                    },
                    onDragEnd = {
//                        onDragEnd(block.copy(x = block.x + offsetX.value.roundToInt(), y = block.y + offsetY.value.roundToInt()))
                    }
                )
            }
    )
}

// 블록 설치 영역
@Composable
fun BlocksArea(viewModel: BlockViewModel) {
    val blocks = viewModel.blocks
    Box(modifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .height(600.dp)) {
        blocks.forEach { block ->
            DraggableBlock(block=block,viewModel=viewModel) { updatedBlock ->
                viewModel.updateBlock(updatedBlock)
            }
        }
    }
}

// 블록 추가
@Composable
fun AddBlockButton(
    viewModel: BlockViewModel,
    cabinet:Cabinet,
    widthLimit:Int = 9000,
    heightLimit:Int = 12000,
) {
    val width = cabinet.cabinetWidth.toInt()
    val height = cabinet.cabinetHeight.toInt()
    val colModuleCount = cabinet.moduleColCount
    val rowModuleCount = cabinet.moduleRowCount

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Button(onClick = {
            viewModel.addBlock(
                width = width,
                height = height,
                widthLimit = widthLimit,
                heightLimit = heightLimit,
                colModuleCount = colModuleCount,
                rowModuleCount = rowModuleCount
            )
        }) {
            Text("+ ${cabinet.name}")
        }

        Button(onClick = {
            viewModel.cancel()
        }) {
            Text("Cancel")
        }

        Button(onClick = {
            while (
                viewModel.addBlock(
                    width = width,
                    height = height,
                    widthLimit = widthLimit,
                    heightLimit = heightLimit,
                    colModuleCount = colModuleCount,
                    rowModuleCount = rowModuleCount
                )) {}
        }) {
            Text("Auto")
        }
    }

}