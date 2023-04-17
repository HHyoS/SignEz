package com.signez.signageproblemshooting.ui.inputs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.AppContainer
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import com.signez.signageproblemshooting.data.entities.ErrorImage
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.ui.analysis.resultListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream


class MainViewModel(appContainer: AppContainer) : ViewModel() {
    private val analysisResultsRepository = appContainer.analysisResultsRepository
    private val errorModuleRepository = appContainer.errorModulesRepository
    private val errorImageRepository = appContainer.errorImagesRepository
    val permissionsGranted = mutableStateOf(true)

    private fun imageToByteArray(context: Context, rId: Int): ByteArray {
        val drawable = ContextCompat.getDrawable(context, rId)
        // Convert drawable to Bitmap
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bmp = drawable?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
            bmp
        }
        val outputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        return outputStream.toByteArray()
    }

    fun insertTestRecord(context:Context) = runBlocking {
        val resultListState: StateFlow<resultListState> =
            analysisResultsRepository.getAllResultsStream().map { resultListState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000L),
                    initialValue = resultListState()
                )
        if (resultListState.value.itemList.isEmpty()) {
            analysisResultsRepository.insertResult( AnalysisResult(id=1,signageId=6L))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=1L,
                    resultId = 1L,
                    score=0.871612787246704,
                    x=17,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=2L,
                    resultId = 1L,
                    score=0.73947936296463,
                    x=18,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=3L,
                    resultId = 1L,
                    score=0.760273933410644,
                    x=19,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=4L,
                    resultId = 1L,
                    score=0.846648931503295,
                    x=20,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=5L,
                    resultId = 1L,
                    score=0.784361183643341,
                    x=22,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=6L,
                    resultId = 1L,
                    score=0.840295851230621,
                    x=24,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=7L,
                    resultId = 1L,
                    score=0.840969502925872,
                    x=26,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=8L,
                    resultId = 1L,
                    score=0.831808984279632,
                    x=29,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=9L,
                    resultId = 1L,
                    score=0.766186773777008,
                    x=30,
                    y=40
                ))
            errorModuleRepository.insertErrorModule(
                ErrorModule(
                    id=10L,
                    resultId = 1L,
                    score=0.74423861503601,
                    x=31,
                    y=40
                ))

            errorImageRepository.insertImage(
                ErrorImage(
                error_module_id = 1L,
                evidence_image = imageToByteArray(context, R.drawable.image_1)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 2L,
                    evidence_image = imageToByteArray(context, R.drawable.image_2)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 3L,
                    evidence_image = imageToByteArray(context, R.drawable.image_3)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 4L,
                    evidence_image = imageToByteArray(context, R.drawable.image_4)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 5L,
                    evidence_image = imageToByteArray(context, R.drawable.image_5)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 6L,
                    evidence_image = imageToByteArray(context, R.drawable.image_6)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 7L,
                    evidence_image = imageToByteArray(context, R.drawable.image_7)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 8L,
                    evidence_image = imageToByteArray(context, R.drawable.image_8)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 9L,
                    evidence_image = imageToByteArray(context, R.drawable.image_9)
                )
            )
            errorImageRepository.insertImage(
                ErrorImage(
                    error_module_id = 10L,
                    evidence_image = imageToByteArray(context, R.drawable.image_10)
                )
            )
        }
    }
}