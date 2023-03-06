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
import androidx.navigation.compose.rememberNavController
import com.kgh.signezprototype.analysis.getFrames
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.pickers.*
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
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    var photoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 커스텀 촬영 사진 uri
    var imageUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 기본 촬영 사진 uri
    var videoUri: MutableState<Uri> = mutableStateOf(Uri.EMPTY) // 기본 촬영 영상 uri

    var mCurrentPhotoPath = mutableStateOf("")
    var mCurrentVideoPath = mutableStateOf("")

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

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null && data.hasExtra("photoUri")) {
                photoUri.value = Uri.parse(data.getStringExtra("photoUri"))
            }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQUEST_CODE_IMAGE_CAPTURE -> {
                    galleryAddPic()
//                    imageUri.value = data?.data!!
//                    if (imageUri != Uri.EMPTY) {
//                        // Do something with the video data, such as playing it or uploading it to a server
//                        Log.d("TAG", "image URI: ${imageUri}")
//                    }
                }
                REQUEST_CODE_VIDEO_CAPTURE -> {
                    // Get the URI of the captured video
                    // Get the URI of the captured video
//                    videoUri.value = data?.data!!
//                    if (videoUri != Uri.EMPTY) {
//                        // Do something with the video data, such as playing it or uploading it to a server
//                        Log.d("TAG", "Video URI: $videoUri")
//                    }
                    galleryAddVideo()
                }
            }
        }
    }

    override fun onKeyDown(keycode: Int, event: KeyEvent?): Boolean {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - presstime

        if (0 <= intervalTime && finishtimeed >= intervalTime) {
            finish()
        } else {
            presstime = tempTime
            Toast.makeText(applicationContext, "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"

        // Get the public Pictures directory
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(storageDir, "SignEz")

        // Create the app directory if it doesn't exist
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        // Create the image file in the app directory
        val imageFile = File(appDir, imageFileName)
        mCurrentPhotoPath.value = imageFile.absolutePath
        return imageFile
    }

    private fun galleryAddPic() {
        // Get the absolute path of the image file
        val imagePath = mCurrentPhotoPath.value ?: return

        // Insert the image into the MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "My Image")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATA, imagePath)
        }
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        if (uri != null) {
//            imageUri.value = uri
//        }
//        // Notify the MediaStore that new content was added
//        uri?.let { mediaUri ->
//            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mediaUri).also {
//                sendBroadcast(it)
//            }
//        }
        imageUri.value = Uri.parse(imagePath)
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                if (photoFile != null) { // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                    val providerURI = FileProvider.getUriForFile(this, "com.kgh.signezprototype.provider", photoFile)
                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)

                }
            }
        }


    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createVideoFile(): File {
        // Create a video file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val videoFileName = "MP4_$timeStamp.mp4"

        // Get the public Movies directory
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        val appDir = File(storageDir, "SignEz")

        // Create the app directory if it doesn't exist
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        // Create the video file in the app directory
        val videoFile = File(appDir, videoFileName)
        mCurrentVideoPath.value = videoFile.absolutePath
        return videoFile
    }

    private fun dispatchTakeVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            // Ensure that there's a camera activity to handle the intent
            takeVideoIntent.resolveActivity(packageManager)?.also {
                // Create the File where the video should go
                val videoFile: File? = try {
                    createVideoFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                if (videoFile != null) {
                    val videoURI = FileProvider.getUriForFile(this, "com.kgh.signezprototype.provider", videoFile)
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600) // Limit video duration to 30 seconds
                    takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0) // 화질 가능한 낮게
                    startActivityForResult(takeVideoIntent, REQUEST_CODE_VIDEO_CAPTURE)
                }
            }
        }
    }

    private fun galleryAddVideo() {
        // Get the absolute path of the video file
        val videoPath = mCurrentVideoPath.value ?: return

        // Insert the video into the MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "My Video")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATA, videoPath)
        }
//        val uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
//        if (uri != null) {
//            videoUri.value = uri
//        }
//        // Notify the MediaStore that new content was added
//        uri?.let { mediaUri ->
//            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mediaUri).also {
//                sendBroadcast(it)
//            }
//        }
        videoUri.value = Uri.parse(videoPath)
        Toast.makeText(this, "Video saved to gallery.", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                            text = "SignEz",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ) },
                        backgroundColor = Color(0xFF0c4da2),
                        navigationIcon = null, // Remove the default navigation icon
                        actions = {
                            // Add your action buttons here
//                            IconButton(onClick = { /* Handle action button press */ }) {
//                                Icon(Icons.Filled.Favorite, contentDescription = "Favorite")
//                            }
                            IconButton(onClick = { /* Handle navigation icon press */ }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Navigation",tint = Color.White)
                            }
                        }
                    )
                },
                content = {
                    val context = this
                    val intent = Intent(context, PicActivity::class.java)
                    intent.putExtra("shouldShowCamera",shouldShowCamera.value)
                    intent.putExtra("shouldShowPhoto",shouldShowPhoto.value)

                    TotalOps(
                        ::dispatchTakePictureIntent,
                        ::dispatchTakeVideoIntent,
                        cameraLauncher,
                        intent,
                        photoUri, // 커스텀 촬영 사진 uri
                        imageUri, // 기본 촬영 사진 uri
                        videoUri, // 기본 촬영 영상 uri
                        ::getRealPathFromURI
                        )

//                    이미지 -> array로 바꾸는 계산하는 부분 테스트.
//                    if (photoUri.value != Uri.EMPTY ) {
//                        imageToArray(contentResolver, photoUri)
//                    }

                }
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

    fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path
    }
}

