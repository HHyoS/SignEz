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
import androidx.compose.ui.layout.ContentScale
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
import com.kgh.signezprototype.ui.components.BottomDoubleFlatButton
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
object AddCabinetDestination : NavigationDestination {
    override val route = "AddCabinet"
    override val titleRes = "Add Cabinet"
}


@Composable
fun AddCabinetScreen(
    modifier: Modifier = Modifier, activity: Activity, viewModel: CabinetViewModel,
    navController: NavHostController
) {
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
    DisposableEffect(Unit) {
        viewModel.imageUri.value = Uri.EMPTY
        onDispose {} // Cleanup logic here, if needed
    }

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
                title = "새 캐비닛 추가",
                canNavigateBack = true
            )
        },
        bottomBar = {
            BottomDoubleFlatButton(
                leftTitle = "취소",
                rightTitle = "저장",
                isLeftUsable = true,
                isRightUsable = allFieldsNotEmpty,
                leftOnClickEvent = { navController.popBackStack() },
                rightOnClickEvent = {
                    coroutineScope.launch {
                        try {
                            viewModel.saveItem(
                                name = cabinetName.value,
                                width = cabinetWidth.value.toDouble(),
                                height = cabinetHeight.value.toDouble(),
                                colCount = colModuleCount.value.toInt(),
                                rowCount = rowModuleCount.value.toInt(),
                                bitmap = imageBitmap
                            )
                            navController.popBackStack()
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "입력 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT)
                                    .show()
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
                        imageBitmap.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "rep Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
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
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        imageBitmap.let {
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
                                type = 222
                            )
                        }
                    }


//                    OutlinedButton(
//                        onClick = {
//                            imageBitmap = bitmap
//                            viewModel.imageUri.value = Uri.EMPTY
//                        },
//                        shape = RoundedCornerShape(20.dp),
//                        border = BorderStroke(2.dp, Color.Blue),
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            backgroundColor = Color.White,
//                            contentColor = Color.Blue
//                        ),
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text("Clear")
//                    }
                }

                // 텍스트필드 박스
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
                                value = cabinetName.value,
                                onValueChange = { it -> cabinetName.value = it },
                                placeholder = "캐비닛 모델 명"
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
                                value = cabinetWidth.value,
                                onValueChange = { cabinetWidth.value = it },
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
                                value = cabinetHeight.value,
                                onValueChange = { cabinetHeight.value = it },
                                unit = "mm"
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }


//                Row {
//                    Button(onClick = { navController.popBackStack() }) {
//                        Text(text = "취소")
//                    }
//                    Button(onClick = {
//                        coroutineScope.launch {
//                            try {
//                                viewModel.saveItem(
//                                    name=cabinetName.value,
//                                    width=cabinetWidth.value.toDouble(),
//                                    height=cabinetHeight.value.toDouble(),
//                                    colCount=colModuleCount.value.toInt(),
//                                    rowCount=rowModuleCount.value.toInt(),
//                                    bitmap=imageBitmap)
//                                navController.popBackStack()
//                            } catch (e: Exception) {
//                                withContext(Dispatchers.Main) {
//                                    Toast.makeText(context, "입력 정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                    } },
//                        enabled= allFieldsNotEmpty) {
//                        Text(text = "저장")
//                    }
//                }
        }// 화면 전체 컬럼 끝
    }// 화면 전체 박스 끝
}