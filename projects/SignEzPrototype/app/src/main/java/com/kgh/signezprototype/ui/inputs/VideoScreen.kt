package com.kgh.signezprototype.ui.inputs

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.analysis.getFrames
import com.kgh.signezprototype.pickers.VideoPicker
import com.kgh.signezprototype.pickers.getVideoTitle
import com.kgh.signezprototype.pickers.loadVideoMetadata
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.reflect.KFunction1

object VideoScreenDestination : NavigationDestination {
    override val route = "VideoScreen"
    override val titleRes = "SignEz_Video"
}

@Composable
fun VideoAnalysis(
    activity:Activity,
    dispatchTakeVideoIntent: (Activity, VideoViewModel) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: VideoViewModel
) {
    val defaultBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var videoTitle by remember { mutableStateOf("") }
    var videoLength by remember { mutableStateOf(0L) }
    var videoSize by remember { mutableStateOf(0L) }
    var videoFrame by remember { mutableStateOf(defaultBitmap) }
    var tempUri by remember { mutableStateOf(Uri.EMPTY) }
    var last: MutableState<String> = remember { mutableStateOf("") }

    val getVideoThumbnail: (Uri) -> Bitmap? = { uri ->
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        retriever.getFrameAtTime()
    }

    val loadVideoThumbnail = {
        if (viewModel.videoUri.value != Uri.EMPTY) {
            coroutineScope.launch {
                val bitmap = withContext(Dispatchers.IO) { getVideoThumbnail(viewModel.videoUri.value) }
                if (bitmap != null ) {
                    videoFrame = bitmap
                }
            }
        }
    }

    val loadVideoMetadata = {
        if (viewModel.videoUri.value != Uri.EMPTY && !viewModel.videoUri.value.toString().contains("content")) {
            val file = File(viewModel.videoUri.value.toString())
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            coroutineScope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    loadVideoMetadata(contentUri, context)
                }
                videoTitle = getVideoTitle(contentUri, context)
                videoLength = metadata.second.toLong() // ms
                videoSize = metadata.third.toLong() //  byte, val megabytes = bytes.toDouble() / (1024 * 1024)
            }
        }
        loadVideoThumbnail()
    }

    if (viewModel.videoUri.value != Uri.EMPTY && !viewModel.videoUri.value.equals(tempUri)) {
        tempUri = viewModel.videoUri.value
        loadVideoMetadata()
    }

    Scaffold(
        topBar = {
            SignEzTopAppBar(
                title = VideoScreenDestination.titleRes,
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ){ innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column( // 예는 정렬 evenly나 spacebetween 같은거 가능
            ) {
                Text(
                    text = "영상 분석",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Row {
                        VideoPicker(onVideoSelected = { address ->
                            viewModel.videoUri.value = Uri.parse(address)
                            videoFrame = defaultBitmap
                        },last = last)
                    }

                    Row {
                        OutlinedButton(
                            onClick = { last.value = "take"; dispatchTakeVideoIntent(activity,viewModel) },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("동영상 촬영")
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.videoUri.value = Uri.EMPTY
                                videoFrame = defaultBitmap
                            },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier.padding(16.dp)
                        ){
                            Text("Clear")
                        }
                    }

                    Log.d("compare","$videoFrame $viewModel.videoUri $tempUri")
                    if (!videoFrame.sameAs(defaultBitmap) && !viewModel.videoUri.value.toString().contains("content") && last.value == "take") {
                        Log.d("compare","$videoFrame $defaultBitmap")
                        val frames: MutableList<Bitmap> = getFrames(context,viewModel.videoUri.value,400,400,5)
                        println(frames.size)
                        Log.d("asasas","${frames.size}")
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(BorderStroke(width = 4.dp, color = Color.Black))
                                    .height(400.dp)
                            ) {
                                Image(
                                    bitmap = videoFrame.asImageBitmap(),
                                    contentDescription = "Video frame",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.Center)
                                        .clickable(onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.setDataAndType(viewModel.videoUri.value, "video/*")
//                                        intent.putExtra("loop", true) // 비디오 반복재생 설정
//                                        intent.putExtra("position", 5000) ms 단위로 비디오 시작점 지정
//                                        intent.putExtra("control", false) // 재생여부등 기본긴으 컨트롤러 키기
//                                        intent.putExtra("quality", "1080p") 화질 조정정                                        startActivity(context,intent,null)
                                        })
                                )
                            }
                            Text(text = "영상 제목 : $videoTitle")
                            Text(text = "영상 길이 : $videoLength ms")
                            Text(text = "영상 크기 : $videoSize byte")
                        }
                    }
                }
            }
        }//최외곽 컬럼 끝
        
    }

}