package com.signez.signageproblemshooting.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.signez.signageproblemshooting.SignEzTopAppBar
import com.signez.signageproblemshooting.ui.components.LoadingSpinner
import com.signez.signageproblemshooting.ui.signage.noRippleClickable

@Composable
fun AnalysisProgress(
    modifier: Modifier = Modifier,
    analysisViewModel: AnalysisViewModel
) {
    val focusManager = LocalFocusManager.current
    androidx.compose.material.Scaffold(
        modifier = Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .background(MaterialTheme.colors.background),
        topBar = {
            SignEzTopAppBar(
                title = "분석 결과",
                canNavigateBack = false,
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
                contentAlignment = Alignment.Center
            ) {
                LoadingSpinner(analysisViewModel.progressMessage.value, analysisViewModel.progressFloat.value)
            }
        }
    }
}