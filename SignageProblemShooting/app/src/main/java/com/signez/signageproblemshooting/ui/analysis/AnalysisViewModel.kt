package com.signez.signageproblemshooting.ui.analysis

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.entities.*
import com.signez.signageproblemshooting.data.repository.*
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream


class AnalysisViewModel(
    private val signageRepository: SignagesRepository,
    private val cabinetRepository: CabinetsRepository,
    private val analysisResultRepository: AnalysisResultsRepository,
    private val errorModulesRepository: ErrorModulesRepository,
    private val errorImagesRepository: ErrorImagesRepository,
    application: Application
) : AndroidViewModel(application), MediaViewModel {
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var mCurrentPhotoPath = mutableStateOf("")
    override var type = 0; // 0 = 선택 x, 1 = 갤러리에서 골랐을 때, 2 = 앱에서 찍었을 때
    var videoContentUri = mutableStateOf(Uri.EMPTY)
    var imageContentUri = mutableStateOf(Uri.EMPTY)
    var signageId = mutableStateOf(-1L)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun getCabinet(signageId: Long): Cabinet {
        return cabinetRepository.getCabinetBySignageId(signageId)
    }


    fun getCabinet(): StateFlow<CabinetState> {
        return cabinetRepository.getCabinetStream(getSignage().value.signage.modelId)
            .filterNotNull()
            .map { CabinetState(cabinet = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AnalysisViewModel.TIMEOUT_MILLIS),
                initialValue = CabinetState()
            )

    }

    fun getSignage(): StateFlow<SignageState> {
        val signageState: StateFlow<SignageState> =
            signageRepository.getSignageStream(signageId.value)
                .filterNotNull()
                .map {
                    SignageState(signage = it)
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(AnalysisViewModel.TIMEOUT_MILLIS),
                    initialValue = SignageState()
                )
        return signageState
    }

    // 결과 저장, 모듈 저장, 이미지 저장 순으로 진행.
    fun saveImage(bitmap: Bitmap, moduleId: Long = 0L) = runBlocking {
        // Save image as a Blob
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()

        val newImage = ErrorImage(
            error_module_id = moduleId,
            evidence_image = byteArray
        )
        return@runBlocking errorImagesRepository.insertImage(newImage)
    }

    fun saveModule(resultId: Long = 0L, score: Double, x: Int, y: Int) = runBlocking {
        //
        val newModule = ErrorModule(
            resultId = resultId,
            score = score,
            x = x,
            y = y
        )
        return@runBlocking errorModulesRepository.insertErrorModule(newModule)
    }

    fun saveResult() = runBlocking {
        //
        val newResult = AnalysisResult(
            signageId = signageId.value
        )
        return@runBlocking analysisResultRepository.insertResult(newResult)
    }

    suspend fun getRelatedImages(moduleId: Long): List<ErrorImage> {
        val images: List<ErrorImage> =
            errorImagesRepository.getImagesByModuleId(moduleId)
        return images
    }

    suspend fun getRelatedModule(resultId: Long): List<ErrorModule> {
        val modules: List<ErrorModule> =
            errorModulesRepository.getModulesByResultId(resultId)
        return modules
    }

    suspend fun getRelatedResults(signageId: Long): List<AnalysisResult> {
        val results: List<AnalysisResult> =
            analysisResultRepository.getResultsBySignageId(signageId)
        return results
    }

    suspend fun getImageById(imageId: Long): ErrorImage {
        val image: ErrorImage =
            errorImagesRepository.getImageById(imageId)
        return image
    }

    suspend fun getModuleById(moduleId: Long): ErrorModule {
        val module: ErrorModule =
            errorModulesRepository.getModuleById(moduleId)
        return module
    }

    suspend fun getResultById(resultId: Long): AnalysisResult {
        val result: AnalysisResult =
            analysisResultRepository.getResultById(resultId)
        return result
    }

    suspend fun deleteResult(resultId: Long) {
        analysisResultRepository.deleteById(resultId)
    }
}

data class SignageState(
    val signage: Signage = Signage(
        id = 0L,
        name = "TEST",
        height = 19.0,
        width = 11.0,
        heightCabinetNumber = 19,
        widthCabinetNumber = 11,
        modelId = 1,
        repImg = byteArrayOf(1)
    )
)
