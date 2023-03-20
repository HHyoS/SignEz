package com.kgh.signezprototype.ui.components

import android.widget.Button
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgh.signezprototype.SignEzTopAppBar
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme

/**
 *
 */
@Composable
fun FocusBlock(
    title: String,
    subtitle: String,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
//            .background(MaterialTheme.colors.background)
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxWidth()
        ) {
            Row() {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }
            Row() {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AnalyzeButton(
    title: String,
    isUsable: Boolean,
    onClickEvent: () -> Unit
) {
    if (isUsable) {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colors.onSurface),
            enabled = isUsable,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface,
            )
        }
    } else {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colors.onBackground),
            enabled = isUsable,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onBackground,
            )
        }
    }

}


@Preview
@Composable
fun AppbarPreview() {
    SignEzPrototypeTheme(darkTheme = false) {
        Column() {
            FocusBlock(title = "사이니지 스펙", subtitle = "정보 입력이 필요합니다", modifier = Modifier)
            AnalyzeButton("영상 분석", false, onClickEvent = {})
        }
    }
}