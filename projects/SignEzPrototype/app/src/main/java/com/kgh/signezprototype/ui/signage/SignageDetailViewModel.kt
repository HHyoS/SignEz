package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgh.signezprototype.data.entities.Cabinet
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.data.repository.CabinetsRepository
import com.kgh.signezprototype.data.repository.SignagesRepository
import com.kgh.signezprototype.ui.analysis.AnalysisViewModel
import com.kgh.signezprototype.ui.analysis.SignageState
import com.kgh.signezprototype.ui.inputs.MediaViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class SignageDetailViewModel(private val signageRepository: SignagesRepository, private val cabinetRepository: CabinetsRepository) : ViewModel(),
    MediaViewModel {

    override var mCurrentPhotoPath = mutableStateOf("")
    override var imageUri = mutableStateOf(Uri.EMPTY)
    val sWidth = mutableStateOf("") // 사이니지
    val sHeight = mutableStateOf("")  // 사이니지
    val sName = mutableStateOf("")

    override var type = 0;
    lateinit var cabinet: MutableState<Cabinet>
    var newCabinetId = mutableStateOf(-1L)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun init() {
        mCurrentPhotoPath.value = ""
        imageUri.value = Uri.EMPTY
        sWidth.value = ""
        sHeight.value = ""
        sName.value = ""
        newCabinetId.value = -1L
    }

    fun updateRecord(bitmap:Bitmap?,signage:Signage) = viewModelScope.launch {
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val byteArray = outputStream.toByteArray()
            signage.repImg = byteArray
        }
        signage.name = sName.value
        signage.width = sWidth.value.toDouble()
        signage.height = sHeight.value.toDouble()

        // 모듈 개수 계산식 반영 필요
        signage.modelId = if (newCabinetId.value > -1 ) {
            newCabinetId.value
        }
        else {
            signage.modelId
        }
        signageRepository.updateSignage(signage)
    }

    fun delete(signage:Signage) = viewModelScope.launch {
        signageRepository.deleteSignage(signage)
    }

    suspend fun getCabinet(signageId:Long): Cabinet {
        val cabinet: Cabinet =
            if (newCabinetId.value > -1) {
                cabinetRepository.getNewCabinet(newCabinetId.value)
            } else {
                cabinetRepository.getCabinetBySignageId(signageId)
            }
        return cabinet
    }

    suspend fun getSignage(signageId: Long): Signage {
        val signage: Signage =
            signageRepository.getSignageById(signageId)
        return signage
    }

    suspend fun getNewCabinet(): Cabinet {
        val cabinet: Cabinet =
            cabinetRepository.getNewCabinet(newCabinetId.value)
        return cabinet
    }
}