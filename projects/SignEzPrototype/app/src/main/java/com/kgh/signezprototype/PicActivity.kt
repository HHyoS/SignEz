package com.kgh.signezprototype

import CameraView
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import java.io.File


class PicActivity : ComponentActivity() {
//    lateinit var photoUri: Uri
    var photoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
//    lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("shouldShowCamera", false)) {
            shouldShowCamera.value = true
        }
//        shouldShowPhoto = intent.getBooleanExtra("shouldShowPhoto", false)

        setContent {
//            Box(modifier = Modifier.background(color= Color.Blue))
            if (shouldShowCamera.value) {
                CameraView(
                    outputDirectory = getOutputDirectory(), // 캡처 저장 위치
//                executor = cameraExecutor, // 카메라 실행자
                    onImageCaptured = ::handleImageCapture, // 사진 촬영시 동작할 함수
                    onError = { Log.e("kgh", "View error:", it) }
                )
            }

            if (shouldShowPhoto.value) {
                Image(
                    painter = rememberImagePainter(photoUri.value),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                Button(onClick = {
                    shouldShowPhoto.value = true
                    val resultIntent = Intent()
                    resultIntent.putExtra("photoUri", photoUri?.value?.toString())
                    setResult(Activity.RESULT_OK, resultIntent)
                    Log.i("kgh", "Closinig camera----: ${photoUri?.value?.toString()}")
                    finish()
                                 },
                    modifier = Modifier.padding(16.dp)) {
                    Text("Get Image")
                }
            }
        }
    }

    private fun handleImageCapture(uri: Uri) {
        Log.i("kgh", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri.value = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
//    val intent = Intent(this, OtherActivity::class.java)
//    intent.putExtra("key", "value")
//    startActivity(intent)
}