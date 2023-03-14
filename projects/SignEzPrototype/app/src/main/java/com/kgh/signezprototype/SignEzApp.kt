package com.kgh.signezprototype

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.VideoView
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kgh.signezprototype.ui.inputs.PictureViewModel
import com.kgh.signezprototype.ui.inputs.VideoViewModel
import com.kgh.signezprototype.ui.navigation.SignEzNavHost

@Composable
fun SignEzApp(
    navController: NavHostController = rememberNavController(),
    context: Context,
    activity: Activity,
    viewModel: PictureViewModel,
    viewModel2: VideoViewModel
    ) {
    SignEzNavHost(
        navController = navController,
        activity = activity,
        viewModel = viewModel,
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
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            ) },
            modifier = modifier,
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "뒤로 가기 버튼"
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { Text(title) }, modifier = modifier)
    }
}