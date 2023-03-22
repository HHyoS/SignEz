package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgh.signezprototype.data.Converters
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.data.repository.CabinetsRepository
import com.kgh.signezprototype.data.repository.SignagesRepository
import com.kgh.signezprototype.ui.inputs.MediaViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class SignageViewModel(private val signageRepository: SignagesRepository, private val cabinetRepository: CabinetsRepository) : ViewModel(),
    MediaViewModel {
     // Initialize this according to your app's architecture
     val signageListState: StateFlow<SignageListState> =
         signageRepository.getAllSignagesStream().map{ SignageListState(it) }
             .stateIn(
                 scope = viewModelScope,
                 started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                 initialValue = SignageListState()
             )
    private val _selectedSignageId = MutableStateFlow<Long?>(null)
    val selectedSignageId: StateFlow<Long?> get() = _selectedSignageId
    var selectedCabinetId = mutableStateOf(-1L)
    override var mCurrentPhotoPath = mutableStateOf("")
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var type = 0;
    lateinit var cabinet: MutableState<Cabinet>

    fun setSelectedSignageId(id: Long) {
        _selectedSignageId.value = id
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        Log.d("testing",byteArray.size.toString() )

        val testAnalysisResult = Signage(id = 1L,
            name="신세계 백화점 강남점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult)
        val testAnalysisResult2 = Signage(id = 2L,
            name="신세계 백화점 본점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult2)
        val testAnalysisResult3 = Signage(id = 3L,
            name="현대 백화점 천호점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult3)
        val testAnalysisResult4 = Signage(id = 4L,
            name="현대 백화점 목동점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult4)
        val testAnalysisResult5 = Signage(id = 5L,
            name="현대 백화점 대구점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult5)
        val testAnalysisResult6 = Signage(id = 6L,
            name="현대 백화점 대구점",
            height = 19000.0,
            width = 11000.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult6)
        val testAnalysisResult7 = Signage(id = 7L,
            name="현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult7)
        val testAnalysisResult8 = Signage(id = 8L,
            name="현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult8)
        val testAnalysisResult9 = Signage(id = 9L,
            name="현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult9)
        val testAnalysisResult10 = Signage(id = 10L,
            name="현대 백화점 대구점",
            height = 1900.0,
            width = 1100.0,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult10)
    }

    fun saveItem(name:String,width:Double,height:Double,bitmap:Bitmap,modelId:Long=0) = viewModelScope.launch {
        // Save image as a Blob
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        val newSignage = Signage(
            name=name,
            height =height,
            width=width,
            heightCabinetNumber = 1,
            widthCabinetNumber = 1,
            modelId = modelId,
            repImg = byteArray
        )
        signageRepository.insertSignage(newSignage)
    }

    fun getCabinet():StateFlow<CabinetState> {
        val cabinetState: StateFlow<CabinetState> =
            cabinetRepository.getCabinetStream(selectedCabinetId.value)
                .filterNotNull()
                .map {
                    CabinetState( cabinet = it)
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = CabinetState()
                )
        return cabinetState
    }

    suspend fun getRelatedCabinet(signageId:Long): Cabinet {
        val cabinet: Cabinet =
            cabinetRepository.getCabinetBySignageId(signageId)
        return cabinet
    }

}


data class SignageListState(val itemList: List<Signage> = listOf())

data class CabinetState(
    val cabinet: Cabinet = Cabinet(
        id = 3L,
        name="TEST",
        cabinetHeight = 5.4,
        cabinetWidth = 5.2,
        moduleColCount = 5,
        moduleRowCount = 7,
        repImg = byteArrayOf(1)
    )
)