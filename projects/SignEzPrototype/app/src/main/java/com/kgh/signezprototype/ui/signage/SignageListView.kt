package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kgh.signezprototype.R
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.analysis.AnalysisViewModel
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGGrey
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme
import java.text.NumberFormat
import java.util.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.composed
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.kgh.signezprototype.R
import com.kgh.signezprototype.ui.components.BottomDoubleFlatButton
import com.kgh.signezprototype.ui.components.BottomSingleFlatButton
import com.kgh.signezprototype.ui.components.SignEzFloatingButton
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme

object SignageListScreenDestination : NavigationDestination {
    override val route = "SignageList"
    override val titleRes = "Total Signage"
}
// clickable의 ripple효과 없애는 메서드
inline fun Modifier.noRippleClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun SignageInformationScreen(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel
) {

    val focusManager = LocalFocusManager.current
    var selectedId: Long by remember { mutableStateOf(-1) }

    androidx.compose.material.Scaffold(
        modifier = Modifier
//            .noRippleClickable { focusManager.clearFocus() }
            .clickable{ focusManager.clearFocus() }
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "사이니지 정보 입력",
                canNavigateBack = true
            )
        },
        floatingActionButton = {
            SignEzFloatingButton(
                onClickEvent = { navController.navigate(AddSignageDestination.route) }
            )
        },
        bottomBar = {
            if (selectedId > -1) {
                BottomSingleFlatButton(title = "선택", isUsable = true) {
                    viewModel.signageId.value = selectedId
                    navController.popBackStack()
                }
            }
        }
    ){ innerPadding ->
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
                    SearchBar()
                    Spacer(modifier.padding(10.dp))
                    Text(
                        text = "전체 사이니지",
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
                        SignageList(onItemClick = { signage ->
                            run {
                                if (selectedId == signage.id) {
                                    selectedId = -1
                                } else {
                                    selectedId = signage.id
                                }
                            }
                        }, selectedId = selectedId,
                            navController=navController)
                    }

                    // 삭제하시나여?
                    if (selectedId > -1) {
                        Button(onClick = {
                            viewModel.signageId.value = selectedId
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
fun SignageList(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignageViewModel = viewModel(factory = AppViewModelProvider.Factory),
    selectedId: Long,
    navController:NavHostController
) {
    val signageListState by viewModel.signageListState.collectAsState()
    val itemList = signageListState.itemList
//    val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
//        value = viewModel.getRelatedCabinet(1)
//    })
//    val cabinet = cabinetState.value

//    if (selectedId > -1) { // 외래기 연결 데이터 확인용
//        Button(onClick = { /*TODO*/ }) {
//            if (cabinet != null) {
//                Text(text=cabinet.cabinetHeight.toString())
//            }
//        }
//    }

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
                    signage = item,
                    onItemClick = onItemClick,
                    selectedId=selectedId,
                    navController=navController)
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

// 조건 맞춰서 Modifier 바꾸는 것
fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}


@Composable
private fun InventoryItem(
    signage: Signage,
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    selectedId: Long,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
        .fillMaxWidth()
        .conditional(selectedId == signage.id) {
            background(
                color = Color(0xFFE6E6E6)
            )
        }
        .clickable {
            navController.navigate(DetailSignageScreenDestination.route+"/${signage.id}")
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
                selectedId == signage.id,
                onClick = {
                    onItemClick(signage)
                },
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
                .clickable { /*여기에 해당 사이니지 정보로가는 이벤트 넣으면 됩니다*/ }
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 10.dp)) {
                signage.repImg?.let { byteArray ->
                    byteArray.let {
                        bitmap = byteArrayToBitmap(it)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Signage Image",
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }
            } // 대표 이미지

            Column(modifier = modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp))
            {
                Text(
                    text = signage.name,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                Row(modifier = modifier.padding(top = 3.dp)) {
                    Text(
                        text = "W : " + NumberFormat.getNumberInstance(Locale.getDefault())
                            .format(signage.width) + "mm / ",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                    )
                    Text(
                        text = "H : " + NumberFormat.getNumberInstance(Locale.getDefault())
                            .format(signage.height) + "mm",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            } // 텍스트 공간
        } // 텍스트 공간
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "설치 장소 검색",
    onSearch: (String) -> Unit = {}
) {
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { newValue -> searchQuery.value = newValue },
        modifier = Modifier
            .fillMaxWidth(),
        textStyle = MaterialTheme.typography.h3,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.onBackground,
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(searchQuery.value)
            keyboardController?.hide()
        }),
        colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onSurface,
            backgroundColor = MaterialTheme.colors.secondary,
            focusedBorderColor = MaterialTheme.colors.secondary,
            unfocusedBorderColor = MaterialTheme.colors.secondary
        )
    )
}

//@Preview
//@Composable
//fun ComponentPreview() {
//    SignEzPrototypeTheme(darkTheme = false) {
//        Column {
//            SearchBar()
//        }
//   }
//}