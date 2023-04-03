package com.signez.signageproblemshooting.ui.signage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.data.repository.CabinetsRepository
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CabinetViewModel(private val cabinetRepository: CabinetsRepository) : ViewModel(),
    MediaViewModel {
    // Initialize this according to your app's architecture
    val cabinetListState: StateFlow<CabinetListState> =
        cabinetRepository.getAllCabinetsStream().map { CabinetListState(it) }
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

    fun insertTestRecord(context:Context) = viewModelScope.launch {
        // Save image as a Blob
//        val bitmap = createSimpleBitmap(100, 100, Color.RED)


        val drawable = ContextCompat.getDrawable(context, R.drawable.xpr)
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
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val byteArray = outputStream.toByteArray()

        val drawable2 = ContextCompat.getDrawable(context, R.drawable.xhb)
        // Convert drawable to Bitmap
        val bitmap2 = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bmp = drawable2?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable2.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable2.setBounds(0, 0, canvas.width, canvas.height)
                drawable2.draw(canvas)
            }
            bmp
        }
        val outputStream2 = ByteArrayOutputStream()
        bitmap2?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream2)
        val byteArray2 = outputStream.toByteArray()



        val testCabinet = Cabinet(
            id = 1L,
            name = "XPR",
            cabinetWidth = 1000.0,
            cabinetHeight = 1000.0,
            moduleColCount = 4,
            moduleRowCount = 4,
            repImg = byteArray
        )
        cabinetRepository.insertCabinet(testCabinet)

        val testCabinet23 = Cabinet(
            id = 2L,
            name = "XHB / Flat-23",
            cabinetWidth = 689.9,
            cabinetHeight = 1035.4,
            moduleColCount = 2,
            moduleRowCount = 3,
            repImg = byteArray2
        )
        val testCabinet22 = Cabinet(
            id = 3L,
            name = "XHB / Flat-22",
            cabinetWidth = 689.9,
            cabinetHeight = 690.4,
            moduleColCount = 2,
            moduleRowCount = 2,
            repImg = byteArray2
        )
        val testCabinet13 = Cabinet(
            id = 4L,
            name = "XHB / Flat-13",
            cabinetWidth = 344.6,
            cabinetHeight = 1035.4,
            moduleColCount = 1,
            moduleRowCount = 3,
            repImg = byteArray2
        )
        val testCabinet12 = Cabinet(
            id = 5L,
            name = "XHB / Flat-12",
            cabinetWidth = 344.6,
            cabinetHeight = 690.4,
            moduleColCount = 1,
            moduleRowCount = 2,
            repImg = byteArray2
        )
        val testCabinet43 = Cabinet(
            id = 6L,
            name = "XHB / Flat-43",
            cabinetWidth = 690.0,
            cabinetHeight = 1035.0,
            moduleColCount = 4,
            moduleRowCount = 3,
            repImg = byteArray2
        )
        val testCabinet42 = Cabinet(
            id = 7L,
            name = "XHB / Flat-42",
            cabinetWidth = 690.0,
            cabinetHeight = 690.0,
            moduleColCount = 4,
            moduleRowCount = 2,
            repImg = byteArray2
        )
        cabinetRepository.insertCabinet(testCabinet23)
        cabinetRepository.insertCabinet(testCabinet22)
        cabinetRepository.insertCabinet(testCabinet13)
        cabinetRepository.insertCabinet(testCabinet12)
        cabinetRepository.insertCabinet(testCabinet43)
        cabinetRepository.insertCabinet(testCabinet42)

        val drawable3 = ContextCompat.getDrawable(context, R.drawable.xps)
        // Convert drawable to Bitmap
        val bitmap3 = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bmp = drawable3?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable3.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable3.setBounds(0, 0, canvas.width, canvas.height)
                drawable3.draw(canvas)
            }
            bmp
        }
        val outputStream3 = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream3)
        val byteArray3 = outputStream.toByteArray()

        val testCabinet3 = Cabinet(
            id = 8L,
            name = "XPS",
            cabinetHeight = 1050.0,
            cabinetWidth = 700.0,
            moduleColCount = 3,
            moduleRowCount = 2,
            repImg = byteArray3
        )
        cabinetRepository.insertCabinet(testCabinet3)
    }

    fun saveItem(
        name: String,
        width: Double,
        height: Double,
        bitmap: Bitmap,
        colCount: Int,
        rowCount: Int
    ) = viewModelScope.launch {
        // Save image as a Blob
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val byteArray = outputStream.toByteArray()

        val newCabinet = Cabinet(
            name = name,
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