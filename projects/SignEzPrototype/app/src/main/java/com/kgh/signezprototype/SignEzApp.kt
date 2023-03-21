package com.kgh.signezprototype

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.VideoView
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kgh.signezprototype.ui.inputs.PictureViewModel
import com.kgh.signezprototype.ui.inputs.VideoViewModel
import com.kgh.signezprototype.ui.navigation.SignEzNavHost
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme

@Composable
fun SignEzApp(
    navController: NavHostController = rememberNavController(),
    activity: Activity,
    viewModel1: PictureViewModel,
    viewModel2: VideoViewModel
    ) {
    SignEzNavHost(
        navController = navController,
        activity = activity,
        viewModel1 = viewModel1,
        viewModel2 = viewModel2
        )
}
/**
 * App bar to display title and conditionally display the back navigation.
 */
@Composable
fun SignEzTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            title = { Text(
                text = title,
                style = MaterialTheme.typography.h2,
                color = MaterialTheme.colors.onSurface
            ) },
            modifier = modifier,
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "뒤로 가기 버튼"
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.onSurface
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            modifier = modifier,
            elevation = 0.dp
        )
    }
}

@Preview
@Composable
fun AppbarPreview() {
    SignEzPrototypeTheme(darkTheme = false) {
        SignEzTopAppBar(title = "SignEz", canNavigateBack = false)
    }
}
