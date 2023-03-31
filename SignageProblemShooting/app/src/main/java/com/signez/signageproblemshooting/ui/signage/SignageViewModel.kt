package com.signez.signageproblemshooting.ui.signage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.signez.signageproblemshooting.data.Converters
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.ErrorModule
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.data.repository.CabinetsRepository
import com.signez.signageproblemshooting.data.repository.SignagesRepository
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

class SignageViewModel(
    private val signageRepository: SignagesRepository,
    private val cabinetRepository: CabinetsRepository
) : ViewModel(),
    MediaViewModel {
    // Initialize this according to your app's architecture

    private val _selectedSignageId = MutableStateFlow<Long?>(null)
    val selectedSignageId: StateFlow<Long?> get() = _selectedSignageId
    var selectedCabinetId = mutableStateOf(-1L)
    override var mCurrentPhotoPath = mutableStateOf("")
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var type = 0;
    lateinit var cabinet: MutableState<Cabinet>
    val sWidth = mutableStateOf("") // 사이니지
    val sHeight = mutableStateOf("")  // 사이니지
    val sName = mutableStateOf("")
    val signageListState: StateFlow<SignageListState> =
        signageRepository.getAllSignagesStream().map { SignageListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SignageListState()
            )

    fun setSelectedSignageId(id: Long) {
        _selectedSignageId.value = id
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun init() {
        mCurrentPhotoPath.value = ""
        imageUri.value = Uri.EMPTY
        sWidth.value = ""
        sHeight.value = ""
        sName.value = ""
        selectedCabinetId.value = -1L
    }

    fun createSimpleBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }

    fun insertTestRecord() = viewModelScope.launch {
        // Save image as a Blob
        val bitmap = createSimpleBitmap(100, 100, Color.BLUE)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val byteArray = outputStream.toByteArray()
        Log.d("testing", byteArray.size.toString())

        val testAnalysisResult = Signage(
            id = 1L,
            name = "S 백화점 강남점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 19,
            widthCabinetNumber = 11,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult)
        val testAnalysisResult2 = Signage(
            id = 2L,
            name = "S 백화점 본점",
            height = 17000.0,
            width = 12000.0,
            heightCabinetNumber = 17,
            widthCabinetNumber = 12,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult2)
        val testAnalysisResult3 = Signage(
            id = 3L,
            name = "H 백화점 천호점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 19,
            widthCabinetNumber = 11,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult3)
        val testAnalysisResult4 = Signage(
            id = 4L,
            name = "H 백화점 목동점",
            height = 17000.0,
            width = 12000.0,
            heightCabinetNumber = 17,
            widthCabinetNumber = 12,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult4)
        val testAnalysisResult5 = Signage(
            id = 5L,
            name = "현대 백화점 대구점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult5)
        val testAnalysisResult6 = Signage(
            id = 6L,
            name = "현대 백화점 대구점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult6)
        val testAnalysisResult7 = Signage(
            id = 7L,
            name = "현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult7)
        val testAnalysisResult8 = Signage(
            id = 8L,
            name = "현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult8)
        val testAnalysisResult9 = Signage(
            id = 9L,
            name = "현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult9)
        val testAnalysisResult10 = Signage(
            id = 10L,
            name = "현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult10)
    }
    fun getCabinetById(modelId: Long) =
        runBlocking {
            return@runBlocking cabinetRepository.getNewCabinet(modelId)
        }

    fun saveItem(bitmap: Bitmap, modelId: Long = 0) = viewModelScope.launch {
        // Save image as a Blob
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val byteArray = outputStream.toByteArray()
        val this_cabinet = getCabinetById(modelId)
        val height = sHeight.value.toDouble()
        val width = sWidth.value.toDouble()
        val newSignage = Signage(
            name=sName.value,
            height = height,
            width= width,
            heightCabinetNumber = (height/this_cabinet.cabinetHeight).toInt(),
            widthCabinetNumber = (width/this_cabinet.cabinetWidth).toInt(),
            modelId = modelId,
            repImg = byteArray
        )
        signageRepository.insertSignage(newSignage)
    }

    fun getCabinet(): StateFlow<CabinetState> {
        val cabinetState: StateFlow<CabinetState> =
            cabinetRepository.getCabinetStream(selectedCabinetId.value)
                .filterNotNull()
                .map {
                    CabinetState(cabinet = it)
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = CabinetState()
                )
        return cabinetState
    }

    suspend fun getRelatedCabinet(signageId: Long): Cabinet {
        val cabinet: Cabinet =
            cabinetRepository.getCabinetBySignageId(signageId)
        return cabinet
    }

    suspend fun getSignageList(): List<Signage> {
        val signageList: List<Signage> =
            signageRepository.getSignageList()
        return signageList
    }
}


data class SignageListState(val itemList: List<Signage> = listOf())

data class CabinetState(
    val cabinet: Cabinet = Cabinet(
        id = 3L,
        name = "TEST",
        cabinetHeight = 5.4,
        cabinetWidth = 5.2,
        moduleColCount = 5,
        moduleRowCount = 7,
        repImg = byteArrayOf(1)
    )
)