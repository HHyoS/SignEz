package com.signez.signageproblemshooting.ui.analysis

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.entities.Cabinet
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.data.repository.CabinetsRepository
import com.signez.signageproblemshooting.data.repository.SignagesRepository
import com.signez.signageproblemshooting.ui.inputs.MediaViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetState
import com.signez.signageproblemshooting.ui.signage.SignageViewModel
import kotlinx.coroutines.flow.*


class AnalysisViewModel(private val signageRepository: SignagesRepository, private val cabinetRepository: CabinetsRepository): ViewModel(), MediaViewModel {
    override var imageUri = mutableStateOf(Uri.EMPTY)
    override var mCurrentPhotoPath = mutableStateOf("")
    override var type = 0; // 0 = 선택 x, 1 = 갤러리에서 골랐을 때, 2 = 앱에서 찍었을 때
    var videoContentUri = mutableStateOf(Uri.EMPTY)
    var imageContentUri = mutableStateOf(Uri.EMPTY)
    var signageId = mutableStateOf(-1L)

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun getCabinet(signageId:Long): Cabinet {
        val cabinet: Cabinet =
            cabinetRepository.getCabinetBySignageId(signageId)
        return cabinet
    }

    fun getSignage(): StateFlow<SignageState> {
        val signageState: StateFlow<SignageState> =
            signageRepository.getSignageStream(signageId.value)
                .filterNotNull()
                .map {
                    SignageState( signage = it)
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(AnalysisViewModel.TIMEOUT_MILLIS),
                    initialValue = SignageState()
                )
        return signageState
    }
}

data class SignageState(
    val signage: Signage = Signage(
        id = 3L,
        name="TEST",
        height =5.4,
        width=5.2,
        heightCabinetNumber = 5,
        widthCabinetNumber = 7,
        modelId = 1,
        repImg = byteArrayOf(1)
    )
)
