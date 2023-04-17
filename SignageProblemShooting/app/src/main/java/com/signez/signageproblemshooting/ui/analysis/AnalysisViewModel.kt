package com.signez.signageproblemshooting.ui.analysis

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.entities.*
import com.signez.signageproblemshooting.data.repository.*
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
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
    var cabinetId = mutableStateOf(-1L)
    var selectedResultId = mutableStateOf(-1L)
    var selectedModuleX = mutableStateOf(-1)
    var selectedModuleY = mutableStateOf(-1)
    var selectedCabinetX = mutableStateOf(-1)
    var selectedCabinetY = mutableStateOf(-1)
    var threshold = mutableStateOf(-1)
    var selectedMoudleXInCabinet = mutableStateOf(-1)
    var selectedMoudleYInCabinet = mutableStateOf(-1)
    var progressMessage = mutableStateOf("분석 중")
    var progressFloat = mutableStateOf(0.0F)
    var isModuleClicked = mutableStateOf(false)

    var selectedModuleXforEvent = mutableStateOf(-1)
    var selectedModuleYforEvent = mutableStateOf(-1)
    var selectedModuleAccuracy = mutableStateOf(-1)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun getCabinet(signageId: Long): Cabinet {
        return cabinetRepository.getCabinetBySignageId(signageId)
    }

    fun getDeferredCabinet(signageId: Long): Deferred<Cabinet> {
        return viewModelScope.async { cabinetRepository.getCabinetBySignageId(signageId) }
    }


    fun getCabinet(): StateFlow<CabinetState> {
        return cabinetRepository.getCabinetStream(cabinetId.value)
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

    fun saveResult(signageId: Long) = runBlocking {
        //
        val newResult = AnalysisResult(
            signageId = signageId
        )
        return@runBlocking analysisResultRepository.insertResult(newResult)
    }

    private suspend fun getMostRecentResultId() = runBlocking {
        return@runBlocking analysisResultRepository.getMostRecentResult().id
    }

    suspend fun getRelatedModule(resultId: Long): List<ErrorModule> {
        return if (resultId != -1L) {
            val modules: List<ErrorModule> =
                errorModulesRepository.getModulesByResultId(resultId)
            modules
        } else { // resultId가 -1 == 안 골라 졌다면 가장 최근 생성된 결과 아이디 가져옴.
            val modules: List<ErrorModule> =
                errorModulesRepository.getModulesByResultId(getMostRecentResultId())
            modules
        }

    }

    suspend fun getResultById(resultId: Long): AnalysisResult {
        return analysisResultRepository.getResultById(resultId)
    }

    suspend fun getModulesByXYResultId(x: Int, y: Int, resultId: Long): List<ErrorModuleWithImage> {
        if (resultId != -1L) {
            return errorModulesRepository.getModulesByXYResultId(x, y, resultId)
        }
        return errorModulesRepository.getModulesByXYResultId(x, y, getMostRecentResultId())
    }

    suspend fun deleteResult(resultId: Long) {
        if (resultId != -1L) {
            analysisResultRepository.deleteById(resultId)
        } else {
            analysisResultRepository.deleteById(getMostRecentResultId())
        }
    }

    suspend fun deleteErrorModule(module: ErrorModule) {
        errorModulesRepository.deleteErrorModule(module)
    }

    suspend fun getSignageByResultId(resultId: Long): Signage? {
        return if (resultId != -1L) {
            val resultById = getResultById(resultId = resultId)
            getSignageById(resultById.signageId)
        } else { // 분석 마치고 왔을 때
            val resultById = getResultById(resultId = getMostRecentResultId())
            getSignageById(resultById.signageId)
        }
    }

    suspend fun getCabinetByResultId(resultId: Long): Cabinet? {
        return if (resultId != -1L) {
            val resultById = getResultById(resultId = resultId)
            getCabinet(signageId = resultById.signageId)
        } else {
            val resultById = getResultById(resultId = getMostRecentResultId())
            getCabinet(signageId = resultById.signageId)
        }
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

data class resultListState(val itemList: List<AnalysisResult> = listOf())