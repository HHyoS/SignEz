package com.signez.signageproblemshooting.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.components.BottomSingleFlatButton
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
) {
    val focusManager = LocalFocusManager.current
    val modulesState = produceState(initialValue = null as List<ErrorModule>?, producer = {
        value = viewModel.getRelatedModule(viewModel.selectedResultId.value)
    })
    val modules = modulesState.value

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
                navController.navigate(ErrorImageDestination.route)
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column {
                    Text(text="분석 도표 페이지")

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
                }// 컬럼 끝

            } // 박스 끝
        }
    }
}