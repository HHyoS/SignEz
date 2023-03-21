package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.inputs.VideoScreenDestination
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGBlue
import com.kgh.signezprototype.ui.theme.OneBGGrey
import java.text.NumberFormat
import java.util.*

object SignageListScreenDestination : NavigationDestination {
    override val route = "SignageList"
    override val titleRes = "Total Signage"
}
@Composable
fun SignageInformationScreen(    
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController:NavHostController
    ) {

    val focusManager = LocalFocusManager.current
    var selectedId:Long by remember { mutableStateOf(-1)}

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .clickable(onClick = { focusManager.clearFocus() })
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "사이니지 정보 입력",
                canNavigateBack = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AddSignageDestination.route) }, //navigateToEditItem(uiState.value.id)
                modifier = Modifier.navigationBarsPadding(),
                backgroundColor = OneBGBlue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "사이니지 추가",
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
                    SearchBar()
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
                            .background(Color.White,shape=RoundedCornerShape(10.dp)),
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
                        }, selectedId = selectedId)
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
    selectedId: Long
) {
    val signageListState by viewModel.signageListState.collectAsState()
    val itemList = signageListState.itemList
    if (itemList.isEmpty()) {
        Text(
            text = "텅 비었어요.",
            style = MaterialTheme.typography.subtitle2
        )
    } else {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = itemList, key = { it.id }) { item ->
                InventoryItem(signage = item,
                    onItemClick = onItemClick,
                selectedId=selectedId)
                Divider()
            }
        }
    }
}

@Composable
private fun InventoryItem(
    signage: Signage,
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    selectedId:Long
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick(signage) }
        .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var bitmap:Bitmap
        RadioButton(
            selectedId==signage.id,
            onClick = { onItemClick(signage)},
            enabled=true
        ) // 라디오 버튼
        signage.repImg?.let { byteArray ->
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
                text = signage.name,
                fontWeight = FontWeight.Bold,
                color=Color.Black,
                fontSize = 20.sp,
            )
            Row (modifier=modifier.padding()) {
                Text (
                    text = "W : " + NumberFormat.getNumberInstance(Locale.getDefault()).format(signage.width)+"mm / ",
                )
                Text(
                    text = "H : " + NumberFormat.getNumberInstance(Locale.getDefault()).format(signage.height)+"mm",
                )
            }
        } // 텍스트 공간
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "설치 장소 검색",
    onSearch: (String) -> Unit = {}
) {
    val searchQuery = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { newValue -> searchQuery.value = newValue },
        modifier = Modifier.fillMaxWidth(0.9f),
        label = { Text(hint) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch(searchQuery.value)
            keyboardController?.hide()
        }),
        colors = androidx.compose.material.TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun CheckboxRow(
    text: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colors.onPrimary
        } else {
            MaterialTheme.colors.secondary
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colors.onPrimary
            } else {
                MaterialTheme.colors.secondary
            }
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, Modifier.weight(1f))
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}

@Composable
fun RoundedBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Gray,
    cornerRadius: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
    ) {
        content()
    }
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    val options = BitmapFactory.Options()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
}