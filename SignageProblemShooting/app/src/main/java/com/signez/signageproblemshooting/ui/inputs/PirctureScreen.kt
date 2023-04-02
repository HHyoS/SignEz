package com.signez.signageproblemshooting.ui.inputs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.pickers.ImagePicker
import com.signez.signageproblemshooting.pickers.loadImageMetadata
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
import com.signez.signageproblemshooting.ui.components.FocusBlock
import com.signez.signageproblemshooting.ui.components.IntentButton
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.theme.OneBGDarkGrey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pytorch.Module
import java.io.File

object PictureScreenDestination : NavigationDestination {
    override val route = "PictureScreen"
    override val titleRes = "SignEz_Picture"
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PictureAnalysis(
    activity: Activity,
    dispatchTakePictureIntent: (Activity, PictureViewModel, Int) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: PictureViewModel,
    analysisViewModel: AnalysisViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    var imageBitmap by remember { mutableStateOf<Bitmap>(bitmap) }
    var imageTitle by remember { mutableStateOf("-") }
    var imageSize by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    val file = File(viewModel.imageUri.value.toString())
    var contentUri: Uri = Uri.EMPTY
    val REQUEST_DETECT_PHOTO: Int = 101

    //test start
    var mModule: Module? = null
    var mWidth: Int = 2
    var mHeight: Int = 2
    var mmWidth: Int = 3
    var mmHeight: Int = 5
    var mImgScaleX = 0f
    var mImgScaleY = 0f
    var mIvScaleX = 0f
    var mIvScaleY = 0f
    var mStartX = 0f
    var mStartY = 0f
    var imageUri by remember { mutableStateOf(contentUri) }
    var rec : Rect? = null

    //test end
    if (viewModel.imageUri.value != Uri.EMPTY) {
        // content uri가 아니면 content uri로 바꿔줌.
        if (!viewModel.imageUri.value.toString().contains("content")) {
            contentUri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } else {
            contentUri = viewModel.imageUri.value
        }
        analysisViewModel.videoContentUri.value = Uri.EMPTY
        analysisViewModel.imageContentUri.value = contentUri
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
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
//                title = PictureScreenDestination.titleRes,
                title = "사진 분석",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomDoubleFlatButton(
                leftTitle = "취소",
                rightTitle = "분석하기",
                isLeftUsable = true,
                isRightUsable = true,
                leftOnClickEvent = onNavigateUp,
                rightOnClickEvent = {

                    // test

                    if(contentUri == Uri.EMPTY){
                        Toast.makeText(context,"사진을 등록 후 진행해주세요.", Toast.LENGTH_SHORT).show()
                    } else if (analysisViewModel.signageId.value < 1) {
                        Toast.makeText(context,"사이니지를 선택 후 진행해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        openImageCropActivity(context, mWidth, mHeight, mmWidth, mmHeight, REQUEST_DETECT_PHOTO, analysisViewModel.signageId.value, contentUri)
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
            Column() {
                Spacer(modifier = modifier.padding(5.dp))

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
//                                    .fillMaxHeight(0.4f)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(color = OneBGDarkGrey)
                            )
                        }
                        Text(
                            text = "분석할 사진을 추가해 주세요.",
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
                                .onSizeChanged { ImageSize ->
                                    val width = ImageSize.width
                                    val height = ImageSize.height
                                    Log.d("Image Size", "width: $width, height: $height")
                                }
                        )
                        rec = null
                        mWidth = LocalConfiguration.current.screenWidthDp
                        mHeight = LocalConfiguration.current.screenHeightDp
                        val uri = Uri.parse(contentUri.toString()) // 실제 Uri 주소를 사용하여 초기화합니다.
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        // 사진 진짜크기
                        BitmapFactory.decodeStream(
                            activity.contentResolver.openInputStream(uri),
                            null,
                            options
                        )
                        mmWidth = options.outWidth
                        mmHeight = options.outHeight
                        Log.d("here","error")

//                        imageBitmap?.let {
//                            Image(
//                                bitmap = it.asImageBitmap(),
//                                contentDescription = "Picture frame",
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
////                                    .fillMaxHeight(0.4f)
//                                    .clip(RoundedCornerShape(15.dp))
//                                    .background(color = MaterialTheme.colors.onSurface)
//                            )
//                        }
                    }
                }

                FocusBlock(
                    title = "사진정보",
                    subtitle = "제목 : $imageTitle",
                    infols = listOf("용량 : $imageSize byte"),
                    buttonTitle = "입력",
                    isbuttonVisible = false,
                    buttonOnclickEvent = {},
                    modifier = Modifier,
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        ImagePicker(onImageSelected = { address ->
                            viewModel.imageUri.value = Uri.parse(address)
                            imageBitmap = bitmap
                        })
                    }

                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        IntentButton(title = "카메라") {
                            dispatchTakePictureIntent(activity, viewModel, 2)
                        }

                    }
                }

            }
        }
    }
} // 최외곽 컬럼 끝
