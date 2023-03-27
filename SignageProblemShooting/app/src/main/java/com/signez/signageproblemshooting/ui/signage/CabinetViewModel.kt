package com.signez.signageproblemshooting.ui.signage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.data.repository.CabinetsRepository
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CabinetViewModel(private val cabinetRepository: CabinetsRepository) : ViewModel(),
    MediaViewModel {
    // Initialize this according to your app's architecture
    val cabinetListState: StateFlow<CabinetListState> =
        cabinetRepository.getAllCabinetsStream().map{ CabinetListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CabinetListState()
            )
    private val _selectedCabinetId = MutableStateFlow<Long?>(null)
    val selectedCabinetId: StateFlow<Long?> get() = _selectedCabinetId
    override var mCurrentPhotoPath = mutableStateOf("")
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var type = 0;

    fun setSelectedCabinetId(id: Long) {
        _selectedCabinetId.value = id
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val byteArray = outputStream.toByteArray()

        val testAnalysisResult = Cabinet(id = 1L,
            name="TEST",
            cabinetHeight = 5.4,
            cabinetWidth = 5.2,
            moduleColCount = 5,
            moduleRowCount = 7,
            repImg = byteArray
        )
        cabinetRepository.insertCabinet(testAnalysisResult)
    }

    fun saveItem(name:String, width:Double, height:Double, bitmap: Bitmap, colCount:Int, rowCount:Int) = viewModelScope.launch {
        // Save image as a Blob
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        val newCabinet = Cabinet(
            name=name,
            cabinetHeight = height,
            cabinetWidth = width,
            moduleColCount = colCount,
            moduleRowCount = rowCount,
            repImg = byteArray
        )
        cabinetRepository.insertCabinet(newCabinet)
    }
}


data class CabinetListState(val itemList: List<Cabinet> = listOf())