//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Bitmap
//import android.net.Uri
//import android.util.Log
//import android.view.MotionEvent
//import android.view.ScaleGestureDetector
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Camera
//import androidx.compose.material.icons.filled.Videocam
//import androidx.compose.material.icons.sharp.CameraEnhance
//import androidx.compose.material.icons.sharp.FlashOn
//import androidx.compose.material.icons.sharp.Lens
//import androidx.compose.material.icons.sharp.Search
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.Executor
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.Executors
//import kotlin.coroutines.resume
//import kotlin.coroutines.suspendCoroutine
//import kotlin.math.roundToInt
//@SuppressLint("RestrictedApi")
//@Composable
//fun CameraView(
//    outputDirectory: File,
////    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//    lateinit var executor: ExecutorService
//    executor = Executors.newSingleThreadExecutor()
//    DisposableEffect(Unit) {
//        onDispose {
//            executor.shutdown()
//            }
//        }
//    // 1
//    var isVideoMode = remember { false }
//    val lensFacing = CameraSelector.LENS_FACING_BACK
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val preview = Preview.Builder().build()
//    val previewView = remember { PreviewView(context) }
//    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
//    val videoCapture: VideoCapture = remember { VideoCapture.Builder().build() }
//
//    val cameraSelector = CameraSelector.Builder()
//        .requireLensFacing(lensFacing)
//        .build()
//    var cameraController: CameraControl? = null
//    var cameraInfo: CameraInfo? = null
//    var maxZoomRatio : Float = 1F
//    var minZoomRatio : Float = 0F
////    var nowZoomRatio : Float = 0F
//    var nowZoomRatio by remember { mutableStateOf(0F) }
//
//    // 2
//    LaunchedEffect(lensFacing) {
//        val cameraProvider = context.getCameraProvider()
//        cameraProvider.unbindAll()
//        var camera = cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelector,
//            preview,
//            imageCapture
//        )
//        if (isVideoMode) {
//            camera = cameraProvider.bindToLifecycle(
//                lifecycleOwner,
//                cameraSelector,
//                preview,
//                imageCapture
//            )
//        } else {
//            camera = cameraProvider.bindToLifecycle(
//                lifecycleOwner,
//                cameraSelector,
//                preview,
//                imageCapture
//            )
//        }
//
//
//        cameraController = camera.cameraControl
//        cameraInfo = camera.cameraInfo
//        cameraInfo!!.zoomState.observe(lifecycleOwner, androidx.lifecycle.Observer {
//            maxZoomRatio = it.maxZoomRatio
//            minZoomRatio = it.minZoomRatio
//            nowZoomRatio = it.zoomRatio
//        })
////          setLinearZoom
////        cameraController.setLinearZoom(0F) // Mininum Zoom
////        cameraController.setLinearZoom(1F) // Maximum Zoom
//
//        val scaleGestureDetector = ScaleGestureDetector(context,
//            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//                override fun onScale(detector: ScaleGestureDetector): Boolean {
//                    val scale = camera.cameraInfo.zoomState.value!!.zoomRatio * detector.scaleFactor
//
//                    camera.cameraControl.setZoomRatio(scale)
//                    return true
//                }
//            })
//        // 터치로 초점 수정
//        previewView.setOnTouchListener { v, event ->
//            scaleGestureDetector.onTouchEvent(event)
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    v.performClick()
//                    return@setOnTouchListener true
//                }
//                MotionEvent.ACTION_UP -> {
//
//                    // Get the MeteringPointFactory from PreviewView
//                    val factory = previewView.meteringPointFactory
//                    // Create a MeteringPoint from the tap coordinates
//                    val point = factory.createPoint(event.x, event.y)
//
//                    // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
//                    val action = FocusMeteringAction.Builder(point).build()
//
//                    // Trigger the focus and metering. The method returns a ListenableFuture since the operation
//                    // is asynchronous. You can use it get notified when the focus is successful or if it fails.
//                    cameraController?.startFocusAndMetering(action)
//
//                    v.performClick()
//                    return@setOnTouchListener true
//                }
//                else -> return@setOnTouchListener false
//            }
//        }
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//    }
//
//    // 3
//    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
//        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
//        Column {
//            Row( modifier = Modifier.padding(bottom = 40.dp)) {
//                // 플래시
//                IconButton(
//                    modifier = Modifier.padding(10.dp),
//                    onClick = {
//                        Log.i("kgh", "Torch control")
//                        when(cameraInfo?.torchState?.value){
//                            TorchState.ON -> cameraController?.enableTorch(false)
//                            TorchState.OFF -> cameraController?.enableTorch(true)
//                        }
//                    },
//                    content = {
//                        Icon(
//                            imageVector = Icons.Sharp.FlashOn,
//                            contentDescription = "Flash!",
//                            tint = Color.Yellow,
//                            modifier = Modifier
//                                .size(70.dp)
//                                .padding(1.dp)
//                                .border(1.dp, Color.White, CircleShape)
//                        )
//                    }
//                )
//
//                // 줌
//                IconButton(
//                    modifier = Modifier.padding(10.dp),
//                    onClick = {
//                        Log.i("kgh", "Zoom Control")
//                        if (nowZoomRatio < 2F) {
//                            cameraController?.setZoomRatio(2F)
//                        }
//                        else {
//                            cameraController?.setZoomRatio(1F)
//                        }
//                    },
//                    content = {
//                        Column {
//                            Icon(
//                                imageVector = Icons.Sharp.Search,
//                                contentDescription = "Zoom!",
//                                tint = Color.Yellow,
//                                modifier = Modifier
//                                    .size(70.dp)
//                                    .padding(1.dp)
//                                    .border(1.dp, Color.White, CircleShape)
//                            )
//                            Text(text = "${(nowZoomRatio * 10.0).roundToInt() / 10.0} 배", color = Color.White)
//                        }
//                    }
//                )
//            }
//
//            Row {
//                //////////////// 촬영버튼
//
//                    IconButton(
//                        modifier = Modifier.padding(bottom = 20.dp),
//                        onClick = {
//                            Log.i("kgh", "ON CLICK")
//                            takePhoto(
//                                filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
//                                imageCapture = imageCapture,
//                                outputDirectory = outputDirectory,
//                                executor = executor,
//                                onImageCaptured = onImageCaptured,
//                                onError = onError
//                            )
//                        },
//                        content = {
//                            Icon(
//                                imageVector = Icons.Sharp.Lens,
//                                contentDescription = "Take picture",
//                                tint = Color.White,
//                                modifier = Modifier
//                                    .size(100.dp)
//                                    .padding(1.dp)
//                                    .border(1.dp, Color.White, CircleShape)
//                            )
//                        }
//                    )
//
////                else {
////                    IconButton(
////                        modifier = Modifier.padding(bottom = 20.dp),
////                        onClick = {
////                            Log.i("kgh", "ON CLICK")
////                            takeVideo(
////                                filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
////                                videoCapture = videoCapture,
////                                outputDirectory = outputDirectory,
////                                executor = executor,
////                                onImageCaptured = onImageCaptured,
////                                onError = onError
////                            )
////                        },
////                        content = {
////                            Icon(
////                                imageVector = Icons.Sharp.CameraEnhance ,
////                                contentDescription = "Take video",
////                                tint = Color.White,
////                                modifier = Modifier
////                                    .size(100.dp)
////                                    .padding(1.dp)
////                                    .border(1.dp, Color.White, CircleShape)
////                            )
////                        }
////                    )
////                }
//
//                //////////////////// 촬영버튼
//
//                // mode change
//                IconButton(onClick = {
//                    isVideoMode = !isVideoMode
//                }) {
//                    Icon(
//                        if (isVideoMode) Icons.Filled.Videocam else Icons.Filled.Camera,
//                        contentDescription = if (isVideoMode) "Switch to photo mode" else "Switch to video mode"
//                    )
//                }
//
//            }
//
//        }
//
//
//    }
//}
//
//private fun takePhoto(
//    filenameFormat: String,
//    imageCapture: ImageCapture,
//    outputDirectory: File,
//    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//
//    val photoFile = File(
//        outputDirectory,
//        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
//    )
//
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//    imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
//        override fun onError(exception: ImageCaptureException) {
//            Log.e("kgh", "Take photo error:", exception)
//            onError(exception)
//        }
//
//        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//            val savedUri = Uri.fromFile(photoFile)
//            onImageCaptured(savedUri)
//        }
//    })
//}
//
//@SuppressLint("RestrictedApi")
//private fun takeVideo(
//    filenameFormat: String,
//    videoCapture: VideoCapture,
//    outputDirectory: File,
//    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//
//    val outputFile = File(
//        outputDirectory,
//        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".mp4"
//    )
//
//    val outputOptions = VideoCapture.OutputFileOptions.Builder(outputFile).build()
//
//    videoCapture.startRecording(outputOptions, executor, object : VideoCapture.OnVideoSavedCallback {
//        override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
//            onError(ImageCaptureException(videoCaptureError, "video record error", cause))
//        }
//
//        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
//            val savedUri = Uri.fromFile(outputFile)
//            onImageCaptured(Uri.fromFile(outputFile))
//        }
//    })
//}
//
//private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
//    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
//        cameraProvider.addListener({
//            continuation.resume(cameraProvider.get())
//        }, ContextCompat.getMainExecutor(this))
//    }
//}
