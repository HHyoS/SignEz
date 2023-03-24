package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGBlue
import com.kgh.signezprototype.ui.theme.OneBGGrey
import java.text.NumberFormat
import java.util.*
import com.kgh.signezprototype.ui.components.BottomSingleFlatButton
import com.kgh.signezprototype.ui.components.SignEzFloatingButton

object CabinetListScreenDestination : NavigationDestination {
    override val route = "CabinetList"
    override val titleRes = "Total Cabinet"
}


@Composable
fun CabinetInformationScreen(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    signageViewModel: SignageViewModel,
    detailViewModel: SignageDetailViewModel,
    mode: String
    ) {

    val focusManager = LocalFocusManager.current
    var selectedId: Long by remember { mutableStateOf(-1) }
    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "캐비닛 정보 입력",
                canNavigateBack = true
            )
        },
        floatingActionButton = {
            SignEzFloatingButton(
                onClickEvent = { navController.navigate(AddCabinetDestination.route) }
            )
        },
        bottomBar = {
            if (selectedId != -1L) {
                BottomSingleFlatButton(title = "선택", isUsable = true) {
                    signageViewModel.selectedCabinetId.value = selectedId
                    navController.popBackStack()
                }
            }
        },
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SearchBar(placeholder = "모델 명 검색")
                    Spacer(modifier.padding(10.dp))
                    Text(
                        text = "전체 캐비닛",
                        modifier = modifier
                            .align(alignment = Alignment.Start)
                            .padding(start = 10.dp),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface,
                    )
                    Spacer(modifier.padding(5.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.99F)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colors.surface),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        CabinetList(onItemClick = { cabinet ->
                            run {
                                if (selectedId == cabinet.id) {
                                    selectedId = -1
                                } else {
                                    selectedId = cabinet.id
                                }
                            }
                        }, selectedId = selectedId,
                            navController = navController)
                    }
                    // 이거 지우나요?
                    if (selectedId != -1L) {
                        if (mode == "edit") {
                            Button(onClick = {
                                detailViewModel.newCabinetId.value = selectedId
                                navController.popBackStack()
                            }) {
                                Text(text = "선택")
                            }
                        } else {
                            Button(onClick = {
                                signageViewModel.selectedCabinetId.value = selectedId
                                navController.popBackStack()
                            }) {
                                Text(text = "선택")
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun CabinetList(
    onItemClick: (Cabinet) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CabinetViewModel = viewModel(factory = AppViewModelProvider.Factory),
    selectedId: Long,
    navController:NavController
) {
    val cabinetListState by viewModel.cabinetListState.collectAsState()
    val itemList = cabinetListState.itemList
    if (itemList.isEmpty()) {
        Text(
            text = "텅 비었어요.",
            style = MaterialTheme.typography.subtitle2
        )
    } else {
        LazyColumn(
            modifier = modifier.background(MaterialTheme.colors.surface),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = itemList, key = { it.id }) { item ->
                InventoryItem(
                    cabinet = item,
                    onItemClick = onItemClick,
                    selectedId = selectedId,
                    navController = navController)
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth(0.95f),
                    startIndent = 70.dp
                )
            }
        }
    }
}

@Composable
private fun InventoryItem(
    cabinet: Cabinet,
    onItemClick: (Cabinet) -> Unit,
    modifier: Modifier = Modifier,
    selectedId: Long,
    navController: NavController
) {
    Row(modifier = modifier
//        .clickable {  }
        .fillMaxWidth()
        .conditional(selectedId == cabinet.id) {
            background(
                color = Color(0xFFE6E6E6)
            )
        },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        var bitmap: Bitmap
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selectedId == cabinet.id,
                onClick = { onItemClick(cabinet) },
                enabled = true,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colors.onSurface,
                    unselectedColor = MaterialTheme.colors.secondary
                )
            ) // 라디오 버튼
        }
        Divider(
//            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .height(20.dp)
                .width(1.dp)
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { navController.navigate(DetailCabinetScreenDestination.route + "/${cabinet.id}") }
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 10.dp)) {
                cabinet.repImg?.let { byteArray ->
                    byteArray.let {
                        bitmap = byteArrayToBitmap(it)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Cabinet Image",
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }
            }// 대표 이미지
            Column(modifier = modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp))
            {
                Text(
                    text = cabinet.name,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                Row(modifier = modifier.padding(top = 3.dp)) {
                    Text(
                        text = "W : " + NumberFormat.getNumberInstance(Locale.getDefault())
                            .format(cabinet.cabinetWidth) + "mm / ",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                    )
                    Text(
                        text = "H : " + NumberFormat.getNumberInstance(Locale.getDefault())
                            .format(cabinet.cabinetHeight) + "mm / ",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                    )
                    Text(
                        text = "${cabinet.moduleColCount}X${cabinet.moduleRowCount}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            } // 텍스트 공간
        }
    }
}