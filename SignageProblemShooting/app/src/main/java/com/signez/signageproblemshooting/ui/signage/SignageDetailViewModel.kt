package com.signez.signageproblemshooting.ui.signage

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.data.repository.CabinetsRepository
import com.signez.signageproblemshooting.data.repository.SignagesRepository
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    fun getCabinetById(modelId: Long) =
        runBlocking {
            return@runBlocking cabinetRepository.getNewCabinet(modelId)
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
        val thisCabinet = getCabinetById(signage.modelId)
        signage.heightCabinetNumber = (signage.height/thisCabinet.cabinetHeight).toInt()
        signage.widthCabinetNumber = (signage.width/thisCabinet.cabinetWidth).toInt()

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
        return signageRepository.getSignageById(signageId)
    }

    suspend fun getNewCabinet(): Cabinet {
        return cabinetRepository.getNewCabinet(newCabinetId.value)
    }
}