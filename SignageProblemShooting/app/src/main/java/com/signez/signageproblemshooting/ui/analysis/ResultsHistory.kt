package com.signez.signageproblemshooting.ui.analysis

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.components.BottomSingleFlatButton
import com.signez.signageproblemshooting.ui.components.ResultHistoryBlock
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

object ResultsHistoryDestination : NavigationDestination {
    override val route = "ResultsHistoryScreen"
    override val titleRes = "ResultsHistory"
}

@Composable
fun ResultsHistoryView(
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val selectedId: Long by remember { mutableStateOf(-1) }

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "지난 분석 결과",
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

                Column {
                    ResultList(
                        selectedId = selectedId,
                        navController = navController,
                        onItemClick = {},
                        viewModel = viewModel
                    )

                }

            }
        }
    }
}

@Composable
fun ResultList(
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnalysisViewModel,
    selectedId: Long,
    navController: NavHostController,
) {
    val resultListState by viewModel.resultListState.collectAsState()
    val itemList = resultListState.itemList


    if (itemList.isEmpty()) {
        Text(
            text = "텅 비었어요.",
            style = MaterialTheme.typography.subtitle2
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(count = itemList.size) { item ->
                // Item content here
                ResultItem(
                    result = itemList[item],
                    onItemClick = {},
                    navController = navController,
                    selectedId = selectedId,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ResultItem(
    result: AnalysisResult,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedId: Long,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
) {
    var showContextMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .conditional(selectedId == result.id) {
                background(
                    color = Color(0xFFE6E6E6)
                )
            }
            .combinedClickable(
                onClick = {
                    viewModel.selectedResultId.value = result.id
                    navController.navigate(ResultGridDestination.route + "/${result.id}")
                },
                onLongClick = {
                    showContextMenu = true
                },
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val signageState = produceState(initialValue = null as Signage?, producer = {
            value = viewModel.getSignageById(result.signageId)
        })
        val signage = signageState.value

        if (signage != null) {
            ResultHistoryBlock(
                site = signage.name,
                date = result.resultDate,
                thumbnail = signage.repImg,
                modifier = Modifier
            )
//            Column(modifier = Modifier.padding(start = 10.dp)) {
//                signage.repImg?.let { byteArray ->
//                    GlideImage(
//                        model = byteArray,
//                        contentDescription = "글라이드",
//                        modifier = Modifier
//                            .size(45.dp)
//                            .clip(RoundedCornerShape(10.dp))
//                    )
//                }
//
//                Text(
//                    text = signage.name,
//                    style = MaterialTheme.typography.h4,
//                    color = MaterialTheme.colors.onSecondary,
//                    modifier = Modifier.padding(bottom = 5.dp)
//                )
//                Text(
//                    text = result.resultDate,
//                    style = MaterialTheme.typography.h4,
//                    color = MaterialTheme.colors.onSecondary,
//                    modifier = Modifier.padding(bottom = 5.dp)
//                )
//            }// 열 끝
            if (showContextMenu) {
                ShowContextMenu(
                    closeMenu = {
                        showContextMenu = false
                    },
                    onDelete = {
                        coroutineScope.launch {
                            viewModel.deleteResult(result.id)
                            showContextMenu = false
                        }
                    }
                )
            }
        } // if 끝
    }
}

@Composable
fun ShowContextMenu(
    onDelete: () -> Unit,
    closeMenu: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = {},
    ) {
        DropdownMenuItem(onClick = onDelete) {
            Text("Delete")
        }
        DropdownMenuItem(onClick = closeMenu) {
            Text("닫기")
        }
    }
}