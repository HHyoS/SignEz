package com.kgh.signezprototype

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.Menu
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
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

    var dropDownMenuExpanded by remember {
        mutableStateOf(false)
    }


    if (canNavigateBack) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            },
            modifier = modifier,
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_ios_new_fill1_wght300_grad0_opsz48),
                        contentDescription = "뒤로 가기 버튼",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    // show the drop down menu
                    dropDownMenuExpanded = true
                }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More Vert",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
                // drop down menu
                DropdownMenu(
                    expanded = dropDownMenuExpanded,
                    onDismissRequest = {
                        dropDownMenuExpanded = false
                    },
                    // play around with these values
                    // to position the menu properly
                    offset = DpOffset(x = 10.dp, y = (-60).dp)
                ) {
                    // this is a column scope
                    // items are added vertically

                    DropdownMenuItem(modifier = Modifier.padding(end = 30.dp), onClick = {
                    }) {
                        Text(
                            text = "튜토리얼",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                        )
                    }

                    DropdownMenuItem(modifier = Modifier.padding(end = 30.dp), onClick = {
                    }) {
                        Text(
                            text = "설정",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                        )
                    }

                }
            }
        )
    }
//        TopAppBar(
//            modifier = modifier,
//            backgroundColor = MaterialTheme.colors.background,
//            elevation = 0.dp
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                IconButton(onClick = navigateUp) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.arrow_back_ios_new_fill1_wght300_grad0_opsz48),
//                        contentDescription = "뒤로 가기 버튼",
//                        tint = MaterialTheme.colors.onSurface
//                    )
//                }
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.h2,
//                    color = MaterialTheme.colors.onSurface,
//                    modifier = Modifier.padding(bottom = 3.dp)
//                )
//            }
//        }
//}
    else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            },
            modifier = modifier,
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
            actions = {
                IconButton(onClick = {
                    // show the drop down menu
                    dropDownMenuExpanded = true
                }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More Vert",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
                // drop down menu
                DropdownMenu(
                    expanded = dropDownMenuExpanded,
                    onDismissRequest = {
                        dropDownMenuExpanded = false
                    },
                    // play around with these values
                    // to position the menu properly
                    offset = DpOffset(x = 10.dp, y = (-60).dp)
                ) {
                    // this is a column scope
                    // items are added vertically

                    DropdownMenuItem(modifier = Modifier.padding(end = 30.dp), onClick = {
                    }) {
                        Text(
                            text = "튜토리얼",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                        )
                    }

                    DropdownMenuItem(modifier = Modifier.padding(end = 30.dp), onClick = {
                    }) {
                        Text(
                            text = "설정",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                        )
                    }

                }
            }
//            actions = {
//
//                IconButton(onClick = { /* doSomething() */ }) {
////                    Icon(Icons.Filled.MoreVert, contentDescription = "More Vert", tint = MaterialTheme.colors.onSurface),
//                    resour
//                }
//            }
        )
    }

//        TopAppBar(
//            modifier = modifier,
//            backgroundColor = MaterialTheme.colors.background,
//            elevation = 0.dp
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.h2,
//                    color = MaterialTheme.colors.onSurface,
//                    modifier = Modifier.padding(start = 16.dp, bottom = 3.dp)
//                )
//
//            }
//        }
//    }
}

@Preview
@Composable
fun AppbarPreview() {
    SignEzPrototypeTheme(darkTheme = false) {
        SignEzTopAppBar(title = "SignEz", canNavigateBack = false)
    }
}
