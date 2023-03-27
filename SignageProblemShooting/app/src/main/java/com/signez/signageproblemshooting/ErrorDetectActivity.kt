package com.signez.signageproblemshooting

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.MainViewModelFactory
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.inputs.MainViewModel
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
        analysisViewModel = ViewModelProvider(this, factory = AppViewModelProvider.Factory)[AnalysisViewModel::class.java]

        val intents: Intent = intent
        val type = intents.extras?.getInt("REQUEST_TYPE")

        try {
            signageDetectModule = Module.load(assetFilePath(applicationContext, signageDetectModuleFileName))
            errorDetectModule = Module.load(assetFilePath(applicationContext, errorDetectModuleFileName))
        } catch (e: Error){

        }


        when(type) {
            REQUEST_DETECT_VIDEO -> detectVideo()
            REQUEST_DETECT_PHOTO -> detectPhoto()
            else -> {}
        }




    }

    private fun detectVideo() {

    }

    private fun detectPhoto() {
        val originalImage: Bitmap? = loadBitmapFromUriWithGlide(applicationContext,
            analysisViewModel.imageContentUri.value)
        val width = analysisViewModel.getSignage().value.signage.width.toInt()
        val height = analysisViewModel.getSignage().value.signage.height.toInt()

        val originalMat: Mat = bitmapToMat(originalImage!!)

        val points: MutableList<Point> = getCorners(originalMat)

        val warpedMat = getWarp(originalMat, points, width, height)







    }


    private fun getCorners(originalMat: Mat): MutableList<Point> {
        var points = mutableListOf<Point>()
        //
        //
        //
        //
        //

        if (points.size != 4) throw SignageNotFoundException("Signage Not Found")
        points = sortPoints(points)
        return points
    }

    private fun getWarp(originalMat: Mat, points:MutableList<Point>, width: Int, height: Int): Mat{
        val point: Array<Point> = points.toTypedArray()
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

    private fun sortPoints(points: MutableList<Point>): MutableList<Point>{
        //
        //
        //
        //
        return points
    }

    private fun getPredictions(){

    }

    private fun setProgress() {

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

    fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        return mat
    }

}