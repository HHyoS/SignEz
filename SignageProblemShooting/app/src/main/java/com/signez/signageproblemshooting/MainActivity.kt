package com.signez.signageproblemshooting


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
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
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.signez.signageproblemshooting.analysis.getFrames
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.fields.EditNumberField
import com.signez.signageproblemshooting.pickers.*
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.MainViewModelFactory
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.inputs.*
import com.signez.signageproblemshooting.ui.signage.CabinetDetailViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetViewModel
import com.signez.signageproblemshooting.ui.signage.SignageDetailViewModel
import com.signez.signageproblemshooting.ui.signage.SignageViewModel
import com.signez.signageproblemshooting.ui.theme.SignEzPrototypeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

enum class Screen {
    MAIN,
    SECOND,
    THIRD
}

class MainActivity : ComponentActivity() {
    private lateinit var viewModel1: PictureViewModel
    private lateinit var viewModel2: VideoViewModel
    private lateinit var viewModel3: SignageViewModel
    private lateinit var viewModel4: CabinetViewModel
    private lateinit var viewModel5: AnalysisViewModel
    private lateinit var viewModel6: SignageDetailViewModel
    private lateinit var viewModel7: CabinetDetailViewModel
//    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as SignEzApplication).container)
    }
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    var canRun: MutableState<Boolean> = mutableStateOf(false)
//    var imageUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 기본 촬영 사진 uri
//    var videoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 기본 촬영 영상 uri
//    private var imageUri:MutableLiveData<Uri> = MutableLiveData(Uri.EMPTY)

//    var mCurrentPhotoPath = mutableStateOf("")
//    private var mCurrentPhotoPath:MutableLiveData<String> = MutableLiveData("")
//    var mCurrentVideoPath = mutableStateOf("")

    private val REQUEST_CODE_VIDEO_CAPTURE = 1
    private val REQUEST_CODE_IMAGE_CAPTURE = 2
    private val REQUEST_CODE_IMAGE_CAPTURE_2 = 22
    private val REQUEST_CODE_IMAGE_CAPTURE_3 = 222
    private val REQUEST_CODE_IMAGE_CAPTURE_4 = 2222
    private val REQUEST_CODE_IMAGE_CAPTURE_5 = 22222
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4

    private val finishtimeed = 1000L;
    private var presstime = 0L;

    private val REQUEST_CODE_PERMISSIONS = 1000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private lateinit var permissionLatch: CountDownLatch

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_CAPTURE -> {
                    galleryAddPic(this, viewModel1) // 사진 분석용
                }
                REQUEST_CODE_IMAGE_CAPTURE_2 -> {
                    galleryAddPic(this, viewModel3) // 사이니지 생성 사진
                }
                REQUEST_CODE_IMAGE_CAPTURE_3 -> {
                    galleryAddPic(this, viewModel4) // 캐비닛 생성 사진
                }
                REQUEST_CODE_IMAGE_CAPTURE_4 -> {
                    galleryAddPic(this, viewModel6) // 사이니지 수정 사진
                }
                REQUEST_CODE_IMAGE_CAPTURE_5 -> {
                    galleryAddPic(this, viewModel7) // 캐비닛 수정 사진
                }
                REQUEST_CODE_VIDEO_CAPTURE -> {
                    galleryAddVideo(this, viewModel2) // 영상 분석용
                }

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

        viewModel1 = ViewModelProvider( // 분석 이미지
            this,
            factory = AppViewModelProvider.Factory
        ).get(PictureViewModel::class.java)
        viewModel2 = ViewModelProvider( // 분석 영상
            this,
            factory = AppViewModelProvider.Factory
        ).get(VideoViewModel::class.java)
        viewModel3 = ViewModelProvider( // 사이니지
            this,
            factory = AppViewModelProvider.Factory
        ).get(SignageViewModel::class.java)
        viewModel4 = ViewModelProvider( // 캐비닛
            this,
            factory = AppViewModelProvider.Factory
        ).get(CabinetViewModel::class.java)
        viewModel5 = ViewModelProvider( // 분석 종합
            this,
            factory = AppViewModelProvider.Factory
        ).get(AnalysisViewModel::class.java)
        viewModel6 = ViewModelProvider( // 사이니지 수정
            this,
            factory = AppViewModelProvider.Factory
        ).get(SignageDetailViewModel::class.java)
        viewModel7 = ViewModelProvider( // 캐비닛 수정
            this,
            factory = AppViewModelProvider.Factory
        ).get(CabinetDetailViewModel::class.java)

        viewModel4.insertTestRecord()
        viewModel3.insertTestRecord()
        mainViewModel.insertTestRecord()
        setContent {
            SignEzPrototypeTheme {

                SignEzApp(
                    activity = this,
                    viewModel1 = viewModel1,
                    viewModel2 = viewModel2,
                    viewModel3 = viewModel3,
                    viewModel4 = viewModel4,
                    viewModel5 = viewModel5,
                    viewModel6 = viewModel6,
                    viewModel7 = viewModel7
                )
            }
        }

        requestPermissions()
        Log.d("gogogo","${canRun.value}")

//        requestCameraPermission() // 카메라 권한 받기 , 앱 열때
//        requestWriteExternalStoragePermission() // 외부 저장소 읽기 권한
//        requestReadExternalStoragePermission() // 외부 저장소 쓰기 권한
        // 권한 없으면 동작 못하게 처리 해줘야함 or 재요청, 나중에 ㄱ
    }


    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val permissionsToRequest = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) { // 권한 요청할게 있으면 요청 날림.
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            canRun.value = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            var allPermissionsGranted = true

            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted
                canRun.value = true
            } else {
                // Permissions denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                finishAffinity() // Close the app if permissions are denied
            }
        }
    }



    /**
     * A native method that is implemented by the 'signageproblemshooting' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'signageproblemshooting' library on application startup.
        init {
            System.loadLibrary("signageproblemshooting")
        }
    }

    // 현재 권한 승인상태를 확인하고, 충족되지 않았다면 권한 요청.
//    private fun requestCameraPermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                Log.i("kgh", "Permission previously granted")
//                shouldShowCamera.value = true
//            }
//
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                Manifest.permission.CAMERA
//            ) -> Log.i("kgh", "Show camera permissions dialog")
//
//            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    private fun requestWriteExternalStoragePermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
//            )
//        }
//    }
//
//    private fun requestReadExternalStoragePermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
//            )
//        }
//    }
}

//@Preview(showBackground = true)
// 일단 찍기, 불러오기 uri 따로 분리했는데 합쳐도 될듯.
