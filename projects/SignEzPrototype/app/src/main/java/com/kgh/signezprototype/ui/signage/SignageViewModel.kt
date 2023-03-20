package com.kgh.signezprototype.ui.signage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.data.repository.SignagesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SignageViewModel(private val signageRepository: SignagesRepository) : ViewModel() {
     // Initialize this according to your app's architecture
     val signageListState: StateFlow<SignageListState> =
         signageRepository.getAllSignagesStream().map{ SignageListState(it) }
             .stateIn(
                 scope = viewModelScope,
                 started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                 initialValue = SignageListState()
             )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun insertTestRecord() = viewModelScope.launch {
        val testAnalysisResult = Signage(id = 2L,  name="TEST", height =3.4, width=5.2, heightCabinetNumber = 5, widthCabinetNumber = 7, modelId = 3)
        signageRepository.insertSignage(testAnalysisResult)

        // Query the test record
//        val retrievedAnalysisResult = analysisResultsRepository.getResultStream(2)

        // Log the retrieved data
//        Log.d("MainViewModel", "Retrieved Analysis Result: $retrievedAnalysisResult")
    }
}


data class SignageListState(val itemList: List<Signage> = listOf())