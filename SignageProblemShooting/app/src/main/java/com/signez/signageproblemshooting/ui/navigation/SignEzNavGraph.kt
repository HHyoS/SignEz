package com.signez.signageproblemshooting.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.signez.signageproblemshooting.ui.analysis.*
import com.signez.signageproblemshooting.ui.home.HomeDestination
import com.signez.signageproblemshooting.ui.home.HomeScreen
import com.signez.signageproblemshooting.ui.inputs.*
import com.signez.signageproblemshooting.ui.signage.*

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
    viewModel7: CabinetDetailViewModel,
    viewModel8: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToVideo = {
                    if(viewModel5.signageId.value > -1){
                        navController.navigate(VideoScreenDestination.route)
                    } else {
                        Toast.makeText(activity,"사이니지를 선택 후 진행해주세요.", Toast.LENGTH_SHORT).show()
                    }
                                  },
                navigateToPicture = {
                    if(viewModel5.signageId.value > -1){
                        navController.navigate(PictureScreenDestination.route)
                    } else {
                        Toast.makeText(activity,"사이니지를 선택 후 진행해주세요.", Toast.LENGTH_SHORT).show()
                    }
                                    },
                navigateToSignageList = { navController.navigate(SignageListScreenDestination.route) },
                viewModel = viewModel5,
                mainViewModel = viewModel8,
                navController = navController
            )
        }

        composable(route = PictureScreenDestination.route) {
            PictureAnalysis(
                activity = activity,
                dispatchTakePictureIntent = ::dispatchTakePictureIntent,
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel1,//viewModel(factory = AppViewModelProvider.Factory)
                analysisViewModel = viewModel5,
            )
        }

        composable(route = VideoScreenDestination.route) {
            VideoAnalysis(
                activity = activity,
                dispatchTakeVideoIntent = ::dispatchTakeVideoIntent,
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel2,
                analysisViewModel = viewModel5,
            )
        }

        composable(route = SignageListScreenDestination.route) {
            SignageInformationScreen(
                modifier = Modifier,
                navController = navController,
                viewModel =  viewModel5,
                detailViewModel = viewModel3,
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable(route = AddSignageDestination.route) {
            AddSignageScreen(
                activity=activity,
                viewModel = viewModel3,
                navController = navController,
                onNavigateUp = { navController.navigateUp() },)
        }

        composable(route = CabinetListScreenDestination.route+"/{mode}") {
            backStackEntry ->
            backStackEntry.arguments?.getString("mode")?.let {
                CabinetInformationScreen(
                    modifier = Modifier,
                    navController = navController,
                    signageViewModel =  viewModel3,
                    detailViewModel = viewModel6,
                    mode=it,
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
                SDetail(
                    navController = navController,
                    signageId=it.toLong(),
                    activity=activity,
                    viewModel=viewModel6,
                    onNavigateUp = { navController.navigateUp() }) }
        }
        composable(route = DetailCabinetScreenDestination.route+"/{cabinetId}") {
                backStackEntry ->
            backStackEntry.arguments?.getString("cabinetId")?.let {
                CDetail(
                    navController = navController,
                    cabinetId=it.toLong(),
                    activity=activity,
                    viewModel=viewModel7,
                    onNavigateUp = { navController.navigateUp() })
            }
        }

        composable(route = ResultsHistoryDestination.route) {
            ResultsHistoryView(
                modifier = Modifier,
                navController = navController,
                onNavigateUp = { navController.navigateUp() },
                viewModel = viewModel5
            )

        }

        composable(route = ResultGridDestination.route+"/{resultId}") {
            backStackEntry ->
            backStackEntry.arguments?.getString("resultId")?.let {
                ResultGridView(
                    modifier = Modifier,
                    navController = navController,
                    onNavigateUp = { navController.navigateUp() },
                    viewModel = viewModel5,
                    resultId = it.toLong()
                )
            }
        }

        composable(route = ErrorImageDestination.route+"/{x}/{y}/{resultId}") {
            backStackEntry ->
            backStackEntry.arguments?.let {
                ErrorImageView(
                    modifier = Modifier,
                    onNavigateUp = { navController.navigateUp() },
                    viewModel = viewModel5,
                    x = it.getString("x")!!.toInt(),
                    y = it.getString("y")!!.toInt(),
                    resultId = it.getString("resultId")!!.toLong()
                )
            }
        }

        composable(route = BlockLayoutDestination.route+"/{signageId}/{cabinetId}") {
            backStackEntry ->
            backStackEntry.arguments?.let {
                LayoutScreen(
                    signageId = it.getString("signageId")!!,
                    cabinetId = it.getString("cabinetId")!!,
                    navController = navController,
                    onNavigateUp = { navController.navigateUp() },
                )
            }
        }

    }
}
