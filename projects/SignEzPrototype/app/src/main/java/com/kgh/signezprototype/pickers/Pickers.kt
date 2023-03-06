package com.kgh.signezprototype.pickers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ImagePicker(onImageSelected: (videoUri: String) -> Unit, last:MutableState<String>) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf("") }
    var imageTitle by remember { mutableStateOf("") }
    var imageSize by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()

    val loadImageMetadata = {
        if (imageUri != "") {
            coroutineScope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    loadImageMetadata(Uri.parse(imageUri), context)
                }
                imageTitle = metadata.first
                imageSize = metadata.second // bytes
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//                imageBitmap?.let {
//                    onImageSelected(it)
//                }
                imageUri = uri.toString()
                onImageSelected(imageUri)
                loadImageMetadata()
                last.value = "gal"
            }
        }
    }

    Column {
        Row {
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    launcher.launch(intent)
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color.Blue),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Blue
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("사진 가져오기")
            }

            OutlinedButton(
                onClick = {
                    imageBitmap = null
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color.Blue),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Blue
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Clear")
            }
        }
        Log.d("ccc","${last.value}")
        if (last.value == "gal") {
            imageBitmap?.let {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(width = 4.dp, color = Color.Black))
                            .height(400.dp)
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.Center)
                        )
                    }
                    Text(text = "이미지 제목 : $imageTitle")
                    Text(text = "이미지 크기 : $imageSize byte")
                }
            } // image + 정보

        }

    }
}

@Composable
fun VideoPicker(onVideoSelected: (videoUri: String) -> Unit, last:MutableState<String>) {
    val defaultBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val context = LocalContext.current
    var videoUri by remember { mutableStateOf("") }
    var videoFrame by remember { mutableStateOf(defaultBitmap) }
    var videoTitle by remember { mutableStateOf("") }
    var videoLength by remember { mutableStateOf(0L) }
    var videoSize by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    val getVideoThumbnail: (Uri) -> Bitmap? = { uri ->
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        retriever.getFrameAtTime()
    }

    val loadVideoThumbnail = {
        if (videoUri.isNotEmpty()) {
            val uri = Uri.parse(videoUri)
            coroutineScope.launch {
                val bitmap = withContext(Dispatchers.IO) { getVideoThumbnail(uri) }
                if (bitmap != null) {
                    videoFrame = bitmap
                }
            }
        }
    }

    val loadVideoMetadata = {
        if (videoUri.isNotEmpty()) {
            val uri = Uri.parse(videoUri)
            coroutineScope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    loadVideoMetadata(uri, context)
                }
                videoTitle = getVideoTitle(uri, context)
                videoLength = metadata.second.toLong() // ms
                videoSize = metadata.third.toLong() //  byte, val megabytes = bytes.toDouble() / (1024 * 1024)
            }
        }
    }

    LaunchedEffect(videoUri) {
        loadVideoThumbnail()
        loadVideoMetadata()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        videoUri = uri.toString()
        onVideoSelected(videoUri)
        Log.d("VideoPicker", "Selected video: $uri")
        loadVideoThumbnail()
        last.value = "gal"
    }

    Column {
        Row {
            OutlinedButton(
                onClick = {
                    launcher.launch("video/mp4")
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color.Blue),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Blue
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("동영상 가져오기")
            }

            OutlinedButton(
                onClick = {
                    videoUri = ""
                    videoFrame = defaultBitmap
                          },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color.Blue),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Blue
                ),
                modifier = Modifier.padding(8.dp)
            ){
                Text("Clear")
            }
        }

        if (!videoFrame.sameAs(defaultBitmap) && last.value == "gal") {
            Log.d("compare","$videoFrame $defaultBitmap")
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
                    )
                }
                Text(text = "영상 제목 : $videoTitle")
                Text(text = "영상 길이 : $videoLength ms")
                Text(text = "영상 크기 : $videoSize byte")
            }
        }
    }
}

@SuppressLint("Range")
fun loadImageMetadata(uri: Uri, context: Context): Pair<String, Long> {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val title = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: ""
            val size = it.getLong(it.getColumnIndex(OpenableColumns.SIZE))
            return Pair(title, size)
        }
    }
    return Pair("", 0)
}

fun loadVideoMetadata(uri: Uri, context: Context): Triple<String, Long, Long> {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, uri)

    val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val duration = durationStr?.toLongOrNull() ?: 0L
    val size = getFileSizeFromUri(uri, context)

    retriever.release()

    return Triple(title, duration, size)
}

fun getFileSizeFromUri(uri: Uri, context: Context): Long {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.moveToFirst()
    val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
    val size = cursor?.getLong(sizeIndex ?: 0)
    cursor?.close()

    return size ?: 0
}

suspend fun getVideoTitle(uri: Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val title = if (cursor != null && cursor.moveToFirst()) {
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.getString(index)
    } else {
        ""
    }
    cursor?.close()
    return title
}
