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
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.fields.CustomTextInput
import com.signez.signageproblemshooting.fields.EditNumberField
import com.signez.signageproblemshooting.pickers.ImagePicker
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
import com.signez.signageproblemshooting.ui.components.FocusBlock
import com.signez.signageproblemshooting.ui.components.IntentButton
import com.signez.signageproblemshooting.ui.components.WhiteButton
import com.signez.signageproblemshooting.ui.inputs.dispatchTakePictureIntent
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.theme.OneBGDarkGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object AddSignageDestination : NavigationDestination {
    override val route = "AddSignage"
    override val titleRes = "Add Signage"
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddSignageScreen(
    modifier: Modifier = Modifier, activity: Activity, viewModel: SignageViewModel,
    navController: NavHostController,
    onNavigateUp: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var imageBitmap by remember { mutableStateOf<Bitmap>(bitmap) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY
    val allFieldsNotEmpty = (
            viewModel.sName.value.isNotEmpty() &&
                    viewModel.sWidth.value.isNotEmpty() &&
                    viewModel.sHeight.value.isNotEmpty() &&
                    viewModel.selectedCabinetId.value > 0
            )
    val cabinetState by viewModel.getCabinet().collectAsState()

    val imageLoadingScope = CoroutineScope(Dispatchers.Main)

    // Load the image asynchronously using coroutines
    fun loadImageAsync(context: Context, contentUri: Uri) {

        imageLoadingScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Load the image bitmap on a background thread
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
                } catch (e: Exception) {
                    // Handle any errors that occur while loading the image
                    Log.e("Error", "Error loading image", e)
                }
            }
        }
    }

    if (viewModel.imageUri.value != Uri.EMPTY) {
        // content uri가 아니면 content uri로 바꿔줌.
        contentUri = if (!viewModel.imageUri.value.toString().contains("content")) {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } else {
            viewModel.imageUri.value
        }
    }

    if (viewModel.imageUri.value != Uri.EMPTY) {
        loadImageAsync(context, contentUri)
    }

    Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "새 사이트 추가",
                canNavigateBack = true,
                navigateUp = onNavigateUp
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
                            if(viewModel.selectedCabinetId.value < 1L) throw Exception("Cabinet Not Selected!")
                            viewModel.saveItem(
                                bitmap = imageBitmap,
                                modelId = viewModel.selectedCabinetId.value
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
                .fillMaxWidth()
                .fillMaxHeight(0.91f)
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
                            text = "사이트 사진을 추가해 주세요.",
                            modifier = Modifier.align(Alignment.Center), // Adjust the alignment as needed
                            style = TextStyle(color = Color.Black), // Customize the text style
                        )
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
                                onValueChange = { viewModel.sName.value = it },
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

                if (viewModel.selectedCabinetId.value == -1L) {
                    WhiteButton(title = "캐비닛 스펙 추가하기", isUsable = true) {
                        navController.navigate(CabinetListScreenDestination.route + "/add")
                    }
                } // 캐비닛 정보 선택 구간
                else {
                    FocusBlock(
                        title = "캐비닛 스펙",
                        subtitle = cabinetState.cabinet.name,
                        infols = listOf(
                            "너비 : ${cabinetState.cabinet.cabinetWidth} mm",
                            "높이 : ${cabinetState.cabinet.cabinetHeight} mm",
                            "모듈 : ${cabinetState.cabinet.moduleColCount}X${cabinetState.cabinet.moduleRowCount}"
                        ),
                        buttonTitle = "변경",
                        isbuttonVisible = true,
                        buttonOnclickEvent = { navController.navigate(CabinetListScreenDestination.route + "/add") },
                    )
                }
            } // 캐비닛 변경 버튼 else문
        }// 화면 전체 컬럼 끝
    }// 화면 전체 박스 끝
}
