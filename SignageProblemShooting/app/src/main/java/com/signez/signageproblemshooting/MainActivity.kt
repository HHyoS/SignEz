package com.signez.signageproblemshooting

//SignEzPrototypeTheme


//
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
import java.io.*
import java.util.*
import android.Manifest
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener

class MainActivity : ComponentActivity(), AutoPermissionsListener {
    private lateinit var viewModel1: PictureViewModel
    private lateinit var viewModel2: VideoViewModel
    private lateinit var viewModel3: SignageViewModel
    private lateinit var viewModel4: CabinetViewModel
    private lateinit var viewModel5: AnalysisViewModel
    private lateinit var viewModel6: SignageDetailViewModel
    private lateinit var viewModel7: CabinetDetailViewModel

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as SignEzApplication).container)
    }

    private val REQUEST_CODE_VIDEO_CAPTURE = 1
    private val REQUEST_CODE_IMAGE_CAPTURE = 2
    private val REQUEST_CODE_IMAGE_CAPTURE_2 = 22
    private val REQUEST_CODE_IMAGE_CAPTURE_3 = 222
    private val REQUEST_CODE_IMAGE_CAPTURE_4 = 2222
    private val REQUEST_CODE_IMAGE_CAPTURE_5 = 22222
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 4
    private val REQUEST_CODE_ERROR_DETECT_ACTIVITY = 999

    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1000
        private const val REQUEST_CODE_APP_SETTINGS = 2000
        // Used to load the 'signageproblemshooting' library on application startup.
        init {
            System.loadLibrary("signageproblemshooting")
        }
    }

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
                REQUEST_CODE_APP_SETTINGS -> {
                    mainViewModel.permissionsGranted.value = checkAndRequestPermissions()
                }
                REQUEST_CODE_PERMISSIONS-> {
                    mainViewModel.permissionsGranted.value = checkAndRequestPermissions()
                }
                REQUEST_CODE_ERROR_DETECT_ACTIVITY -> {
                    Log.d("godetect","clear")
                }
            }
        }
    }

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
        viewModel5.insertTestRecord()
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
                    viewModel7 = viewModel7,
                    viewModel8 = mainViewModel
                )
            }
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            requestPermissions()
//        }, 200)
        AutoPermissions.Companion.loadSelectedPermissions(this, REQUEST_CODE_PERMISSIONS, permissions)
    }
    fun go() {
        applicationContext
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.Companion.parsePermissions(this, REQUEST_CODE_PERMISSIONS, permissions, this)
    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        if (permissions.isNotEmpty()) {
            mainViewModel.permissionsGranted.value=false
            Toast.makeText(this, "거부된 권한 수: " + permissions.size, Toast.LENGTH_LONG).show()
        }
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        Toast.makeText(this, "허용된 권한 수: " + permissions.size, Toast.LENGTH_LONG).show()
    }
    private fun checkAndRequestPermissions(): Boolean {
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGrantedPermissions, REQUEST_CODE_PERMISSIONS)
            return false
        }
        return true
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_CODE_APP_SETTINGS)
    }


//    private fun requestPermissions() {
//        val permissionsToRequest = mutableListOf<String>()
//        for (permission in permissions) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    permission
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                permissionsToRequest.add(permission)
//                shouldShowRequestPermissionRationale(permission)
//            }
//        }
//
//        if (permissionsToRequest.isNotEmpty()) { // 권한 요청할게 있으면 요청 날림.
//            ActivityCompat.requestPermissions(
//                this,
//                permissionsToRequest.toTypedArray(),
//                REQUEST_CODE_PERMISSIONS
//            )
//        } else {
//            Log.d("permissionis","granted")
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        var allPermissionsGranted = mutableStateOf(false)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            for (i in grantResults.indices) {
//                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                    allPermissionsGranted.value = false
//                    break
//                }
//            }
//            if (allPermissionsGranted.value) {
//                // Permissions granted
//                Log.d("permissions are","granted")
//            } else {
//                // Permissions denied
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
////                finishAffinity() // Close the app if permissions are denied
//            }
//        }
//    }
}

//@Preview(showBackground = true)
// 일단 찍기, 불러오기 uri 따로 분리했는데 합쳐도 될듯.
