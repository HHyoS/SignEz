package com.signez.signageproblemshooting.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.data.entities.Signage
import com.signez.signageproblemshooting.ui.navigation.NavigationDestination
import com.signez.signageproblemshooting.ui.signage.SignageViewModel
import com.signez.signageproblemshooting.ui.signage.noRippleClickable

object ResultGridDetailDestination : NavigationDestination {
    override val route = "ResultGridDetailScreen"
    override val titleRes = "ResultGridDetail"
}

@Composable
fun ResultGridDetailView(
    onItemClick: (Signage) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: AnalysisViewModel,
    detailViewModel: SignageViewModel,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "사이트 정보 입력",
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Column(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {

            }
        }
    }
}