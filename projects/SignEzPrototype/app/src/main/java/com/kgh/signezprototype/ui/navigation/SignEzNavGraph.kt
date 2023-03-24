package com.kgh.signezprototype.ui.navigation

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kgh.signezprototype.SignEzApplication
import com.kgh.signezprototype.ui.AppViewModelProvider
import com.kgh.signezprototype.ui.analysis.AnalysisViewModel
import com.kgh.signezprototype.ui.home.HomeDestination
import com.kgh.signezprototype.ui.home.HomeScreen
import com.kgh.signezprototype.ui.inputs.*
import com.kgh.signezprototype.ui.signage.*
import kotlin.math.sign

@Composable
fun SignEzNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    activity: Activity,
    viewModel1: PictureViewModel,
    viewModel2: VideoViewModel,
    viewModel3: SignageViewModel,
    viewModel4: CabinetViewModel,
    viewModel5: AnalysisViewModel,
    viewModel6: SignageDetailViewModel,
    viewModel7: CabinetDetailViewModel
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToVideo = { navController.navigate(VideoScreenDestination.route) },
                navigateToPicture = { navController.navigate(PictureScreenDestination.route) },
                navigateToSignageList = { navController.navigate(SignageListScreenDestination.route) },
                viewModel = viewModel5
            )
        }

        composable(route = PictureScreenDestination.route) {
            PictureAnalysis(
                activity = activity,
                dispatchTakePictureIntent = ::dispatchTakePictureIntent,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel1,//viewModel(factory = AppViewModelProvider.Factory)
                analysisViewModel = viewModel5
            )
        }

        composable(route = VideoScreenDestination.route) {
            VideoAnalysis(
                activity = activity,
                dispatchTakeVideoIntent = ::dispatchTakeVideoIntent,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel2,
                analysisViewModel = viewModel5
            )
        }

        composable(route = SignageListScreenDestination.route) {
            SignageInformationScreen(
                onItemClick = {},
                modifier = Modifier,
                navController = navController,
                viewModel =  viewModel5
            )
        }

        composable(route = AddSignageDestination.route) {
            AddSignageScreen(
                activity=activity,
                viewModel = viewModel3,
                navController = navController)
        }

        composable(route = CabinetListScreenDestination.route+"/{mode}") {
            backStackEntry ->
            backStackEntry.arguments?.getString("mode")?.let {
                CabinetInformationScreen(
                    onItemClick = {},
                    modifier = Modifier,
                    navController = navController,
                    signageViewModel =  viewModel3,
                    detailViewModel = viewModel6,
                    mode=it
                )
            }

        }

        composable(route = AddCabinetDestination.route) {
            AddCabinetScreen(
                activity=activity,
                viewModel = viewModel4,
                navController = navController)
        }

        composable(route = DetailSignageScreenDestination.route+"/{signageId}") {
            backStackEntry ->
            backStackEntry.arguments?.getString("signageId")?.let {
                SDetail(navController,
                    signageId=it.toLong(),
                    activity=activity,
                    viewModel=viewModel6) }
        }
        composable(route = DetailCabinetScreenDestination.route+"/{cabinetId}") {
                backStackEntry ->
            backStackEntry.arguments?.getString("cabinetId")?.let {
                CDetail(navController,
                    cabinetId=it.toLong(),
                    activity=activity,
                    viewModel=viewModel7) }
        }
    }
}
