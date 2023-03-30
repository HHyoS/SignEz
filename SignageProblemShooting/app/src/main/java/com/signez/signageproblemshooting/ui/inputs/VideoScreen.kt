package com.signez.signageproblemshooting.ui.inputs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.pickers.VideoPicker
import com.signez.signageproblemshooting.pickers.getVideoTitle
import com.signez.signageproblemshooting.pickers.loadVideoMetadata
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
import java.io.File

object VideoScreenDestination : NavigationDestination {
    override val route = "VideoScreen"
    override val titleRes = "SignEz_Video"
}

@Composable
fun VideoAnalysis(
    activity: Activity,
    dispatchTakeVideoIntent: (Activity, VideoViewModel) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: VideoViewModel,
    analysisViewModel: AnalysisViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    var imageBitmap by remember { mutableStateOf<Bitmap>(bitmap) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var videoTitle by remember { mutableStateOf("-") }
    var videoLength by remember { mutableStateOf(0L) }
    var videoSize by remember { mutableStateOf(0L) }
    var videoFrame by remember { mutableStateOf(bitmap) }
    var rec : Rect? = null
    val getVideoThumbnail: (Uri) -> Bitmap? = { uri ->
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        retriever.getFrameAtTime()
    }

    val loadVideoThumbnail = {
        if (viewModel.videoUri.value != Uri.EMPTY) {
            coroutineScope.launch {
                val bitmap =
                    withContext(Dispatchers.IO) { getVideoThumbnail(viewModel.videoUri.value) }
                if (bitmap != null) {
                    videoFrame = bitmap
                }
            }
        }
    }

    val loadVideoMetadata = {
        if (viewModel.videoUri.value != Uri.EMPTY) {
            var contentUri: Uri
            if (!viewModel.videoUri.value.toString().contains("content")) {
                val file = File(viewModel.videoUri.value.toString())
                contentUri =
                    FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            } else {
                contentUri = viewModel.videoUri.value
            }

            coroutineScope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    loadVideoMetadata(contentUri, context)
                }
                videoTitle = getVideoTitle(contentUri, context)
                videoLength = metadata.second.toLong() // ms
                videoSize =
                    metadata.third.toLong() //  byte, val megabytes = bytes.toDouble() / (1024 * 1024)
            }
            analysisViewModel.imageContentUri.value = Uri.EMPTY
            analysisViewModel.videoContentUri.value = contentUri
        }
        loadVideoThumbnail()
    }

    if (viewModel.videoUri.value != Uri.EMPTY) {
        loadVideoMetadata()
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
//                title = VideoScreenDestination.titleRes,
                title = "영상 분석",
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
                /* 분석하기 이벤트를 넣으면 됨 */
                    // .currentDestination?.let { navController.popBackStack(it.id , true) }
                    navController.popBackStack()
                    navController.navigate(ResultsHistoryDestination.route)
                    navController.navigate(ResultGridDestination.route)
                   // openErrorDetectActivity(context,rec!!,contentUri)
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
                Spacer(modifier = modifier.padding(5.dp))
                if (viewModel.videoUri.value == Uri.EMPTY) {
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
                            text = "분석할 영상을 추가해 주세요.",
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
                                bitmap = videoFrame.asImageBitmap(),
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

                FocusBlock(
                    title = "영상정보",
                    subtitle = "제목 : $videoTitle",
                    infols = listOf("길이 : $videoLength ms", "용량 : $videoSize byte"),
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
                        VideoPicker(onVideoSelected = { address ->
                            viewModel.videoUri.value = Uri.parse(address)
                            videoFrame = bitmap
                        })
                    }

                    Column(
                        modifier = Modifier.weight(0.5f)
                    ) {
                        IntentButton(title = "카메라") {
                            dispatchTakeVideoIntent(activity, viewModel)
                        }
                    }
                }
            }//Column



//            if (!videoFrame.sameAs(bitmap)) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .border(BorderStroke(width = 4.dp, color = Color.Black))
//                            .height(400.dp)
//                    ) {
//                        Image(
//                            bitmap = videoFrame.asImageBitmap(),
//                            contentDescription = "Video frame",
//                            contentScale = ContentScale.FillBounds,
//                            modifier = Modifier
//                                .fillMaxHeight()
//                                .align(Alignment.Center)
//                                .clickable(onClick = {
//                                    val intent = Intent(Intent.ACTION_VIEW)
//                                    intent.setDataAndType(
//                                        viewModel.videoUri.value,
//                                        "video/*"
//                                    )
//                                        intent.putExtra("loop", true) // 비디오 반복재생 설정
//                                        intent.putExtra("position", 5000) ms 단위로 비디오 시작점 지정
//                                        intent.putExtra("control", false) // 재생여부등 기본긴으 컨트롤러 키기
//                                        intent.putExtra("quality", "1080p") 화질 조정정                                        startActivity(context,intent,null)
//                                })
//                        )
//                    }
//                }

        }
    }
}//최외곽 컬럼 끝
