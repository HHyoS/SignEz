package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import java.text.NumberFormat

object SignageListScreenDestination : NavigationDestination {
    override val route = "SignageList"
    override val titleRes = "Total Signage"
}
@Composable
fun SignageInformationScreen(    
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,) {

    val focusManager = LocalFocusManager.current

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .clickable(onClick = { focusManager.clearFocus() }),
        topBar = {
            SignEzTopAppBar(
                title = "사이니지 정보 입력",
                canNavigateBack = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  }, //navigateToEditItem(uiState.value.id)
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "사이니지 추가",
                    tint = MaterialTheme.colors.onPrimary
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
                    SignageList(onItemClick = onItemClick)
                }
            }
        }
    }

}

@Composable
fun SignageList(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignageViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
                InventoryItem(signage = item, onItemClick = onItemClick)
                Divider()
            }
        }
    }
}

@Composable
private fun InventoryItem(
    signage: Signage,
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick(signage) }
        .padding(vertical = 16.dp)
    ) {
        var bitmap:Bitmap
        Text(
            text = signage.name,
            modifier = Modifier.weight(1.5f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = NumberFormat.getCurrencyInstance().format(signage.width),
            modifier = Modifier.weight(1.0f)
        )
        Text(
            text = NumberFormat.getCurrencyInstance().format(signage.height),
            modifier = Modifier.weight(1.0f)
        )
        signage.repImg?.let { byteArray ->
            byteArray.let {
                bitmap = byteArrayToBitmap(it)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Signage Image",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
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

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    val options = BitmapFactory.Options()
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
}