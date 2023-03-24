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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import com.kgh.signezprototype.ui.inputs.dispatchTakePictureIntent
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGDarkGrey
import com.kgh.signezprototype.ui.theme.OneBGGrey
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
    signageId:Long = -1,
    activity: Activity,
    viewModel: SignageDetailViewModel
    ) {
// 로컬 뷰모델 2
//    val viewModel = LocalViewModelStoreOwner.current?.let {
//            viewModel<SignageDetailViewModel>(
//            viewModelStoreOwner = it,
//            factory = AppViewModelProvider.Factory
//        )
//    }

    val coroutineScope = rememberCoroutineScope()
    var bitmap:Bitmap? = null
    var imageBitmap:Bitmap? = null
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY
    val sWidth = remember { mutableStateOf("") } // 사이니지
    val sHeight = remember { mutableStateOf("") } // 사이니지
    val sName = remember { mutableStateOf("") }
//    CompositionLocalProvider(LocalSignageDetailViewModel provides viewModel!!) { 로컬 뷰모델 3
        val cabinetState = produceState(initialValue = null as Cabinet?, producer = {
            value = viewModel.getCabinet(signageId)
        })
        val cabinet = cabinetState.value
        val signageState = produceState(initialValue = null as Signage?, producer = {
            value = viewModel.getSignage(signageId)
        })
        val signage = signageState.value

        if (signage != null) {
            sName.value = signage.name
            sHeight.value = signage.height.toString()
            sWidth.value = signage.width.toString()
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
            imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
        }

        androidx.compose.material.Scaffold(
            modifier = Modifier
                .clickable(onClick = { focusManager.clearFocus() })
                .background(OneBGGrey),
            topBar = {
                SignEzTopAppBar(
                    title = "사이니지 정보",
                    canNavigateBack = true
                )
            },
        ) { innerPadding ->
            Column {
                Spacer(modifier = Modifier.padding(innerPadding))
                if (viewModel.imageUri.value == Uri.EMPTY) {
                    signage?.repImg?.let { byteArray ->
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
                        if (it != null) {
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
                }
                Row {
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

                    Box {
                        Column {
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
                                Text("선택")
                            }
                            Text(text="캐비닛 스펙")
                            if (cabinet != null) {
                                Text(text = cabinet.name)
                                Text(text = "${cabinet.cabinetWidth} mm")
                                Text(text = "${cabinet.cabinetHeight} mm")
                                Text(text = "${cabinet.moduleColCount}X${cabinet.moduleRowCount}")
                            }

                        }
                    }
                Row {
                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                if (signage != null) {
                                    if (imageBitmap == null ){
                                        viewModel.updateRecord(
                                            name = sName.value,
                                            width = sWidth.value.toDouble(),
                                            height = sHeight.value.toDouble(),
                                            bitmap = null,
                                            signage = signage
                                        )
                                    }
                                    else {
                                        viewModel.updateRecord(
                                            name = sName.value,
                                            width = sWidth.value.toDouble(),
                                            height = sHeight.value.toDouble(),
                                            bitmap = imageBitmap,
                                            signage = signage
                                        )
                                    }

                                    navController.navigate(SignageListScreenDestination.route)
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
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
                    }) {
                        Text(text = "삭제")
                    }
                }
            } // 캐비닛 변경 버튼 else문

        }// 화면 전체 컬럼 끝

//    } 로컬 뷰모델 4
    // Use viewModel here
}