/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kgh.signezprototype.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kgh.signezprototype.SignEzApplication
import com.kgh.signezprototype.ui.analysis.AnalysisViewModel
import com.kgh.signezprototype.ui.inputs.MainViewModel
import com.kgh.signezprototype.ui.inputs.PictureViewModel
import com.kgh.signezprototype.ui.inputs.VideoViewModel
import com.kgh.signezprototype.ui.signage.CabinetViewModel
import com.kgh.signezprototype.ui.signage.SignageViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            PictureViewModel()
        }

        // Initializer for HomeViewModel
        initializer {
            VideoViewModel()
        }

        initializer {
            SignageViewModel(SignEzApplication().container.signagesRepository,SignEzApplication().container.cabinetsRepository)
        }

        initializer {
            CabinetViewModel(SignEzApplication().container.cabinetsRepository)
        }

        initializer {
            AnalysisViewModel(SignEzApplication().container.signagesRepository,SignEzApplication().container.cabinetsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.SignEzApplication(): SignEzApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SignEzApplication)
