package com.signez.signageproblemshooting.ui.signage

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.repository.CabinetsRepository
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CabinetDetailViewModel(private val cabinetRepository: CabinetsRepository) : ViewModel(),
    MediaViewModel {

    override var mCurrentPhotoPath = mutableStateOf("")
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var type = 0;
    fun updateRecord(name:String,width:Double,height:Double,bitmap:Bitmap?,colNum:Int,rowNum:Int,cabinet:Cabinet) = viewModelScope.launch {
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val byteArray = outputStream.toByteArray()
            cabinet.repImg = byteArray
        }
        cabinet.name = name
        cabinet.cabinetWidth = width
        cabinet.cabinetHeight = height
        cabinet.moduleColCount = colNum
        cabinet.moduleRowCount = rowNum
        cabinetRepository.updateCabinet(cabinet)
    }

    suspend fun delete(cabinet:Cabinet) :Boolean {
        var success = false
        viewModelScope.launch {
            success = try {
                cabinetRepository.deleteCabinet(cabinet)
                true
            } catch (e: Exception) {
                false
            }
        }.join()
        return success
    }

    suspend fun getCabinet(cabinetId: Long): Cabinet {
        return cabinetRepository.getNewCabinet(cabinetId)
    }
}