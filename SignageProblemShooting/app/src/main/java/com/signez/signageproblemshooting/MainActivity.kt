package com.signez.signageproblemshooting

//SignEzTheme


//
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.signez.signageproblemshooting.ui.theme.SignEzTheme
import java.io.*
import java.util.*
import android.Manifest
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import com.signez.signageproblemshooting.data.datastore.StoreInitialLaunch
import com.signez.signageproblemshooting.ui.analysis.ResultGridDestination
import com.signez.signageproblemshooting.ui.analysis.ResultsHistoryDestination

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
    private val REQUEST_CODE_IMAGE_CROP_ACTIVITY = 957
    private val REQUEST_CODE_TUTORIAL_ACTIVITY = 310

    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    lateinit var navController: NavHostController
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
        Log.d("MainActivityOnActivityResult", "${requestCode.toString()}, ${resultCode.toString()}")
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
                REQUEST_CODE_IMAGE_CROP_ACTIVITY-> {
                    finishActivity(REQUEST_CODE_IMAGE_CROP_ACTIVITY)
                    navController.popBackStack()
                    navController.navigate(ResultsHistoryDestination.route)
                    navController.navigate(ResultGridDestination.route+"/-1")
                }
                REQUEST_CODE_TUTORIAL_ACTIVITY-> {
                    finishActivity(REQUEST_CODE_TUTORIAL_ACTIVITY)
                    Log.d("TUTORIAL","tutorial ended")
                }
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Intent(this, MainActivity::class.java)

        viewModel1 = ViewModelProvider( // 분석 이미지
            this,
            factory = AppViewModelProvider.Factory
        )[PictureViewModel::class.java]
        viewModel2 = ViewModelProvider( // 분석 영상
            this,
            factory = AppViewModelProvider.Factory
        )[VideoViewModel::class.java]
        viewModel3 = ViewModelProvider( // 사이니지
            this,
            factory = AppViewModelProvider.Factory
        )[SignageViewModel::class.java]
        viewModel4 = ViewModelProvider( // 캐비닛
            this,
            factory = AppViewModelProvider.Factory
        )[CabinetViewModel::class.java]
        viewModel5 = ViewModelProvider( // 분석 종합
            this,
            factory = AppViewModelProvider.Factory
        )[AnalysisViewModel::class.java]
        viewModel6 = ViewModelProvider( // 사이니지 수정
            this,
            factory = AppViewModelProvider.Factory
        )[SignageDetailViewModel::class.java]
        viewModel7 = ViewModelProvider( // 캐비닛 수정
            this,
            factory = AppViewModelProvider.Factory
        )[CabinetDetailViewModel::class.java]

        viewModel4.insertTestRecord(applicationContext)
        viewModel3.insertTestRecord(applicationContext)
        mainViewModel.insertTestRecord(applicationContext)


        // ...
        // Check if the initial data input is finished and restart the activity
        setContent {
            navController = rememberNavController()
            val isInitialLaunch = StoreInitialLaunch(LocalContext.current).getInitialLaunch.collectAsState(initial = false).value!!
            SignEzTheme {
                if(isInitialLaunch){
                    openTutorialActivity(context = LocalContext.current)
                }
                SignEzApp(
                    activity = this,
                    navController = navController,
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

        AutoPermissions.loadSelectedPermissions(this, REQUEST_CODE_PERMISSIONS, permissions)
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.Companion.parsePermissions(this, REQUEST_CODE_PERMISSIONS, permissions, this)
    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
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
}

