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
import com.kgh.signezprototype.ui.home.HomeDestination
import com.kgh.signezprototype.ui.home.HomeScreen
import com.kgh.signezprototype.ui.inputs.*

@Composable
fun SignEzNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    activity: Activity,
    viewModel1: PictureViewModel,
    viewModel2: VideoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToVideo = { navController.navigate(VideoScreenDestination.route) },
                navigateToPicture = { navController.navigate(PictureScreenDestination.route) }
            )
        }

        composable(route = PictureScreenDestination.route) {
            PictureAnalysis(
                activity = activity,
                dispatchTakePictureIntent = ::dispatchTakePictureIntent,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel1//viewModel(factory = AppViewModelProvider.Factory)
            )
        }

        composable(route = VideoScreenDestination.route) {
            VideoAnalysis(
                activity = activity,
                dispatchTakeVideoIntent = ::dispatchTakeVideoIntent,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel2
            )
        }

    }
}
