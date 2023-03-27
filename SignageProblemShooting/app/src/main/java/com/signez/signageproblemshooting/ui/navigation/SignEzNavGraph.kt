package com.signez.signageproblemshooting.ui.navigation

import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.signez.signageproblemshooting.SignEzApplication
import com.signez.signageproblemshooting.ui.AppViewModelProvider
import com.signez.signageproblemshooting.ui.analysis.AnalysisViewModel
import com.signez.signageproblemshooting.ui.home.HomeDestination
import com.signez.signageproblemshooting.ui.home.HomeScreen
import com.signez.signageproblemshooting.ui.inputs.*
import com.signez.signageproblemshooting.ui.signage.*
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
                viewModel =  viewModel5,
                detailViewModel = viewModel3,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(route = AddSignageDestination.route) {
            AddSignageScreen(
                activity=activity,
                viewModel = viewModel3,
                navController = navController,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },)
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
                    mode=it,
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() },
                )
            }

        }

        composable(route = AddCabinetDestination.route) {
            AddCabinetScreen(
                activity=activity,
                viewModel = viewModel4,
                navController = navController,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },)
        }

        composable(route = DetailSignageScreenDestination.route+"/{signageId}") {
            backStackEntry ->
            backStackEntry.arguments?.getString("signageId")?.let {
                SDetail(navController,
                    signageId=it.toLong(),
                    activity=activity,
                    viewModel=viewModel6,
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() },) }
        }
        composable(route = DetailCabinetScreenDestination.route+"/{cabinetId}") {
                backStackEntry ->
            backStackEntry.arguments?.getString("cabinetId")?.let {
                CDetail(navController,
                    cabinetId=it.toLong(),
                    activity=activity,
                    viewModel=viewModel7,
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() },) }
        }
    }
}