package com.kgh.signezprototype.ui.signage

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.fields.CustomTextInput
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.pickers.ImagePicker
import com.kgh.signezprototype.ui.inputs.dispatchTakePictureIntent
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGDarkGrey
import com.kgh.signezprototype.ui.theme.OneBGGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object DetailCabinetScreenDestination : NavigationDestination {
    override val route = "DetailCabinet"
    override val titleRes = "캐비닛 정보"
}

@Composable
fun CDetail(
    navController: NavController,
    cabinetId:Long = -1,
    activity: Activity,
    viewModel: CabinetDetailViewModel
    ) {

    val coroutineScope = rememberCoroutineScope()
    var bitmap:Bitmap? = null
    var imageBitmap:Bitmap? = null
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY
    val cabinetWidth = remember { mutableStateOf("") } // 사이니지
    val cabinetHeight = remember { mutableStateOf("") } // 사이니지
    val cabinetName = remember { mutableStateOf("") }
    val colModuleCount = remember { mutableStateOf("") }
    val rowModuleCount = remember { mutableStateOf("") }
    val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
        value = viewModel.getCabinet(cabinetId)
    })
    val cabinet = cabinetState.value

    val imageLoadingScope = CoroutineScope(Dispatchers.Main)
    // Load the image asynchronously using coroutines
    fun loadImageAsync(context: Context, contentUri: Uri) {
        imageLoadingScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Load the image bitmap on a background thread
                    imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
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

    if (cabinet != null) {
        cabinetWidth.value = cabinet.cabinetWidth.toString()
        cabinetHeight.value = cabinet.cabinetHeight.toString()
        cabinetName.value = cabinet.name
        colModuleCount.value = cabinet.moduleColCount.toString()
        rowModuleCount.value = cabinet.moduleRowCount.toString()
    }

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
//        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
        loadImageAsync(context, contentUri)
    }

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .clickable(onClick = { focusManager.clearFocus() })
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "캐비닛 정보",
                canNavigateBack = true
            )
        },
    ) { innerPadding ->
        Column {
            Spacer(modifier = Modifier.padding(innerPadding))
            if (viewModel.imageUri.value == Uri.EMPTY) {
                cabinet?.repImg?.let { byteArray ->
                    byteArray.let {
                        bitmap = byteArrayToBitmap(it)
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Signage Image",
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.3f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = OneBGDarkGrey)
                        )
                    }
                }
            } else {
                imageBitmap.let {
                    it?.let { it1 ->
                        Image(
                            bitmap = it1.asImageBitmap(),
                            contentDescription = "rep Image",
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.3f)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = OneBGDarkGrey)
                        )
                    }
                }
            }
            Column {
                ImagePicker(onImageSelected = { address ->
                    imageBitmap = bitmap
                    viewModel.imageUri.value = Uri.parse(address)
                })

                OutlinedButton(
                    onClick = { dispatchTakePictureIntent(activity, viewModel=viewModel,type=22222) },
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
                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            Log.d("gogo","${imageBitmap == null}")
                            if (cabinet != null) {
                                if (imageBitmap == null ){
                                    viewModel.updateRecord(
                                        name = cabinetName.value,
                                        width = cabinetWidth.value.toDouble(),
                                        height = cabinetHeight.value.toDouble(),
                                        bitmap = null,
                                        cabinet = cabinet,
                                        colNum = colModuleCount.value.toInt(),
                                        rowNum = rowModuleCount.value.toInt(),
                                    )
                                }
                                else {
                                    viewModel.updateRecord(
                                        name = cabinetName.value,
                                        width = cabinetWidth.value.toDouble(),
                                        height = cabinetHeight.value.toDouble(),
                                        bitmap = imageBitmap,
                                        cabinet = cabinet,
                                        colNum = colModuleCount.value.toInt(),
                                        rowNum = rowModuleCount.value.toInt(),
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Log.d("finde",e.toString())
                                Toast.makeText(context, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    navController.popBackStack() }) {
                    Text(text = "확인")
                }

                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            if (cabinet != null) {
                                if (!viewModel.delete(cabinet = cabinet)) {
                                    Toast.makeText(context, "연관된 사이니지가 있습니다.", Toast.LENGTH_SHORT).show()
                                }
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "연관된 사이니지가 있습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) {
                    Text(text = "삭제")
                }
            }
        } // 컬럼 끝

    }// 화면 전체 컬럼 끝
    
}