// 그냥 가지고 노는 샘플.
@Composable
fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Blue)
            .fillMaxSize()
    ){
        Text(scale.toString())
    }
}


//@Preview(showBackground = true)
@Composable
fun TotalOps(
    dispatchTakePictureIntent: () -> Unit,
    dispatchTakeVideoIntent: () -> Unit,
    cameraLauncher: ActivityResultLauncher<Intent>,
    myIntent:Intent,
    photoUri: MutableState<Uri>, // 커스텀 촬영 사진 uri
    imageUri: MutableState<Uri>, // 기본 촬영 사진 uri
    videoUri: MutableState<Uri>, // 기본 촬영 영상 uri
    getRealPathFromURI:(uri: Uri) -> String?

    ) {
    val focusManager = LocalFocusManager.current
    val sWidth = remember { mutableStateOf("") } // 사이니지
    val sHeight = remember { mutableStateOf("") } // 사이니지

    val dWidth = remember { mutableStateOf("") } // 디스플레이
    val dHeight = remember { mutableStateOf("") } // 디스플레이

    var step = remember { mutableStateOf(1) } //진행단계
    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "home") {
//        composable("video") {
//            videoSelect(
//            dispatchTakeVideoIntent,
//            step,
//            videoUri,
//            getRealPathFromURI
//        ) }
//        composable("picture") {
//            pictureSelect(
//            dispatchTakePictureIntent,
//            cameraLauncher,
//            myIntent,
//            step,
//            photoUri,
//            imageUri) }
//        /*...*/
//    }

    SignEzPrototypeTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Column(
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                if (step.value == 1) {
                    Text(
                        text = "분석 데이터 선택",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "사이니지 정보",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0c4da2)
                    )
                    Text(text = "사이니지 사이즈 (mm)")
                    EditNumberField(
                        // 가로 길이
                        head = "W : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = sWidth.value,
                        onValueChange = { sWidth.value = it },
                    )
                    EditNumberField(
                        // 세로 길이
                        head = "H : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done

                        ),
                        keyboardActions = KeyboardActions(
//                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = sHeight.value,
                        onValueChange = { sHeight.value = it },
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Text(text = "디스플레이 사이즈 (mm)")
                    EditNumberField(
                        // 가로 길이
                        head = "W : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = dWidth.value,
                        onValueChange = { dWidth.value = it },
                    )
                    EditNumberField(
                        // 세로 길이
                        head = "H : ",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
//                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        ),
                        value = dHeight.value,
                        onValueChange = { dHeight.value = it },
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Row {
                        OutlinedButton(
                            onClick = { step.value = 2 },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text("영상 분석")
                        }

                        OutlinedButton(
                            onClick = { step.value = 3 },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Color.Blue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Blue
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text("사진분석")
                        }
                    }

                }
                else if (step.value == 2) {
//                navController.navigate("video") {
//                    popUpTo("home")
//                }
                    videoSelect(
                        dispatchTakeVideoIntent,
                        step,
                        videoUri,
                        getRealPathFromURI
                    )

                }

                else if (step.value == 3) {
                    pictureSelect(
                        dispatchTakePictureIntent,
                        cameraLauncher,
                        myIntent,
                        step,
                        photoUri,
                        imageUri)
                }
                Spacer(modifier = Modifier.padding(40.dp))
            } // 컬럼 닫는 괄호

            if (step.value == 2 || step.value == 3) {
                OutlinedButton(
                    onClick = { step.value = 1 },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color.Blue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Blue
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomStart) // 컬럼이나, 로우는 못씀.*(정렬 종류 제한)
                        .padding(16.dp)
                        .height(50.dp)
                        .width(100.dp)
                ) {
                    Text(text = "이전")
                }

                OutlinedButton(
                    onClick = { step.value = 1 },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color.Blue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Blue
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // 컬럼이나, 로우는 못씀.*(정렬 종류 제한)
                        .padding(16.dp)
                        .height(50.dp)
                        .width(100.dp)
                ) {
                    Text(text = "다음")
                }
            } // 바닥 버튼 조건문

//            SelectBox() , 모델 고르기 잠시 중단.
        } // 가장 바같 박스 닫는 괄호
    }
}
// 일단 찍기, 불러오기 uri 따로 분리했는데 합쳐도 될듯.

