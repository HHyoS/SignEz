package com.kgh.signezprototype.ui.signage

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import androidx.navigation.NavHostController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.fields.CustomTextInput
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.pickers.ImagePicker
import com.kgh.signezprototype.ui.inputs.dispatchTakePictureIntent
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGDarkGrey
import com.kgh.signezprototype.ui.theme.OneBGGrey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object AddCabinetDestination : NavigationDestination {
    override val route = "AddCabinet"
    override val titleRes = "Add Cabinet"
}


@Composable
fun AddCabinetScreen(modifier:Modifier = Modifier
                     ,activity:Activity
                     ,viewModel: CabinetViewModel,
                     navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var imageBitmap by remember { mutableStateOf<Bitmap>(bitmap) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY

    val cabinetWidth = remember { mutableStateOf("") } // 사이니지
    val cabinetHeight = remember { mutableStateOf("") } // 사이니지
    val cabinetName = remember { mutableStateOf("") }
    val colModuleCount = remember { mutableStateOf("") }
    val rowModuleCount = remember { mutableStateOf("") }
    val allFieldsNotEmpty = (
                cabinetWidth.value.isNotEmpty() &&
                cabinetHeight.value.isNotEmpty() &&
                cabinetName.value.isNotEmpty() &&
                colModuleCount.value.isNotEmpty() &&
                rowModuleCount.value.isNotEmpty()
            )
    if (viewModel.imageUri.value != Uri.EMPTY) {
        // content uri가 아니면 content uri로 바꿔줌.
        if (!viewModel.imageUri.value.toString().contains("content")) {
            contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }
        else {
            contentUri = viewModel.imageUri.value
        }
    }

    if (viewModel.imageUri.value != Uri.EMPTY) {
        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
    }

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .clickable(onClick = { focusManager.clearFocus() })
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "새 캐비닛 추가",
                canNavigateBack = true
            )
        },
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(modifier = modifier.padding(15.dp))
                if (viewModel.imageUri.value == Uri.EMPTY) {
                    Box {
                        imageBitmap.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "rep Image",
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(0.3f)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(color = OneBGDarkGrey)
                            )
                        }

                        Text(
                            text = "캐비닛 사진을 추가해 주세요.",
                            modifier = Modifier.align(Alignment.Center), // Adjust the alignment as needed
                            style = TextStyle(color = Color.Black), // Customize the text style
                        )
                    }
                } else {
                    imageBitmap.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "rep Image",
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.3f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = OneBGDarkGrey)
                        )
                    }
                }


                Row {
                    ImagePicker(onImageSelected = { address ->
                        imageBitmap = bitmap
                        viewModel.imageUri.value = Uri.parse(address)
                    })

                    OutlinedButton(
                        onClick = { dispatchTakePictureIntent(activity, viewModel=viewModel,type=222) },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color.Blue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("카메라")
                    }

                    OutlinedButton(
                        onClick = {
                            imageBitmap = bitmap
                            viewModel.imageUri.value = Uri.EMPTY
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color.Blue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Clear")
                    }
                }
                CustomTextInput(
                    value = cabinetName.value,
                    onValueChange = { it -> cabinetName.value = it }
                    , placeholder = "캐비닛 모델 명")
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
                    value = cabinetWidth.value,
                    onValueChange = { cabinetWidth.value = it },
                    unit = "mm"
                )
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
                    value = cabinetHeight.value,
                    onValueChange = { cabinetHeight.value = it },
                    unit = "mm"
                )
                EditNumberField(
                    // 세로 길이
                    head = "가로 모듈 수",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done

                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    value = colModuleCount.value,
                    onValueChange = { colModuleCount.value = it },
                    unit = "개"
                )
                EditNumberField(
                    // 세로 길이
                    head = "세로 모듈 수",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done

                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    value = rowModuleCount.value,
                    onValueChange = { rowModuleCount.value = it },
                    unit = "개"
                )
                Row {
                    Button(onClick = { navController.popBackStack() }) {
                        Text(text = "취소")
                    }
                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                viewModel.saveItem(
                                    name=cabinetName.value,
                                    width=cabinetWidth.value.toDouble(),
                                    height=cabinetHeight.value.toDouble(),
                                    colCount=colModuleCount.value.toInt(),
                                    rowCount=rowModuleCount.value.toInt(),
                                    bitmap=imageBitmap)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "입력 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } },
                        enabled= allFieldsNotEmpty) {
                        Text(text = "저장")
                    }
                }
            }// 화면 전체 컬럼 끝
        }// 화면 전체 박스 끝
    }
}