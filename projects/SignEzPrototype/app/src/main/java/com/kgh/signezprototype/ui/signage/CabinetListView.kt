package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.inputs.VideoScreenDestination
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGBlue
import com.kgh.signezprototype.ui.theme.OneBGGrey
import java.text.NumberFormat
import java.util.*

object CabinetListScreenDestination : NavigationDestination {
    override val route = "CabinetList"
    override val titleRes = "Total Cabinet"
}

@Composable
fun CabinetInformationScreen(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController:NavHostController,
    signageViewModel:SignageViewModel
    ) {

    val focusManager = LocalFocusManager.current
    var selectedId:Long by remember { mutableStateOf(-1)}
    androidx.compose.material.Scaffold(
        modifier = Modifier
            .clickable(onClick = { focusManager.clearFocus() })
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "캐비닛 정보 입력",
                canNavigateBack = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AddCabinetDestination.route) }, //navigateToEditItem(uiState.value.id)
                modifier = Modifier.navigationBarsPadding(),
                backgroundColor = OneBGBlue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "캐비닛 추가",
                    tint = Color.White
                )
            }
        },
    ){ innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    SearchBar()
                    Spacer(modifier.padding(10.dp))
                    Text(text = "전체 사이니지",
                        modifier=modifier
                            .align(alignment=Alignment.Start),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.9F)
                            .background(Color.White, shape = RoundedCornerShape(10.dp)),
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
                        }, selectedId = selectedId)
                    }
                    if (selectedId != -1L) {
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

@Composable
fun CabinetList(
    onItemClick: (Cabinet) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CabinetViewModel = viewModel(factory = AppViewModelProvider.Factory),
    selectedId: Long
) {
    val cabinetListState by viewModel.cabinetListState.collectAsState()
    val itemList = cabinetListState.itemList
    if (itemList.isEmpty()) {
        Text(
            text = "텅 비었어요.",
            style = MaterialTheme.typography.subtitle2
        )
    } else {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = itemList, key = { it.id }) { item ->
                InventoryItem(cabinet = item,
                    onItemClick = onItemClick,
                    selectedId=selectedId)
                Divider()
            }
        }
    }
}

@Composable
private fun InventoryItem(
    cabinet: Cabinet,
    onItemClick: (Cabinet) -> Unit,
    modifier: Modifier = Modifier,
    selectedId:Long
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick(cabinet) }
        .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var bitmap:Bitmap
        RadioButton(
            selectedId==cabinet.id,
            onClick = { onItemClick(cabinet)},
            enabled=true
        ) // 라디오 버튼
        cabinet.repImg?.let { byteArray ->
            byteArray.let {
                bitmap = byteArrayToBitmap(it)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Signage Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(15.dp))
                )
            }
        } // 대표 이미지
        Column (modifier=modifier.padding(start=16.dp)) {
            Text(
                text = cabinet.name,
                fontWeight = FontWeight.Bold,
                color=Color.Black,
                fontSize = 20.sp,
            )
            Row (modifier=modifier.padding()) {
                Text (
                    text = "W : " + NumberFormat.getNumberInstance(Locale.getDefault()).format(cabinet.cabinetWidth)+"mm / ",
                )
                Text(
                    text = "H : " + NumberFormat.getNumberInstance(Locale.getDefault()).format(cabinet.cabinetHeight)+"mm / ",
                )
                Text(
                    text = "${cabinet.moduleColCount}X${cabinet.moduleRowCount}"
                )
            }
        } // 텍스트 공간
    }
}