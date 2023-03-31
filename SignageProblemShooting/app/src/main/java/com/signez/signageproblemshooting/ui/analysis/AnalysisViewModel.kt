package com.signez.signageproblemshooting.ui.analysis

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
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
    var selectedResultId = mutableStateOf(-1L)
    var selectedModuleX = mutableStateOf(-1)
    var selectedModuleY = mutableStateOf(-1)
    var progressMessage = mutableStateOf("분석 중")
    var progressFloat = mutableStateOf(0.0F)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun getCabinet(signageId: Long): Cabinet {
        return cabinetRepository.getCabinetBySignageId(signageId)
    }


    fun getCabinet(): StateFlow<CabinetState> {
        return cabinetRepository.getCabinetStream(getSignage().value.signage.modelId)
            .filterNotNull()
            .map {
                CabinetState(cabinet = it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AnalysisViewModel.TIMEOUT_MILLIS),
                initialValue = CabinetState()
            )
    }

    suspend fun getSignageById(signageId: Long): Signage {
        return signageRepository.getSignageById(signageId)
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


    // 지난 분석 결과 목록
    val resultListState: StateFlow<resultListState> =
        analysisResultRepository.getAllResultsStream().map { resultListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(AnalysisViewModel.TIMEOUT_MILLIS),
                initialValue = resultListState()
            )

    // 결과 저장, 모듈 저장, 이미지 저장 순으로 진행.
    fun saveImage(bitmap: Bitmap, moduleId: Long = 0L): Long = runBlocking {
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

    fun saveResult(signageId: Long) = runBlocking {
        //
        val newResult = AnalysisResult(
            signageId = signageId
        )
        return@runBlocking analysisResultRepository.insertResult(newResult)
    }

    suspend fun getRelatedImages(moduleId: Long): List<ErrorImage> {
        return errorImagesRepository.getImagesByModuleId(moduleId)
    }

    private suspend fun getMostRecentResultId() = runBlocking {
        return@runBlocking analysisResultRepository.getMostRecentResult().id
    }

    suspend fun getRelatedModule(resultId: Long): List<ErrorModule> {
        return if (resultId == -1L) {
            val modules: List<ErrorModule> =
                errorModulesRepository.getModulesByResultId(resultId)
            modules
        } else { // resultId가 -1 == 안 골라 졌다면 가장 최근 생성된 결과 아이디 가져옴.
            val modules: List<ErrorModule> =
                errorModulesRepository.getModulesByResultId(getMostRecentResultId())
            modules
        }

    }

    suspend fun getRelatedResults(signageId: Long): List<AnalysisResult> {
        return analysisResultRepository.getResultsBySignageId(signageId)
    }

    suspend fun getImageById(imageId: Long): ErrorImage {
        return errorImagesRepository.getImageById(imageId)
    }

    suspend fun getModuleById(moduleId: Long): ErrorModule {
        return errorModulesRepository.getModuleById(moduleId)
    }

    suspend fun getResultById(resultId: Long): AnalysisResult {
        return analysisResultRepository.getResultById(resultId)
    }

    suspend fun getModulesByXYResultId(x: Int, y: Int, resultId: Long): List<ErrorModuleWithImage> {
        return errorModulesRepository.getModulesByXYResultId(x, y, resultId)
    }

    suspend fun deleteResult(resultId: Long) {
        analysisResultRepository.deleteById(resultId)
    }

    suspend fun deleteErrorModule(module: ErrorModule) {
        errorModulesRepository.deleteErrorModule(module)
    }

    suspend fun getSignageByResultId(resultId: Long): Signage? {
        val resultById = getResultById(resultId = resultId)
        if (resultById == null) {
            return null
        } else {
            return getSignageById(resultById.signageId)
        }
    }

    private fun createSimpleBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }

    fun insertTestRecord() = viewModelScope.launch {
        // Save image as a Blob
//        val bitmap1 = createSimpleBitmap(100, 100, Color.BLUE)
//        val outputStream1 = ByteArrayOutputStream()
//        bitmap1.compress(Bitmap.CompressFormat.JPEG, 20, outputStream1)
//        val byteArray1 = outputStream1.toByteArray()
//
//        val bitmap2 = createSimpleBitmap(100, 100, Color.RED)
//        val outputStream2 = ByteArrayOutputStream()
//        bitmap2.compress(Bitmap.CompressFormat.JPEG, 20, outputStream2)
//        val byteArray2 = outputStream2.toByteArray()
//
//        val bitmap3 = createSimpleBitmap(100, 100, Color.GREEN)
//        val outputStream3 = ByteArrayOutputStream()
//        bitmap3.compress(Bitmap.CompressFormat.JPEG, 20, outputStream3)
//        val byteArray3 = outputStream3.toByteArray()
//
//        val testAnalysisResult = AnalysisResult(signageId = 1L)
//        val resultId = analysisResultRepository.insertResult(testAnalysisResult)
//        val testErrorModule1 = ErrorModule(
//            resultId = resultId,
//            score = 90.1,
//            x = 1,
//            y = 1
//        )
//        val testErrorModule2 = ErrorModule(
//            resultId = resultId,
//            score = 45.1,
//            x = 1,
//            y = 1
//        )
//        val testErrorModule3 = ErrorModule(
//            resultId = resultId,
//            score = 75.1,
//            x = 1,
//            y = 1
//        )
//        val moduleId1 = errorModulesRepository.insertErrorModule(testErrorModule1)
//        val moduleId2 = errorModulesRepository.insertErrorModule(testErrorModule2)
//        val moduleId3 = errorModulesRepository.insertErrorModule(testErrorModule3)
//
//        val testErrorImage1 = ErrorImage(error_module_id = moduleId1, evidence_image = byteArray1)
//        errorImagesRepository.insertImage(testErrorImage1)
//        val testErrorImage2 = ErrorImage(error_module_id = moduleId2, evidence_image = byteArray2)
//        errorImagesRepository.insertImage(testErrorImage2)
//        val testErrorImage3 = ErrorImage(error_module_id = moduleId3, evidence_image = byteArray3)
//        errorImagesRepository.insertImage(testErrorImage3)
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

data class resultState(
    val result: AnalysisResult = AnalysisResult(
        id = 3L,
        signageId = 1L,
        resultDate = "2023-03-29"
    )
)

data class resultListState(val itemList: List<AnalysisResult> = listOf())