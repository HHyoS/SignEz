package com.kgh.signezprototype.ui.signage

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.*
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
import com.kgh.signezprototype.data.entities.Cabinet
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

object AddSignageDestination : NavigationDestination {
    override val route = "AddSignage"
    override val titleRes = "Add Signage"
}


@Composable
fun AddSignageScreen(modifier:Modifier = Modifier
                     ,activity:Activity
                     ,viewModel: SignageViewModel,
                     navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var imageBitmap by remember { mutableStateOf<Bitmap>(bitmap) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY

    val sWidth = remember { mutableStateOf("") } // 사이니지
    val sHeight = remember { mutableStateOf("") } // 사이니지
    val sName = remember { mutableStateOf("") }
    val allFieldsNotEmpty = (
            sName.value.isNotEmpty() &&
            sWidth.value.isNotEmpty() &&
            sHeight.value.isNotEmpty()
            )
    val cabinetState by viewModel.getCabinet().collectAsState()

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
                title = "새 사이니지 추가",
                canNavigateBack = true
            )
        },
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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
                            text = "사이니지 사진을 추가해 주세요.",
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


                Column {
                    ImagePicker(onImageSelected = { address ->
                        imageBitmap = bitmap
                        viewModel.imageUri.value = Uri.parse(address)
                    })

                    OutlinedButton(
                        onClick = { dispatchTakePictureIntent(activity, viewModel=viewModel,type=22) },
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
                    value = sName.value,
                    onValueChange = { it -> sName.value = it },
                    placeholder = "사이니지 이름")
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
                    value = sWidth.value,
                    onValueChange = { sWidth.value = it },
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
                    value = sHeight.value,
                    onValueChange = { sHeight.value = it },
                    unit = "mm"
                )
                
                if (viewModel.selectedCabinetId.value == -1L) {
                    OutlinedButton(
                        onClick = {
                            navController.navigate(CabinetListScreenDestination.route+"/edit")
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color.Blue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("캐비닛 스펙 추가")
                    }
                } // 캐비닛 정보 선택 구간
                else {
                    Box {
                        Column {
                            OutlinedButton(
                                onClick = {
                                    navController.navigate(CabinetListScreenDestination.route)
                                },
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(2.dp, Color.Blue),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Color.Blue
                                ),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("변경")
                            }
                            Text(text="캐비닛 스펙")
                            Text(text = cabinetState.cabinet.name)
                            Text(text = "${cabinetState.cabinet.cabinetWidth} mm")
                            Text(text = "${cabinetState.cabinet.cabinetHeight} mm")
                            Text(text = "${cabinetState.cabinet.moduleColCount}X${cabinetState.cabinet.moduleRowCount}")
                        }
                    }
                } // 캐비닛 변경 버튼 else문

                Row {
                    Button(onClick = { navController.popBackStack() }) {
                        Text(text = "취소")
                    }
                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                viewModel.saveItem(
                                    name = sName.value,
                                    width = sWidth.value.toDouble(),
                                    height = sHeight.value.toDouble(),
                                    bitmap = imageBitmap,
                                    modelId = viewModel.selectedCabinetId.value
                                )
                                navController.popBackStack()
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "입력 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                     },
                        enabled=allFieldsNotEmpty) {
                        Text(text = "저장 ${viewModel.selectedCabinetId.value}")
                    }
                }
            }// 화면 전체 컬럼 끝
        }// 화면 전체 박스 끝
    }
}