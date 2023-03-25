package com.kgh.signezprototype.ui.signage

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.fields.CustomTextInput
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.pickers.ImagePicker
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.components.BottomDoubleFlatButton
import com.kgh.signezprototype.ui.components.FocusBlock
import com.kgh.signezprototype.ui.components.IntentButton
import com.kgh.signezprototype.ui.inputs.dispatchTakePictureIntent
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGDarkGrey
import com.kgh.signezprototype.ui.theme.OneBGGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object DetailSignageScreenDestination : NavigationDestination {
    override val route = "DetailSignage"
    override val titleRes = "사이니지 정보"
}

// 로컬에서 뷰모델 사용하기1
//val LocalSignageDetailViewModel = compositionLocalOf<SignageDetailViewModel> { error("No SignageDetailViewModel found!") }

@Composable
fun SDetail(
    navController: NavController,
    signageId: Long = -1,
    activity: Activity,
    viewModel: SignageDetailViewModel,
    modifier: Modifier = Modifier,
) {
// 로컬 뷰모델 2
//    val viewModel = LocalViewModelStoreOwner.current?.let {
//            viewModel<SignageDetailViewModel>(
//            viewModelStoreOwner = it,
//            factory = AppViewModelProvider.Factory
//        )
//    }

    val coroutineScope = rememberCoroutineScope()
    var bitmap: Bitmap? = null
//    var imageBitmap: Bitmap? = null
    var imageBitmap by remember { mutableStateOf<Bitmap?>(bitmap) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY

//    CompositionLocalProvider(LocalSignageDetailViewModel provides viewModel!!) { 로컬 뷰모델 3
    val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
        value = viewModel.getCabinet(signageId)
    })
    val cabinet = cabinetState.value
    val signageState = produceState(initialValue = null as Signage?, producer = {
        value = viewModel.getSignage(signageId)
    })
    val signage = signageState.value

    val imageLoadingScope = CoroutineScope(Dispatchers.Main)

    // Load the image asynchronously using coroutines
    fun loadImageAsync(context: Context, contentUri: Uri) {
        imageLoadingScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Load the image bitmap on a background thread
                    imageBitmap =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
                } catch (e: Exception) {
                    // Handle any errors that occur while loading the image
                    Log.e("Error", "Error loading image", e)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.imageUri.value = Uri.EMPTY
        onDispose {} // Cleanup logic here, if needed
    }

    if (signage != null) {
        viewModel.sName.value = signage.name
        viewModel.sHeight.value = signage.height.toString()
        viewModel.sWidth.value = signage.width.toString()
    }


    if (viewModel.imageUri.value != Uri.EMPTY) {
        // content uri가 아니면 content uri로 바꿔줌.
        if (!viewModel.imageUri.value.toString().contains("content")) {
            contentUri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } else {
            contentUri = viewModel.imageUri.value
        }
    }
    if (viewModel.imageUri.value != Uri.EMPTY) {
//        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
        loadImageAsync(context, contentUri)
    }

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "사이니지 정보",
                canNavigateBack = true
            )
        },
        bottomBar = {
            BottomDoubleFlatButton(
                leftTitle = "확인",
                rightTitle = "삭제",
                isLeftUsable = true,
                isRightUsable = true,
                leftOnClickEvent = {
                    coroutineScope.launch {
                        try {
                            if (signage != null) {
                                if (imageBitmap == null) {
                                    viewModel.updateRecord(
                                        bitmap = null,
                                        signage = signage
                                    )
                                } else {
                                    Log.d("어이", "SDetail: ${imageBitmap.toString()}")
                                    viewModel.updateRecord(
                                        bitmap = imageBitmap,
                                        signage = signage
                                    )
                                }
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                rightOnClickEvent = {
                    coroutineScope.launch {
                        try {
                            if (signage != null) {
                                viewModel.delete(signage = signage)
                                navController.navigate(SignageListScreenDestination.route)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(modifier = modifier.padding(15.dp))
                if (viewModel.imageUri.value == Uri.EMPTY) {
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        signage?.repImg?.let { byteArray ->
                            byteArray.let {
                                bitmap = byteArrayToBitmap(it)
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = "Signage Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(color = MaterialTheme.colors.onSurface)
                                )
                            }
                        }
//                        Text(
//                            text = "사이니지 사진을 추가해 주세요.",
//                            modifier = Modifier.align(Alignment.Center), // Adjust the alignment as needed
//                            style = TextStyle(color = Color.Black), // Customize the text style
//                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        imageBitmap.let {
                            if (it != null) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "rep Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(color = MaterialTheme.colors.onSurface)
                                )
                            }
                        }
                    }
                }
                //
//                Column {
//                    Spacer(modifier = Modifier.padding(innerPadding))
//                    if (viewModel.imageUri.value == Uri.EMPTY) {
//                        signage?.repImg?.let { byteArray ->
//                            byteArray.let {
//                                bitmap = byteArrayToBitmap(it)
//                                Image(
//                                    bitmap = bitmap!!.asImageBitmap(),
//                                    contentDescription = "Signage Image",
//                                    modifier = Modifier
//                                        .fillMaxWidth(0.9f)
//                                        .fillMaxHeight(0.3f)
//                                        .clip(RoundedCornerShape(15.dp))
//                                        .background(color = OneBGDarkGrey)
//                                )
//                            }
//                        }
//                    } else {
//                        imageBitmap.let {
//                            if (it != null) {
//                                Image(
//                                    bitmap = it.asImageBitmap(),
//                                    contentDescription = "rep Image",
//                                    modifier = Modifier
//                                        .fillMaxWidth(0.9f)
//                                        .fillMaxHeight(0.3f)
//                                        .clip(RoundedCornerShape(15.dp))
//                                        .background(color = OneBGDarkGrey)
//                                )
//                            }
//                        }
//                    }
//                  //
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        ImagePicker(onImageSelected = { address ->
                            imageBitmap = bitmap
                            viewModel.imageUri.value = Uri.parse(address)
                        })
                    }
                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        IntentButton(title = "카메라") {
                            dispatchTakePictureIntent(
                                activity,
                                viewModel = viewModel,
                                type = 22
                            )
                        }
                    }
                }

                //
//                    Row {
//                        ImagePicker(onImageSelected = { address ->
//                            imageBitmap = bitmap
//                            viewModel.imageUri.value = Uri.parse(address)
//                        })
//
//                        OutlinedButton(
//                            onClick = {
//                                dispatchTakePictureIntent(
//                                    activity,
//                                    viewModel = viewModel,
//                                    type = 22
//                                )
//                            },
//                            shape = RoundedCornerShape(20.dp),
//                            border = BorderStroke(2.dp, Color.Blue),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                backgroundColor = Color.White,
//                                contentColor = Color.Blue
//                            ),
//                            modifier = Modifier.padding(16.dp)
//                        ) {
//                            Text("카메라")
//                        }
//                    }

                //

                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colors.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
//                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            CustomTextInput(
                                value = viewModel.sName.value,
                                onValueChange = { it -> viewModel.sName.value = it },
                                placeholder = "사이니지 이름"
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditNumberField(
                                // 가로 길이
                                head = "너비",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                value = viewModel.sWidth.value,
                                onValueChange = { viewModel.sWidth.value = it },
                                unit = "mm"
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EditNumberField(
                                // 세로 길이
                                head = "높이",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done

                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                value = viewModel.sHeight.value,
                                onValueChange = { viewModel.sHeight.value = it },
                                unit = "mm"
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }


                //
//                    CustomTextInput(
//                        value = viewModel.sName.value,
//                        onValueChange = { it -> viewModel.sName.value = it },
//                        placeholder = "사이니지 이름"
//                    )
//                    EditNumberField(
//                        // 가로 길이
//                        head = "너비",
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { focusManager.clearFocus() }
//                        ),
//                        value = viewModel.sWidth.value,
//                        onValueChange = { viewModel.sWidth.value = it },
//                        unit = "mm"
//                    )
//                    EditNumberField(
//                        // 세로 길이
//                        head = "높이",
//                        keyboardOptions = KeyboardOptions(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done
//
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onDone = { focusManager.clearFocus() }
//                        ),
//                        value = viewModel.sHeight.value,
//                        onValueChange = { viewModel.sHeight.value = it },
//                        unit = "mm"
//                    )


//                    Box {
//                        Column {
//                            OutlinedButton(
//                                onClick = {
//                                    navController.navigate(CabinetListScreenDestination.route + "/edit")
//                                },
//                                shape = RoundedCornerShape(20.dp),
//                                border = BorderStroke(2.dp, Color.Blue),
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    backgroundColor = Color.White,
//                                    contentColor = Color.Blue
//                                ),
//                                modifier = Modifier.padding(16.dp)
//                            ) {
//                                Text("선택")
//                            }
//                            Text(text = "캐비닛 스펙")
                if (cabinet != null) {
                    if (viewModel.newCabinetId.value > -1) {
                        val newCabinetState =
                            produceState(initialValue = null as Cabinet?, producer = {
                                value = viewModel.getNewCabinet()
                            })
                        val newCabinet = cabinetState.value
                        if (newCabinet != null) {
                            FocusBlock(
                                title = "캐비닛 스펙",
                                subtitle = newCabinet.name,
                                infols = listOf(
                                    "너비 : ${newCabinet.cabinetWidth} mm",
                                    "높이 : ${newCabinet.cabinetHeight} mm",
                                    "모듈 : ${newCabinet.moduleColCount}X${cabinet.moduleRowCount}"
                                ),
                                buttonTitle = "변경",
                                isbuttonVisible = true,
                                buttonOnclickEvent = {
                                    navController.navigate(
                                        CabinetListScreenDestination.route + "/edit"
                                    )
                                },
                                modifier = Modifier,
                            )
                        }
//                                    if (newCabinet != null) {
//                                        Text(text = newCabinet.name)
//                                        Text(text = "${newCabinet.cabinetWidth} mm")
//                                        Text(text = "${newCabinet.cabinetHeight} mm")
//                                        Text(text = "${newCabinet.moduleColCount}X${cabinet.moduleRowCount}")
//                                    }

                    } else {

                        FocusBlock(
                            title = "캐비닛 스펙",
                            subtitle = cabinet.name,
                            infols = listOf(
                                "너비 : ${cabinet.cabinetWidth} mm",
                                "높이 : ${cabinet.cabinetHeight} mm",
                                "모듈 : ${cabinet.moduleColCount}X${cabinet.moduleRowCount}"
                            ),
                            buttonTitle = "변경",
                            isbuttonVisible = true,
                            buttonOnclickEvent = {
                                navController.navigate(
                                    CabinetListScreenDestination.route + "/edit"
                                )
                            },
                            modifier = Modifier,
                        )
                    }
//                                    Text(text = cabinet.name)
//                                    Text(text = "${cabinet.cabinetWidth} mm")
//                                    Text(text = "${cabinet.cabinetHeight} mm")
//                                    Text(text = "${cabinet.moduleColCount}X${cabinet.moduleRowCount}")


                }
            }
//            Row {
//                Button(onClick = {
//                    coroutineScope.launch {
//                        try {
//                            if (signage != null) {
//                                if (imageBitmap == null) {
//                                    viewModel.updateRecord(
//                                        bitmap = null,
//                                        signage = signage
//                                    )
//                                } else {
//                                    viewModel.updateRecord(
//                                        bitmap = imageBitmap,
//                                        signage = signage
//                                    )
//                                }
//                                navController.popBackStack()
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(context, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }) {
//                    Text(text = "확인")
//                }
//
//                Button(onClick = {
//                    coroutineScope.launch {
//                        try {
//                            if (signage != null) {
//                                viewModel.delete(signage = signage)
//                                navController.navigate(SignageListScreenDestination.route)
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(context, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                }) {
//                    Text(text = "삭제")
//                }
//            }
        } // 캐비닛 변경 버튼 else문

    }// 화면 전체 컬럼 끝
//    } 로컬 뷰모델 4
    // Use viewModel here
}