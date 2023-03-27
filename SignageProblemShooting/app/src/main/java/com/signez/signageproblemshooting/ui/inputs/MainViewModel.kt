package com.signez.signageproblemshooting.ui.inputs

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.signez.signageproblemshooting.data.AppContainer
import com.signez.signageproblemshooting.data.entities.AnalysisResult
import kotlinx.coroutines.launch


class MainViewModel(appContainer: AppContainer) : ViewModel() {
    private val analysisResultsRepository = appContainer.analysisResultsRepository

    fun insertTestRecord() = viewModelScope.launch {
        val testAnalysisResult = AnalysisResult(id = 2, signageId = 1L, resultDate="1")
        analysisResultsRepository.insertResult(testAnalysisResult)

        // Query the test record
        val retrievedAnalysisResult = analysisResultsRepository.getResultStream(2)

        // Log the retrieved data
        Log.d("MainViewModel", "Retrieved Analysis Result: $retrievedAnalysisResult")
    }
}