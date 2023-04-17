package com.signez.signageproblemshooting.ui.analysis

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.components.ResultHistoryBlock
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.*
import kotlinx.coroutines.launch
import java.util.*

object ResultsHistoryDestination : NavigationDestination {
    override val route = "ResultsHistoryScreen"
    override val titleRes = "ResultsHistory"
}

@Composable
fun ResultsHistoryView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val selectedId: Long by remember { mutableStateOf(-1) }

    Scaffold(
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
                        viewModel = viewModel
                    )

                }

            }
        }
    }
}

@Composable
fun ResultList(
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
                    navController = navController,
                    selectedId = selectedId,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ResultItem(
    result: AnalysisResult,
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
            )
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