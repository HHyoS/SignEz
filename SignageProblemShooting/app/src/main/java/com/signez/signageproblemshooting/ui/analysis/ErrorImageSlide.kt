package com.signez.signageproblemshooting.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.ErrorModuleWithImage
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.noRippleClickable
import com.signez.signageproblemshooting.ui.theme.OneBGDarkGrey
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object ErrorImageDestination : NavigationDestination {
    override val route = "ErrorImageScreen"
    override val titleRes = "ErrorImage"
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ErrorImageView(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
    navigateBack: () -> Unit,
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
    LaunchedEffect(deletionCompleted.value) {
        if (deletionCompleted.value) {
            deletionCompleted.value = false
        }
        maisState.value = viewModel.getModulesByXYResultId(
            x = viewModel.selectedModuleX.value,
            y = viewModel.selectedModuleY.value,
            resultId = viewModel.selectedResultId.value
        )
    }
    val mais = maisState.value //

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "근거 이미지 슬라이드",
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
                        if (mais.isEmpty()) {
                            Text(
                                text = "텅 비었어요.",
                                style = MaterialTheme.typography.subtitle2
                            )
                        }
//                        else {
//                            LazyColumn(
//                                modifier = modifier.background(MaterialTheme.colors.surface),
//                                //            verticalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                items(items = mais, key = { it.errorModule.id }) { item ->
//                                    Text(text = "${item.errorModule.id}번 에러 모듈 좌표 : ${item.errorModule.x} , ${item.errorModule.y}")
//                                    Divider(
//                                        modifier = Modifier
//                                            .height(1.dp)
//                                            .fillMaxWidth(0.95f),
//                                        startIndent = 70.dp
//                                    )
//
//                                }
//                            }
//                        }
                    } // 모듈 눌체크

                    if (mais != null && mais.isNotEmpty()) {
                        GlideImage(
                            model = mais[selectedIdx.value].errorImage?.evidence_image,
                            contentDescription = "글라이드",
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.65f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = Color.Black)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        mais[selectedIdx.value].errorModule.let {
//                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                                Column {
//                                    Text(
//                                        text = "정확도: ${(mais[selectedIdx.value].errorModule.score * 100).roundToInt()}%",
//                                        fontSize = 20.sp,
//                                        fontWeight = FontWeight.Bold,
//                                        modifier = Modifier.align(alignment = Alignment.Start)
//                                    )
//                                    Text(
//                                        text = "x: ${mais[selectedIdx.value].errorModule.x}" +
//                                                " y: ${mais[selectedIdx.value].errorModule.y}",
//                                        fontSize = 20.sp,
//                                        fontWeight = FontWeight.Bold,
//                                        modifier = Modifier.align(alignment = Alignment.Start)
//                                    )
//                                }
//                                Button(onClick = {
//                                    coroutineScope.launch {
//                                        viewModel.deleteErrorModule(mais[selectedIdx.value].errorModule)
//                                        deletionCompleted.value = true
//                                    }
//                                }) {
//                                    Text(text = "삭제")
//                                }
//                            }

//                            Spacer(modifier = Modifier.padding(10.dp))
                            ErrorImageSlideBox(selectedIdx = selectedIdx, mais = mais)
                        }
                    }

                }

            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ErrorImageSlideBox(
    modifier: Modifier = Modifier,
    selectedIdx: MutableState<Int>,
    mais: List<ErrorModuleWithImage>
) {
    //
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
                    text = "캐비닛 : " +
                            "(X : ${mais[selectedIdx.value].errorModule.x}," +
                            " Y : ${mais[selectedIdx.value].errorModule.y})",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)
                )
            } // Row 끝
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "모듈 : " +
                            "(X : ${mais[selectedIdx.value].errorModule.x}," +
                            " Y : ${mais[selectedIdx.value].errorModule.y})",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)
                )
            } // Row 끝
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "정확도 : ${(mais[selectedIdx.value].errorModule.score * 100).roundToInt()}%",
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)
                )
            } // Row 끝


            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(items = mais, itemContent = { item ->
                    item.errorImage?.evidence_image?.let { byteArray ->
                        GlideImage(
                            model = byteArray,
                            contentDescription = "글라이드",
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = OneBGDarkGrey)
                                .clickable(onClick = { selectedIdx.value = mais.indexOf(item) })
                                .padding(5.dp)
                        )
                    }
                })
            }
        } // Col 끝
    }
}