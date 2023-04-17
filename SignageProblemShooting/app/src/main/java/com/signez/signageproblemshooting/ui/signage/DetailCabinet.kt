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
import com.signez.signageproblemshooting.fields.CustomTextInput
import com.signez.signageproblemshooting.fields.EditNumberField
import com.signez.signageproblemshooting.pickers.ImagePicker
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
import com.signez.signageproblemshooting.ui.components.IntentButton
import com.signez.signageproblemshooting.ui.inputs.dispatchTakePictureIntent
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object DetailCabinetScreenDestination : NavigationDestination {
    override val route = "DetailCabinet"
    override val titleRes = "캐비닛 정보"
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CDetail(
    modifier: Modifier = Modifier,
    navController: NavController,
    cabinetId: Long = -1,
    activity: Activity,
    viewModel: CabinetDetailViewModel,
    onNavigateUp: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val bitmap: Bitmap? = null
    var imageBitmap by remember { mutableStateOf<Bitmap?>(bitmap) }
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
            contentUri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } else {
            contentUri = viewModel.imageUri.value
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
                title = "캐비닛 정보",
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
                            Log.d("gogo", "${imageBitmap == null}")
                            if (cabinet != null) {
                                if (imageBitmap == null) {
                                    viewModel.updateRecord(
                                        name = cabinetName.value,
                                        width = cabinetWidth.value.toDouble(),
                                        height = cabinetHeight.value.toDouble(),
                                        bitmap = null,
                                        cabinet = cabinet,
                                        colNum = colModuleCount.value.toInt(),
                                        rowNum = rowModuleCount.value.toInt(),
                                    )
                                } else {
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
                                Log.d("finde", e.toString())
                                Toast.makeText(context, "정보를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        navController.popBackStack()
                    }
                },
                rightOnClickEvent = {
                    coroutineScope.launch {
                        try {
                            if (cabinet != null) {
                                if (!viewModel.delete(cabinet = cabinet)) {
                                    Toast.makeText(context, "연관된 사이니지가 있습니다.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "연관된 사이니지가 있습니다.", Toast.LENGTH_SHORT)
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
                .fillMaxWidth()
                .fillMaxHeight(0.91f)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(modifier = modifier.padding(5.dp))
                if (viewModel.imageUri.value == Uri.EMPTY) {
                    Box(
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        cabinet?.repImg?.let { byteArray ->
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
                    ){
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
                                type = 22222
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
                        Row() {
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
        } // 컬럼 끝

    }// 화면 전체 컬럼 끝

}