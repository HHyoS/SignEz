package com.signez.signageproblemshooting.ui.signage

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.fields.CustomTextInput
import com.signez.signageproblemshooting.fields.EditNumberField
import com.signez.signageproblemshooting.pickers.ImagePicker
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
import com.signez.signageproblemshooting.ui.components.FocusBlock
import com.signez.signageproblemshooting.ui.components.IntentButton
import com.signez.signageproblemshooting.ui.inputs.dispatchTakePictureIntent
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object DetailSignageScreenDestination : NavigationDestination {
    override val route = "DetailSignage"
    override val titleRes = "사이트 정보"
}

// 로컬에서 뷰모델 사용하기1

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SDetail(
    modifier: Modifier = Modifier,
    navController: NavController,
    signageId: Long = -1,
    activity: Activity,
    viewModel: SignageDetailViewModel,
    onNavigateUp: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bitmap: Bitmap? = null
    var imageBitmap by remember { mutableStateOf<Bitmap?>(bitmap) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY
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
                    Glide.with(context)
                        .asBitmap()
                        .load(contentUri)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                // Save the bitmap here
                                imageBitmap = resource
                            }
                        })
                    // Load the image bitmap on a background thread
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
    if (viewModel.imageUri.value != Uri.EMPTY) { //        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
        loadImageAsync(context, contentUri)
    }

    Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "사이트 정보",
                canNavigateBack = true,
                navigateUp = onNavigateUp
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
                .fillMaxWidth()
                .fillMaxHeight(0.91f)
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column() {
                Spacer(modifier = modifier.padding(5.dp))
                if (viewModel.imageUri.value == Uri.EMPTY) {
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        signage?.repImg?.let { byteArray ->
                            GlideImage(
                                model = byteArray,
                                contentDescription = "글라이드",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(color = MaterialTheme.colors.onSurface)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        GlideImage(
                            model = contentUri,
                            contentDescription = "글라이드",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = MaterialTheme.colors.onSurface)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        ImagePicker(onImageSelected = { address ->
//                            imageBitmap = bitmap
                            Glide.with(context)
                                .asBitmap()
                                .load(contentUri)
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        // Save the bitmap here
                                        imageBitmap = resource
                                    }
                                })
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
                                type = 2222
                            )
                        }
                    }
                }
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
                        Row {
                            CustomTextInput(
                                value = viewModel.sName.value,
                                onValueChange = { it -> viewModel.sName.value = it },
                                placeholder = "사이트 이름"
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
                if (cabinet != null) {
                    if (viewModel.newCabinetId.value > -1) {
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
                            )
                        }

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
                        )
                    }
                }
                FocusBlock(
                    title = "캐비닛 배치",
                    subtitle = "기본 배치를 사용하지 않고 직접 배치",
                    buttonTitle = "배치",
                    isbuttonVisible = true,
                    buttonOnclickEvent = {
                        if (signage != null) {
                            if (cabinet != null) {
                                navController.navigate(
                                    BlockLayoutDestination.route+"/${signage.id}/${cabinet.id}"
                                )
                            }
                        }
                    },
                )
            }
        } // 캐비닛 변경 버튼 else문

    }// 화면 전체 컬럼 끝
//    } 로컬 뷰모델 4
    // Use viewModel here
}