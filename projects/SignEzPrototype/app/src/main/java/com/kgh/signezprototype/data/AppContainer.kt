package com.kgh.signezprototype.data

import android.content.Context
import com.kgh.signezprototype.data.repository.*

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val analysisResultsRepository : AnalysisResultsRepository
    val cabinetsRepository : CabinetsRepository
    val errorImagesRepository : ErrorImagesRepository
    val errorModulesRepository : ErrorModulesRepository
    val signagesRepository : SignagesRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineAnalysisResultsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [Repositories]
     */
    override val analysisResultsRepository: AnalysisResultsRepository by lazy {
        OfflineAnalysisResultsRepository(SignEzDatabase.getDatabase(context).resultDao())
    }

    override val cabinetsRepository: CabinetsRepository by lazy {
        OfflineCabinetsRepository(SignEzDatabase.getDatabase(context).cabinetDao())
    }

    override val errorImagesRepository: ErrorImagesRepository by lazy {
        OfflineErrorImagesRepository(SignEzDatabase.getDatabase(context).imageDao())
    }

    override val errorModulesRepository: ErrorModulesRepository by lazy {
        OfflineErrorModulesRepository(SignEzDatabase.getDatabase(context).errorModuleDao())
    }

    override val signagesRepository: SignagesRepository by lazy {
        OfflineSignagesRepository(SignEzDatabase.getDatabase(context).signageDao())
    }
    // Initialize the repositories -eager
//    override val analysisResultsRepository: AnalysisResultsRepository = OfflineAnalysisResultsRepository(SignEzDatabase.getDatabase(context).resultDao())
//    override val cabinetsRepository: CabinetsRepository = OfflineCabinetsRepository(SignEzDatabase.getDatabase(context).cabinetDao())
//    override val errorImagesRepository: ErrorImagesRepository = OfflineErrorImagesRepository(SignEzDatabase.getDatabase(context).imageDao())
//    override val errorModulesRepository: ErrorModulesRepository = OfflineErrorModulesRepository(SignEzDatabase.getDatabase(context).errorModuleDao())
//    override val signagesRepository: SignagesRepository = OfflineSignagesRepository(SignEzDatabase.getDatabase(context).signageDao())
}