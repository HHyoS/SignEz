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

package com.signez.signageproblemshooting.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.signez.signageproblemshooting.SignEzApplication
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.inputs.PictureViewModel
import com.signez.signageproblemshooting.ui.inputs.VideoViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetDetailViewModel
import com.signez.signageproblemshooting.ui.signage.CabinetViewModel
import com.signez.signageproblemshooting.ui.signage.SignageDetailViewModel
import com.signez.signageproblemshooting.ui.signage.SignageViewModel

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
            AnalysisViewModel(
                SignEzApplication().container.signagesRepository,
                SignEzApplication().container.cabinetsRepository,
                SignEzApplication().container.analysisResultsRepository,
                SignEzApplication().container.errorModulesRepository,
                SignEzApplication().container.errorImagesRepository,
                SignEzApplication())
        }

        initializer {
            SignageDetailViewModel(SignEzApplication().container.signagesRepository,SignEzApplication().container.cabinetsRepository)
        }
        initializer {
            CabinetDetailViewModel(SignEzApplication().container.cabinetsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.SignEzApplication(): SignEzApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SignEzApplication)
