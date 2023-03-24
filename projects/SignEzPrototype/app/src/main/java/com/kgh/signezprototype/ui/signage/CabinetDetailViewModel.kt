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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CabinetDetailViewModel(private val signageRepository: SignagesRepository, private val cabinetRepository: CabinetsRepository) : ViewModel(),
    MediaViewModel {

    override var mCurrentPhotoPath = mutableStateOf("")
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var type = 0;
    var defaultBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateRecord(name:String,width:Double,height:Double,bitmap:Bitmap?,colNum:Int,rowNum:Int,cabinet:Cabinet) = viewModelScope.launch {
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
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
            try {
                cabinetRepository.deleteCabinet(cabinet)
                success = true
            } catch (e: Exception) {
                success = false
            }
        }.join()
        return success
    }

    suspend fun getCabinet(cabinetId:Long): Cabinet {
        val cabinet: Cabinet =
                cabinetRepository.getNewCabinet(cabinetId)
        return cabinet
    }
}