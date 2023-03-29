package com.signez.signageproblemshooting.ui.inputs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.signature.ObjectKey
import com.signez.signageproblemshooting.ErrorDetectActivity
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.pickers.ImagePicker
import com.signez.signageproblemshooting.pickers.loadImageMetadata
import com.signez.signageproblemshooting.service.PrePostProcessor
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.analysis.ResultGridDestination
import com.signez.signageproblemshooting.ui.analysis.ResultsHistoryDestination
import com.signez.signageproblemshooting.ui.components.BottomDoubleFlatButton
import com.signez.signageproblemshooting.ui.components.FocusBlock
import com.signez.signageproblemshooting.ui.components.IntentButton
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.theme.OneBGDarkGrey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import kotlin.reflect.KFunction1

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
    var mWidth: Int = 1
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
                    if (contentUri == Uri.EMPTY)
                        Toast.makeText(context, "사진을 등록 후 진행해주세요.", Toast.LENGTH_SHORT).show()
                    else {
                        mImgScaleX = mmWidth.toFloat() / PrePostProcessor.mInputWidth
                        mImgScaleY = mmHeight.toFloat() / PrePostProcessor.mInputHeight
                        mIvScaleX = (if (mmWidth > mmHeight) mWidth
                            .toFloat() / mmWidth else mHeight
                            .toFloat() / mmHeight)
                        mIvScaleY = (if (mmHeight > mmWidth) mHeight
                            .toFloat() / mmHeight else mWidth
                            .toFloat() / mmWidth)
                        mStartX = (mWidth - mIvScaleX * mmWidth) / 2
                        mStartY = (mHeight - mIvScaleY * mmHeight) / 2

                        if (mModule == null) {
                            val temp = ErrorDetectActivity()
                            mModule = temp.getModel()
                        }
                        val thread = object : Thread() {
                            override fun run() {
                                Log.d("hyoyo", "1")
                                val resizedBitmap = Bitmap.createScaledBitmap(
                                    (BitmapFactory.decodeStream(
                                        activity.contentResolver.openInputStream(
                                            contentUri
                                        )
                                    ))!!,
                                    PrePostProcessor.mInputWidth,
                                    PrePostProcessor.mInputHeight,
                                    true
                                )
                                Log.d("hyoyo", "{")
                                val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                                    resizedBitmap,
                                    PrePostProcessor.NO_MEAN_RGB,
                                    PrePostProcessor.NO_STD_RGB
                                )
                                Log.d("hyoyo", "3")
                                val outputTuple =
                                    mModule!!.forward(IValue.from(inputTensor)).toTuple()
                                val outputTensor = outputTuple[0].toTensor()
                                val outputs = outputTensor.dataAsFloatArray
                                Log.d("hyoyo", "4")
                                val results = PrePostProcessor.outputsToNMSPredictions(
                                    outputs,
                                    mImgScaleX,
                                    mImgScaleY,
                                    mIvScaleX,
                                    mIvScaleY,
                                    mStartX,
                                    mStartY
                                )

                                if (results != null) {
                                    for (r in results!!) {
                                        Log.d(
                                            "test",
                                            "${r.classIndex} - ${r.rect.top} @ ${r.rect.left} @ ${r.rect.right} @ " +
                                                    "${r.rect.bottom} @ ${r.score}"
                                        )
                                    }
                                }
                            }
                        }
                        thread.start()
                    }
                    //
                    /* 분석하기 이벤트를 넣으면 됨 */
//                    navController.currentDestination?.let { navController.popBackStack(it.id , true) }

                    navController.popBackStack()
                    navController.navigate(ResultsHistoryDestination.route)
                    navController.navigate(ResultGridDestination.route)
                    openErrorDetectActivity(
                        context,
                        REQUEST_DETECT_PHOTO,
                        analysisViewModel.signageId.value,
                        analysisViewModel.imageContentUri.value
                    )
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
                                },
                        )

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
//
//                Column( // 예는 정렬 evenly나 spacebetween 같은거 가능
//                ) {
//                    Text(
//                        text = "사진 분석",
//                        modifier = Modifier.align(Alignment.Start),
//                        fontSize = 40.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Row (
//                        horizontalArrangement = Arrangement.SpaceAround
//                    ){
//                        Column (
//                            modifier = Modifier.weight(0.5f)
//                        ){
//                            ImagePicker(onImageSelected = { address ->
//                                viewModel.imageUri.value = Uri.parse(address)
//                                imageBitmap = bitmap
//                            })
//                        }
//
//                        Column (
//                            modifier = Modifier.weight(0.5f)
//                        ){
//                            IntentButton(title = "카메라") {
//                                dispatchTakePictureIntent(activity, viewModel,2)
//                            }
//
//                        }
//                    }

//                    Log.d("compare", viewModel.imageUri.value.toString())
//                    //imageBitmap != null && last.value == "take"
//                    if (viewModel.imageUri.value != Uri.EMPTY) {
//                        Column {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .border(BorderStroke(width = 4.dp, color = Color.Black))
//                                    .height(400.dp)
//                            ) {
//                                imageBitmap?.let {
//                                    Image(
//                                        bitmap = it.asImageBitmap(),
//                                        contentDescription = "Picture frame",
//                                        contentScale = ContentScale.FillBounds,
//                                        modifier = Modifier
//                                    )
//                                }
//                            }
//                            Text(text = "이미지 제목 : $imageTitle")
//                            Text(text = "이미지 크기 : $imageSize byte")
//                        }
//                    }
            }
        }
    }
} // 최외곽 컬럼 끝
