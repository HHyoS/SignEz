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
        val bitmap = createSimpleBitmap(100, 100, Color.RED)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        Log.d("testing",byteArray.size.toString() )

        val testAnalysisResult = Signage(id = 1L,
            name="TEST",
            height =5.4,
            width=5.2,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult)
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