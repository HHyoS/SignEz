package com.kgh.signezprototype.ui.signage

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.data.entities.Signage
import com.kgh.signezprototype.ui.inputs.PictureAnalysis
import com.kgh.signezprototype.ui.inputs.PictureScreenDestination
import com.kgh.signezprototype.ui.navigation.NavigationDestination
import com.kgh.signezprototype.ui.theme.OneBGBlue
import com.kgh.signezprototype.ui.theme.OneBGDarkGrey
import com.kgh.signezprototype.ui.theme.OneBGGrey

object AddSignageDestination : NavigationDestination {
    override val route = "AddSignage"
    override val titleRes = "Add Signage"
}


@Composable
fun AddSignageScreen(modifier:Modifier = Modifier) {

    var bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val focusManager = LocalFocusManager.current

    androidx.compose.material.Scaffold(
        modifier = Modifier
            .clickable(onClick = { focusManager.clearFocus() })
            .background(OneBGGrey),
        topBar = {
            SignEzTopAppBar(
                title = "새 사이니지 추가",
                canNavigateBack = true
            )
        },
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(modifier = modifier.padding(15.dp))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "rep Image",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.3f)
                        .clip(RoundedCornerShape(15.dp))
                        .background(color = OneBGDarkGrey)
                )
//                PictureAnalysis()
            }

        }
    }
}