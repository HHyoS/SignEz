package com.kgh.signezprototype

//SignEzPrototypeTheme


//

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kgh.signezprototype.analysis.getFrames
import com.kgh.signezprototype.data.entities.AnalysisResult
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.pickers.*
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.MainViewModelFactory
import com.kgh.signezprototype.ui.inputs.*
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

enum class Screen {
    MAIN,
    SECOND,
    THIRD
}
class MainActivity : ComponentActivity() {
    private lateinit var viewModel1:PictureViewModel
    private lateinit var viewModel2:VideoViewModel
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as SignEzApplication).container)
    }
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

//    var imageUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 기본 촬영 사진 uri
//    var videoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 기본 촬영 영상 uri
//    private var imageUri:MutableLiveData<Uri> = MutableLiveData(Uri.EMPTY)

//    var mCurrentPhotoPath = mutableStateOf("")
//    private var mCurrentPhotoPath:MutableLiveData<String> = MutableLiveData("")
//    var mCurrentVideoPath = mutableStateOf("")

    private val REQUEST_CODE_VIDEO_CAPTURE = 1
    private val REQUEST_CODE_IMAGE_CAPTURE = 2
    private val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4

    private val finishtimeed = 1000L;
    private var presstime = 0L;

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kgh", "Permission granted")
            shouldShowCamera.value = false
        } else {
            Log.i("kgh", "Permission denied")
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_CAPTURE -> { galleryAddPic(this, viewModel1) }
                REQUEST_CODE_VIDEO_CAPTURE -> { galleryAddVideo(this, viewModel2) }

            }
        }
    }

//    override fun onKeyDown(keycode: Int, event: KeyEvent?): Boolean {
//        val tempTime = System.currentTimeMillis()
//        val intervalTime: Long = tempTime - presstime
//
//        if (0 <= intervalTime && finishtimeed >= intervalTime) {
//            finish()
//        } else {
//            presstime = tempTime
//            Toast.makeText(applicationContext, "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show()
//        }
//        return false
//    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as SignEzApplication).container
        viewModel1 = ViewModelProvider(this,factory = AppViewModelProvider.Factory).get(PictureViewModel::class.java)
        viewModel2 = ViewModelProvider(this,factory = AppViewModelProvider.Factory).get(VideoViewModel::class.java)

        mainViewModel.insertTestRecord()
        setContent {
                SignEzApp(
                    activity = this,
                    viewModel1 = viewModel1,
                    viewModel2 = viewModel2
                    )
        }
        requestCameraPermission() // 카메라 권한 받기 , 앱 열때
        requestWriteExternalStoragePermission() // 외부 저장소 읽기 권한
        requestReadExternalStoragePermission() // 외부 저장소 쓰기 권한
        // 권한 없으면 동작 못하게 처리 해줘야함 or 재요청, 나중에 ㄱ
    }

    // 현재 권한 승인상태를 확인하고, 충족되지 않았다면 권한 요청.
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kgh", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kgh", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun requestWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }
}

//@Preview(showBackground = true)
// 일단 찍기, 불러오기 uri 따로 분리했는데 합쳐도 될듯.

