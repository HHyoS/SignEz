package com.signez.signageproblemshooting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.MainViewModelFactory
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.inputs.MainViewModel
import com.signez.signageproblemshooting.ui.inputs.PictureViewModel
import com.signez.signageproblemshooting.ui.inputs.VideoViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetDetailViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetViewModel
import com.signez.signageproblemshooting.ui.signage.SignageDetailViewModel
import com.signez.signageproblemshooting.ui.signage.SignageViewModel
import org.pytorch.Module
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
        val uri = intents.extras?.getString("DATA_URI")

        try {
            signageDetectModule = Module.load(assetFilePath(applicationContext, signageDetectModuleFileName))
            errorDetectModule = Module.load(assetFilePath(applicationContext, errorDetectModuleFileName))
        } catch (e: Error){

        }


        when(type) {
            REQUEST_DETECT_VIDEO -> detectVideo(uri)
            REQUEST_DETECT_PHOTO -> detectPhoto(uri)
            else -> {}
        }




    }

    fun detectVideo(uri: String?) {

    }

    fun detectPhoto(uri: String?) {

    }

    fun getCorners(){

    }

    fun getPredictions(){

    }

    fun setProgress() {

    }


}