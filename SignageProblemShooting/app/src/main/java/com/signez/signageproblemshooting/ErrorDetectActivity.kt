package com.signez.signageproblemshooting

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.MainViewModelFactory
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.inputs.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.Videoio
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class SignageNotFoundException(message: String) : Exception(message)

class ErrorDetectActivity : ComponentActivity() {
    // constants
    private val REQUEST_DETECT_VIDEO: Int = 100
    private val REQUEST_DETECT_PHOTO: Int = 101


    // viewModels
    private lateinit var analysisViewModel: AnalysisViewModel

    // Torch Modules
    private lateinit var signageDetectModule: Module
    private lateinit var errorDetectModule: Module
    private val signageDetectModuleFileName: String = "signage_detect.torchscript.pt"
    private val errorDetectModuleFileName: String = "error_detect.torchscript.pt"

    private val RESIZE_SIZE: Int = 640
    private val NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
    private val NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)

    // model output is of size 25200*(num_of_class+5)
    private val mOutputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    private val mOutputColumn = 6 // left, top, right, bottom, score and class probability
    private val scoreThreshold = 0.20f // score above which a detection is generated

    private val rectThreshold = 5.0f

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as SignEzApplication).container)
    }

    @Throws(IOException::class)
    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
            return file.absolutePath
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analysisViewModel = ViewModelProvider(
            this,
            factory = AppViewModelProvider.Factory
        )[AnalysisViewModel::class.java]

        val intents: Intent = intent
        val type = intents.extras?.getInt("REQUEST_TYPE")

        try {
            signageDetectModule =
                Module.load(assetFilePath(applicationContext, signageDetectModuleFileName))
            errorDetectModule =
                Module.load(assetFilePath(applicationContext, errorDetectModuleFileName))
        } catch (e: Exception) {
            Log.e("TorchScriptModule", "Failed to open module files.")
            //
            //
        }

        lifecycleScope.launch {
            when (type) {
                REQUEST_DETECT_VIDEO -> detectVideo()
                REQUEST_DETECT_PHOTO -> detectPhoto()
                else -> {
                    Log.e("ErrorDetectActivity", "Wrong Access to Activity!")
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                    //
                }
            }
        }


    }

    private suspend fun detectVideo() = coroutineScope {
        launch {
            val signage = analysisViewModel.getSignage().value.signage
            val cabinet = analysisViewModel.getCabinet().value.cabinet

            val width = signage.width.toInt() * 100
            val height = signage.height.toInt() * 100
            val moduleWidth: Float =
                width.toFloat() / (signage.widthCabinetNumber * cabinet.moduleRowCount)
            val moduleHeight: Float =
                height.toFloat() / (signage.heightCabinetNumber * cabinet.moduleColCount)

            val tempFile = withContext(Dispatchers.IO) {
                File.createTempFile("video", ".temp")
            }
            val inputStream: InputStream? = applicationContext.contentResolver
                .openInputStream(analysisViewModel.videoContentUri.value)
            val outputStream = withContext(Dispatchers.IO) {
                FileOutputStream(tempFile)
            }

            try {
                inputStream?.copyTo(outputStream)
                val videoCapture = VideoCapture()
                videoCapture.open(tempFile.path)

                val frameCount = videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT).toInt()

                if (videoCapture.isOpened) {
                    val frameSize = Size(
                        videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),
                        videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT)
                    )
                    val frame = Mat(frameSize, CvType.CV_8UC3)
                    var points: MutableList<Point>? = null

                    for (i in 0 until frameCount) {
                        if (videoCapture.read(frame)) {
                            if (i == 0) {
                                points = getCorners(frame)
                            }
                            if (points != null && points.size == 4) {
                                val warpedMat = getWarp(frame, points, width, height)
                                // val errorModuleList = getPredictions(warpedMat, errorDetectModule, width, height, moduleWidth, moduleHeight, resultId)
                            }
                            setProgresses(frameCount, i)
                        }
                    }
                } else {
                    Log.e("VideoProcessing", "Failed to open video file.")
                }

            } catch (e: Exception) {
                Log.e("VideoProcessing", "Error processing video file: ${e.localizedMessage}", e)
            } finally {
                withContext(Dispatchers.IO) {
                    outputStream.close()
                    inputStream?.close()
                }
                tempFile.delete()
            }


        }
    }

    private suspend fun detectPhoto() = coroutineScope {
        launch {
            val originalImage: Bitmap? = loadBitmapFromUriWithGlide(
                applicationContext,
                analysisViewModel.imageContentUri.value
            )
            val signage = analysisViewModel.getSignage().value.signage
            val cabinet = analysisViewModel.getCabinet().value.cabinet

            val width = signage.width.toInt() * 100
            val height = signage.height.toInt() * 100
            val moduleWidth: Float =
                width.toFloat() / (signage.widthCabinetNumber * cabinet.moduleRowCount)
            val moduleHeight: Float =
                height.toFloat() / (signage.heightCabinetNumber * cabinet.moduleColCount)

            val originalMat: Mat = bitmapToMat(originalImage!!)
            val points: MutableList<Point> = getCorners(originalMat)

            val warpedMat = getWarp(originalMat, points, width, height)

//            val errorModuleList = getPredictions(warpedMat, errorDetectModule, width, height, moduleWidth, moduleHeight, resultId)

        }
    }

    private fun getPredictions(
        warpedMat: Mat,
        detectModule: Module,
        width: Int,
        height: Int,
        moduleWidth: Float,
        moduleHeight: Float,
        resultId: Long
    ): MutableList<ErrorModule> {
        val imgScaleX = width.toFloat() / RESIZE_SIZE
        val imgScaleY = height.toFloat() / RESIZE_SIZE
        // 640x640 resize
        val resizedMat = Mat()
        Imgproc.resize(warpedMat, resizedMat, Size(RESIZE_SIZE.toDouble(), RESIZE_SIZE.toDouble()))
        // Mat to tensor
        val resizedBitmap =
            Bitmap.createBitmap(resizedMat.cols(), resizedMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resizedMat, resizedBitmap)
        val inputTensor: Tensor =
            TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, NO_MEAN_RGB, NO_STD_RGB)
        // detect
        val outputTuple = detectModule.forward(IValue.from(inputTensor)).toTuple()
        // output to error module entities
        val outputTensor: Tensor = outputTuple[0].toTensor()
        val outputFloats: FloatArray = outputTensor.dataAsFloatArray
        return outputFloatsToErrorModules(
            outputFloats,
            imgScaleX,
            imgScaleY,
            moduleWidth,
            moduleHeight,
            resultId
        )


    }

    private fun outputFloatsToErrorModules(
        outputs: FloatArray,
        imgScaleX: Float,
        imgScaleY: Float,
        moduleWidth: Float,
        moduleHeight: Float,
        resultId: Long
    ): MutableList<ErrorModule> {
        val errorModuleList: MutableList<ErrorModule> = ArrayList()

        for (i in 0 until mOutputRow) {
            if (outputs[i * mOutputColumn + 4] > scoreThreshold) {
                val x = outputs[i * mOutputColumn]
                val y = outputs[i * mOutputColumn + 1]
                val w = outputs[i * mOutputColumn + 2]
                val h = outputs[i * mOutputColumn + 3]
                val score = outputs[i * mOutputColumn + 4]

                val left = imgScaleX * (x - w / 2)
                val top = imgScaleY * (y - h / 2)
                val right = imgScaleX * (x + w / 2)
                val bottom = imgScaleY * (y + h / 2)

                val centerX = (imgScaleX * x).toInt()
                val centerY = (imgScaleY * y).toInt()

                if (left % moduleWidth < rectThreshold && left % moduleWidth > moduleWidth - rectThreshold
                    && right % moduleWidth < rectThreshold && right % moduleWidth > moduleWidth - rectThreshold
                    && top % moduleHeight < rectThreshold && top % moduleHeight > moduleHeight - rectThreshold
                    && bottom % moduleHeight < rectThreshold && bottom % moduleHeight > moduleHeight - rectThreshold
                ) {
                    val moduleX: Int = (centerX / moduleWidth.toInt()) + 1
                    val moduleY: Int = (centerY / moduleHeight.toInt()) + 1
                    errorModuleList.add(
                        ErrorModule(
                            0,
                            resultId,
                            score.toDouble(),
                            moduleX,
                            moduleY
                        )
                    )
                }
            }
        }
        return errorModuleList
    }


    private fun getCorners(originalMat: Mat): MutableList<Point> {
        var points = mutableListOf<Point>()
        //
        //
        //
        //
        //

        if (points.size != 4) throw SignageNotFoundException("Signage Not Found")
        return points
    }

    private fun getWarp(
        originalMat: Mat,
        points: MutableList<Point>,
        width: Int,
        height: Int
    ): Mat {
        val sortedPoints = sortPoints(points)
        val point: Array<Point> = sortedPoints.toTypedArray()
        val src = MatOfPoint2f(*point)
        val dst = MatOfPoint2f(
            Point(0.0, 0.0), Point(width.toDouble(), 0.0),
            Point(width.toDouble(), height.toDouble()), Point(0.0, height.toDouble())
        )
        val m = Imgproc.getPerspectiveTransform(src, dst)
        // 원근 변환을 적용합니다.
        val result = Mat()
        Imgproc.warpPerspective(originalMat, result, m, Size(width.toDouble(), height.toDouble()))
        return result
    }

    private fun sortPoints(points: MutableList<Point>): MutableList<Point> {
        val a1 = points.minBy { it.x + it.y }
        val a2 = points.minBy { it.x - it.y }
        val a3 = points.maxBy { it.x + it.y }
        val a4 = points.maxBy { it.x - it.y }
        points.removeAll { true }
        points.add(a1)
        points.add(a2)
        points.add(a3)
        points.add(a4)
        return points
    }


    private fun setProgresses(frameCount: Int, i: Int) {

    }

    private fun loadBitmapFromUriWithGlide(context: Context, uri: Uri): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(uri)
                .submit()
                .get()
        } catch (e: Exception) {
            null
        }
    }

    private fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        return mat
    }

}