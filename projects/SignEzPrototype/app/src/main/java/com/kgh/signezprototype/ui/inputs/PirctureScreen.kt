package com.kgh.signezprototype.ui.inputs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.pickers.ImagePicker
import com.kgh.signezprototype.pickers.loadImageMetadata
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.reflect.KFunction1

object PictureScreenDestination : NavigationDestination {
    override val route = "PictureScreen"
    override val titleRes = "SignEz_Picture"
}

@Composable
fun PictureAnalysis(
    activity:Activity,
    dispatchTakePictureIntent: (Activity, PictureViewModel) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: PictureViewModel
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageTitle by remember { mutableStateOf("") }
    var imageSize by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY

    if (viewModel.imageUri.value != Uri.EMPTY) {
        // content uri가 아니면 content uri로 바꿔줌.
        if (!viewModel.imageUri.value.toString().contains("content")) {
            contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }
        else {
            contentUri = viewModel.imageUri.value
        }
    }

//    var tempUri by remember { mutableStateOf(Uri.EMPTY) }
    // image의 제목, 크기 등 메타데이터 가져옴
    val loadImageMetadata = {
        if (viewModel.imageUri.value != Uri.EMPTY) {
            coroutineScope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    loadImageMetadata(contentUri, context)
                }
                imageTitle = metadata.first
                imageSize = metadata.second // bytes
            }
        }
    }

    if (viewModel.imageUri.value != Uri.EMPTY) {
        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
        loadImageMetadata()
    }

    Scaffold(
        topBar = {
            SignEzTopAppBar(
                title = PictureScreenDestination.titleRes,
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ){ innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column( // 예는 정렬 evenly나 spacebetween 같은거 가능
            ) {
                Text(
                    text = "사진 분석",
                    modifier = Modifier.align(Alignment.Start),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Row {
                        ImagePicker(onImageSelected = { address ->
                            viewModel.imageUri.value = Uri.parse(address)
                            imageBitmap = null
                        })
                    }

                    Row {
                        OutlinedButton(
                            onClick = { dispatchTakePictureIntent(activity, viewModel) },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("사진 촬영")
                        }

                        OutlinedButton(
                            onClick = {
                                imageBitmap = null
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

                    Log.d("compare", viewModel.imageUri.value.toString())
                    //imageBitmap != null && last.value == "take"
                    if (viewModel.imageUri.value != Uri.EMPTY ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(width = 4.dp, color = Color.Black))
                                    .height(400.dp)
                            ) {
                                imageBitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Picture frame",
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier
                                    )
                                }
                            }
                            Text(text = "이미지 제목 : $imageTitle")
                            Text(text = "이미지 크기 : $imageSize byte")
                        }
                    }
                }
            }
        } // 최외곽 컬럼 끝
    }
}