@Composable
fun videoSelect(
    dispatchTakeVideoIntent: () -> Unit,
    step: MutableState<Int>,
    videoUri: MutableState<Uri>,
    getRealPathFromURI:(uri: Uri) -> String?
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
        if (videoUri.value != Uri.EMPTY) {
            coroutineScope.launch {
                val bitmap = withContext(Dispatchers.IO) { getVideoThumbnail(videoUri.value) }
                if (bitmap != null ) {
                    videoFrame = bitmap
                }
            }
        }
    }

    val loadVideoMetadata = {
        if (videoUri.value != Uri.EMPTY && !videoUri.value.toString().contains("content")) {
            val file = File(videoUri.value.toString())
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

    if (videoUri.value != Uri.EMPTY && !videoUri.value.equals(tempUri)) {
        tempUri = videoUri.value
        loadVideoMetadata()
    }

    Column {
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
                        videoUri.value = Uri.parse(address)
                        videoFrame = defaultBitmap
                    },last = last)
                }

                Row {
                    OutlinedButton(
                        onClick = { last.value = "take"; dispatchTakeVideoIntent() },
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
                            videoUri.value = Uri.EMPTY
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

                Log.d("compare","$videoFrame $videoUri $tempUri")
                if (!videoFrame.sameAs(defaultBitmap) && !videoUri.value.toString().contains("content") && last.value == "take") {
                    Log.d("compare","$videoFrame $defaultBitmap")
                    val frames: MutableList<Bitmap> = getFrames(context,videoUri.value,400,400,5)
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
                                        intent.setDataAndType(videoUri.value, "video/*")
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

    }

}
@Composable
fun pictureSelect(
    dispatchTakePictureIntent: () -> Unit,
    cameraLauncher: ActivityResultLauncher<Intent>,
    myIntent:Intent,
    step: MutableState<Int>,
    photoUri: MutableState<Uri>, //커스텀
    imageUri: MutableState<Uri> //디폴트
) {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageTitle by remember { mutableStateOf("") }
    var imageSize by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    var last: MutableState<String> = remember { mutableStateOf("") }// 기본 촬영 사진 uri
    val file = File(imageUri.value.toString())
    var contentUri:Uri = Uri.EMPTY
    if (imageUri.value != Uri.EMPTY) {
        if (!imageUri.value.toString().contains("content")) {
            contentUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }
        else {
            contentUri = imageUri.value
        }
    }
    var tempUri by remember { mutableStateOf(Uri.EMPTY) }

    val loadImageMetadata = {
        if (imageUri != Uri.EMPTY) {
            coroutineScope.launch {
                val metadata = withContext(Dispatchers.IO) {
                    loadImageMetadata(contentUri, context)
                }
                imageTitle = metadata.first
                imageSize = metadata.second // bytes
            }
        }
    }

    if (imageUri.value != Uri.EMPTY && !imageUri.value.equals(tempUri) ) {
        imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
        tempUri = imageUri.value
        loadImageMetadata()
    }

    Column {
        Column( // 예는 정렬 evenly나 spacebetween 같은거 가능
        ) {
            Text(
                text = "사진 분석",
                modifier = Modifier.align(Alignment.Start),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Column {
                Row {
                    ImagePicker(onImageSelected = { address ->
//                        imageUri.value = Uri.EMPTY
//                        imageUri.value = Uri.EMPTY
                        imageUri.value = Uri.parse(address)
                        imageBitmap = null
                         },last = last)
                }

                Row {
                    OutlinedButton(
                        onClick = { last.value="take"; dispatchTakePictureIntent() },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color.Blue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("사진 촬영")
                    }

                    OutlinedButton(
                        onClick = {
                            imageBitmap = null
                            imageUri.value = Uri.EMPTY
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, Color.Blue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Blue
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Clear")
                    }
                }
//                사용 가능한 커스터마이징 카메라. 잠시 잠금
//                OutlinedButton(
//                    onClick = { cameraLauncher.launch(myIntent) },
//                    shape = RoundedCornerShape(20.dp),
//                    border = BorderStroke(2.dp, Color.Blue),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        backgroundColor = Color.White,
//                        contentColor = Color.Blue
//                    ),
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text("커스텀 촬영")
//                }

                Log.d("compare","${imageUri.value.toString()}")
                if (imageUri.value != Uri.EMPTY && imageBitmap != null && last.value == "take") {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(width = 4.dp, color = Color.Black))
                                .height(400.dp)
                        ) {
                            imageBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Picture frame",
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                )
                            }
                        }
                        Text(text = "이미지 제목 : $imageTitle")
                        Text(text = "이미지 크기 : $imageSize byte")
                    }
                }
            }
        }
    }
}

fun playVideoFromUri(context: Context, uri: Uri) {
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(context, uri)
        prepare()
        start()
    }
}