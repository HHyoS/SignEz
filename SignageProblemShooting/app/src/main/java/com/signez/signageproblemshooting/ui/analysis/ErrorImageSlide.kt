package com.signez.signageproblemshooting.ui.analysis

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.ErrorModuleWithImage
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.noRippleClickable
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object ErrorImageDestination : NavigationDestination {
    override val route = "ErrorImageScreen"
    override val titleRes = "ErrorImage"
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ErrorImageView(
    modifier: Modifier = Modifier,
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit,
    x: Int,
    y: Int,
    resultId: Long,
) {
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val selectedIdx = remember {
        mutableStateOf(0)
    }
    val deletionCompleted = remember { mutableStateOf(false) }
    val maisState = remember { mutableStateOf(null as List<ErrorModuleWithImage>?) }
    var cabinetX = viewModel.selectedCabinetX.value
    var cabinetY = viewModel.selectedCabinetY.value
    var moduleX = viewModel.selectedMoudleXInCabinet.value
    var moduleY = viewModel.selectedMoudleYInCabinet.value
    var threshold = viewModel.threshold.value
    LaunchedEffect(deletionCompleted.value) {
        if (deletionCompleted.value) {
            deletionCompleted.value = false
        }
        Log.d(
            "x, y, resultId",
            "${viewModel.selectedModuleX.value}, ${viewModel.selectedModuleY.value}, ${viewModel.selectedResultId.value}"
        )
        Log.d(
            "x, y, resultId",
            "${x}, ${y}, $resultId"
        )
        maisState.value = viewModel.getModulesByXYResultId(
            x = x,
            y = y,
            resultId = resultId
        ).filter {
            (it.errorModule.score * 100).roundToInt() >= threshold
        }.sortedByDescending {
            (it.errorModule.score)
        }

    }
    var mais = maisState.value //
    LaunchedEffect(maisState.value) {
        mais = maisState.value
    }

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "에러 모듈 근거 사진",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Column(
            modifier = modifier
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (mais != null) {
                        if (mais!!.isEmpty()) {
                            Text(
                                text = "텅 비었어요.",
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
                    } // 모듈 눌체크

                    if (mais != null && mais!!.isNotEmpty()) {
                        GlideImage(
                            model = mais!![selectedIdx.value].errorImage?.evidence_image,
                            contentDescription = "글라이드",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.65f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        ErrorImageSlideBox(
                            selectedIdx = selectedIdx,
                            mais = mais!!,
                            cabinetX = cabinetX,
                            cabinetY = cabinetY,
                            moduleX = moduleX,
                            moduleY = moduleY,
                            deleteEvent =
                            {
                                coroutineScope.launch {
                                    viewModel.deleteErrorModule(mais!![selectedIdx.value].errorModule)
                                    deletionCompleted.value = true
                                }
                            }
                        )
                    }

                }

            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun ErrorImageSlideBox(
    selectedIdx: MutableState<Int>,
    mais: List<ErrorModuleWithImage>,
    cabinetX: Int = 0,
    cabinetY: Int = 0,
    moduleX: Int = 0,
    moduleY: Int = 0,
    deleteEvent: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "캐비닛 ${cabinetY} 행 ${cabinetX} 열",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 4.dp)
                )
            } // Row 끝
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "모듈 ${moduleY} 행 ${moduleX} 열",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 2.dp, bottom = 8.dp)
                )
            } // Row 끝
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "정확도 ${(mais[selectedIdx.value].errorModule.score * 100).roundToInt()}%",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 16.dp)
                )
            } // Row 끝


            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = mais, itemContent = { item ->
                    item.errorImage?.evidence_image?.let { byteArray ->
                        var showDeleteButton by remember { mutableStateOf(false) }
                        Box(
                            contentAlignment = Alignment.TopEnd
                        ) {
                            GlideImage(
                                model = byteArray,
                                contentDescription = "글라이드",
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(color = Color.Black)
                                    .clickable(onClick = { selectedIdx.value = mais.indexOf(item) })
                            )
                            if (selectedIdx.value == mais.indexOf(item)) {
                                Canvas(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .combinedClickable(
                                            onClick = {
                                                showDeleteButton = false
                                            },
                                            onLongClick = {
                                                showDeleteButton = true
                                            },
                                        )
                                )
                                {
                                    //draw shapes here
                                    drawRoundRect(
                                        color = Color(0x80000000),
                                        cornerRadius = CornerRadius(5f, 5f)
                                    )
                                }
                                if (showDeleteButton) {
                                    IconButton(
                                        modifier = Modifier.size(20.dp),
                                        onClick = { deleteEvent() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "삭제",
                                            tint = MaterialTheme.colors.surface,
                                            modifier = Modifier
                                                .size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                })
            }
        } // Col 끝
    }
}