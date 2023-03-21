package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgh.signezprototype.data.Converters
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.data.repository.SignagesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class SignageViewModel(private val signageRepository: SignagesRepository) : ViewModel() {
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

        val testAnalysisResult = Signage(id = 3L,
            name="TEST",
            height =5.4,
            width=5.2,
            heightCabinetNumber = 5,
            widthCabinetNumber = 7,
            modelId = 3,
            repImg = byteArray
        )
        signageRepository.insertSignage(testAnalysisResult)


//         Query the test record
//        val retrievedAnalysisResult = signageRepository.getSignageStream(2)

//         Log the retrieved data
//        Log.d("MainViewModel", "Retrieved Analysis Result: $retrievedAnalysisResult")
    }
}


data class SignageListState(val itemList: List<Signage> = listOf())