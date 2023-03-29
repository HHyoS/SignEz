package com.signez.signageproblemshooting.ui.analysis

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.noRippleClickable

object ErrorImageDestination : NavigationDestination {
    override val route = "ErrorImageScreen"
    override val titleRes = "ErrorImage"
}

@Composable
fun ErrorImageView(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val modulesState = produceState(initialValue = null as List<ErrorModule>?, producer = {
        value = viewModel.getRelatedModule(viewModel.selectedResultId.value)
    })
    val modules = modulesState.value
    val selectedImage = remember {
        mutableStateOf("?")
    }

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
                .padding(start = 16.dp, end = 16.dp)
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
                    if (modules != null) {
                        if (modules.isEmpty()) {
                            Text(
                                text = "텅 비었어요.",
                                style = MaterialTheme.typography.subtitle2
                            )
                        } else {
                            LazyColumn(
                                modifier = modifier.background(MaterialTheme.colors.surface),
                                //            verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(items = modules, key = { it.id }) { item ->
                                    Text(text = "${item.id}번 에러 모듈 좌표 : ${item.x} , ${item.y}")
                                    Divider(
                                        modifier = Modifier
                                            .height(1.dp)
                                            .fillMaxWidth(0.95f),
                                        startIndent = 70.dp
                                    )

                                }
                            }
                        }
                    } // 모듈 눌체크

                    Text(text=selectedImage.value, fontSize = 100.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(10.dp))
                    ImageSliderTest(selectedImage)

                }

            }
        }
    }
}

@Composable
fun ImageSliderTest(selectedValue:MutableState<String>) {
    val list = listOf(
        "A", "B", "C", "D"
    ) + ((0..5).map { it.toString() })
    LazyRow(modifier = Modifier.fillMaxHeight()) {
        items(items = list, itemContent = { item ->
            when (item) {
                "A" -> {
                    Button(onClick = {selectedValue.value = item}) {
                        Text(text = item, style = TextStyle(fontSize = 80.sp))
                    }
                }
                "B" -> {
                    Button(onClick = {selectedValue.value = item}) {
                        Text(text = item, style = TextStyle(fontSize = 80.sp))
                    }
                }
                "C" -> {
                    Button(onClick = {selectedValue.value = item}) {
                        Text(text = item, style = TextStyle(fontSize = 80.sp))
                    }
                }
                "D" -> {
                    Button(onClick = {selectedValue.value = item}) {
                        Text(text = item, style = TextStyle(fontSize = 80.sp))
                    }
                }
                else -> {
                    Button(onClick = {selectedValue.value = item}) {
                        Text(text = item, style = TextStyle(fontSize = 80.sp))
                    }
                }
            }
        })
    }
}