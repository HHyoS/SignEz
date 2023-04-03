package com.signez.signageproblemshooting.ui.signage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.entities.Cabinet
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

    fun insertTestRecord(context: Context) = viewModelScope.launch {
        // Save image as a Blob

        // -------------
        val drawable1 = ContextCompat.getDrawable(context, R.drawable.hdsky_xpr)
        // Convert drawable to Bitmap
        val bitmap1 = if (drawable1 is BitmapDrawable) {
            drawable1.bitmap
        } else {
            val bmp = drawable1?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable1.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable1.setBounds(0, 0, canvas.width, canvas.height)
                drawable1.draw(canvas)
            }
            bmp
        }
        val outputStream1 = ByteArrayOutputStream()
        bitmap1?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream1)
        val byteArray1 = outputStream1.toByteArray()

        // ----------------------------------------
        val drawable2 = ContextCompat.getDrawable(context, R.drawable.hdparkone_xpr)
        // Convert drawable to Bitmap
        val bitmap2 = if (drawable2 is BitmapDrawable) {
            drawable2.bitmap
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
        val byteArray2 = outputStream2.toByteArray()

        // ----------------------------------------
        val drawable3 = ContextCompat.getDrawable(context, R.drawable.lion_xpr)
        // Convert drawable to Bitmap
        val bitmap3 = if (drawable3 is BitmapDrawable) {
            drawable3.bitmap
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
        bitmap3?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream3)
        val byteArray3 = outputStream3.toByteArray()

        // ----------------------------------------
        val drawable4 = ContextCompat.getDrawable(context, R.drawable.hansum_xpr)
        // Convert drawable to Bitmap
        val bitmap4 = if (drawable4 is BitmapDrawable) {
            drawable4.bitmap
        } else {
            val bmp = drawable4?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable4.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable4.setBounds(0, 0, canvas.width, canvas.height)
                drawable4.draw(canvas)
            }
            bmp
        }
        val outputStream4 = ByteArrayOutputStream()
        bitmap4?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream4)
        val byteArray4 = outputStream4.toByteArray()

        // ----------------------------------------
        val drawable5 = ContextCompat.getDrawable(context, R.drawable.mets_xpr)
        // Convert drawable to Bitmap
        val bitmap5 = if (drawable5 is BitmapDrawable) {
            drawable5.bitmap
        } else {
            val bmp = drawable5?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable5.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable5.setBounds(0, 0, canvas.width, canvas.height)
                drawable5.draw(canvas)
            }
            bmp
        }
        val outputStream5 = ByteArrayOutputStream()
        bitmap5?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream5)
        val byteArray5 = outputStream5.toByteArray()

        // ----------------------------------------
        val drawable6 = ContextCompat.getDrawable(context, R.drawable.newnew_xpr)
        // Convert drawable to Bitmap
        val bitmap6 = if (drawable6 is BitmapDrawable) {
            drawable6.bitmap
        } else {
            val bmp = drawable6?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable6.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable6.setBounds(0, 0, canvas.width, canvas.height)
                drawable6.draw(canvas)
            }
            bmp
        }
        val outputStream6 = ByteArrayOutputStream()
        bitmap6?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream6)
        val byteArray6 = outputStream6.toByteArray()

        // ----------------------------------------
        val drawable7 = ContextCompat.getDrawable(context, R.drawable.foothill_xhb)
        // Convert drawable to Bitmap
        val bitmap7 = if (drawable7 is BitmapDrawable) {
            drawable7.bitmap
        } else {
            val bmp = drawable7?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable7.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable7.setBounds(0, 0, canvas.width, canvas.height)
                drawable7.draw(canvas)
            }
            bmp
        }
        val outputStream7 = ByteArrayOutputStream()
        bitmap7?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream7)
        val byteArray7 = outputStream7.toByteArray()

        // ----------------------------------------
        val drawable8 = ContextCompat.getDrawable(context, R.drawable.huston_xhb)
        // Convert drawable to Bitmap
        val bitmap8 = if (drawable8 is BitmapDrawable) {
            drawable8.bitmap
        } else {
            val bmp = drawable8?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable8.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable8.setBounds(0, 0, canvas.width, canvas.height)
                drawable8.draw(canvas)
            }
            bmp
        }
        val outputStream8 = ByteArrayOutputStream()
        bitmap8?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream8)
        val byteArray8 = outputStream8.toByteArray()

        // ----------------------------------------
        val drawable9 = ContextCompat.getDrawable(context, R.drawable.mets_xhb)
        // Convert drawable to Bitmap
        val bitmap9 = if (drawable9 is BitmapDrawable) {
            drawable9.bitmap
        } else {
            val bmp = drawable9?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable9.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable9.setBounds(0, 0, canvas.width, canvas.height)
                drawable9.draw(canvas)
            }
            bmp
        }
        val outputStream9 = ByteArrayOutputStream()
        bitmap9?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream9)
        val byteArray9 = outputStream9.toByteArray()

        // ----------------------------------------
        val drawable10 = ContextCompat.getDrawable(context, R.drawable.pathe_xhb)
        // Convert drawable to Bitmap
        val bitmap10 = if (drawable10 is BitmapDrawable) {
            drawable10.bitmap
        } else {
            val bmp = drawable10?.let { Bitmap.createBitmap(it.intrinsicWidth, drawable10.intrinsicHeight, Bitmap.Config.ARGB_8888) }
            val canvas = bmp?.let { Canvas(it) }
            if (canvas != null) {
                drawable10.setBounds(0, 0, canvas.width, canvas.height)
                drawable10.draw(canvas)
            }
            bmp
        }
        val outputStream10 = ByteArrayOutputStream()
        bitmap10?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream10)
        val byteArray10 = outputStream10.toByteArray()

        // ----------------------------------------



        val testAnalysisResult = Signage(
            id = 1L,
            name = "H 백화점 CH점",
            width = 11000.0,
            height = 19000.0,
            widthCabinetNumber = 11,
            heightCabinetNumber = 19,
            modelId = 1,
            repImg = byteArray1
        )
        signageRepository.insertSignage(testAnalysisResult)

        val testAnalysisResult2 = Signage(
            id = 2L,
            name = "H 백화점 Y점",
            width = 11000.0,
            height = 19000.0,
            widthCabinetNumber = 11,
            heightCabinetNumber = 19,
            modelId = 1,
            repImg = byteArray2
        )
        signageRepository.insertSignage(testAnalysisResult2)

        val testAnalysisResult3 = Signage(
            id = 3L,
            name = "S 사자 공원",
            width = 36000.0,
            height = 20000.0,
            widthCabinetNumber = 36,
            heightCabinetNumber = 20,
            modelId = 1,
            repImg = byteArray3
        )
        signageRepository.insertSignage(testAnalysisResult3)

        val testAnalysisResult4 = Signage(
            id = 4L,
            name = "HS 빌딩",
            width = 11000.0,
            height = 19000.0,
            widthCabinetNumber = 11,
            heightCabinetNumber = 19,
            modelId = 1,
            repImg = byteArray4
        )
        signageRepository.insertSignage(testAnalysisResult4)

        val testAnalysisResult5 = Signage(
            id = 5L,
            name = "Old York Mats A",
            width = 13000.0,
            height = 7000.0,
            widthCabinetNumber = 13,
            heightCabinetNumber = 7,
            modelId = 1,
            repImg = byteArray5
        )
        signageRepository.insertSignage(testAnalysisResult5)

        val testAnalysisResult6 = Signage(
            id = 6L,
            name = "S 백화점 신관",
            width = 5000.0,
            height = 16000.0,
            widthCabinetNumber = 5,
            heightCabinetNumber = 16,
            modelId = 1,
            repImg = byteArray6
        )
        signageRepository.insertSignage(testAnalysisResult6)

        val testAnalysisResult7 = Signage(
            id = 7L,
            name = "F언덕 차도",
            width = 3450.0,
            height = 2070.0,
            widthCabinetNumber = 5,
            heightCabinetNumber = 3,
            modelId = 7,
            repImg = byteArray7
        )
        signageRepository.insertSignage(testAnalysisResult7)

        val testAnalysisResult8 = Signage(
            id = 8L,
            name = "H-ton Stars Stadium",
            width = 37260.0,
            height = 15870.0,
            widthCabinetNumber = 54,
            heightCabinetNumber = 23,
            modelId = 7,
            repImg = byteArray8
        )
        signageRepository.insertSignage(testAnalysisResult8)

        val testAnalysisResult9 = Signage(
            id = 9L,
            name = "Old York Mats B",
            width = 31740.0,
            height = 15870.0,
            widthCabinetNumber = 46,
            heightCabinetNumber = 23,
            modelId = 7,
            repImg = byteArray9
        )
        signageRepository.insertSignage(testAnalysisResult9)
        val testAnalysisResult10 = Signage(
            id = 10L,
            name = "파세 알레샤",
            width = 4128.0,
            height = 3105.0,
            widthCabinetNumber = 12,
            heightCabinetNumber = 3,
            modelId = 7,
            repImg = byteArray10
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