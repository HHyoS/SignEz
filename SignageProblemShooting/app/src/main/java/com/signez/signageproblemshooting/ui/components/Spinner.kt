package com.signez.signageproblemshooting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signez.signageproblemshooting.ui.theme.NotoSansKR
import com.signez.signageproblemshooting.ui.theme.SignEzPrototypeTheme

@Composable
fun LoadingSpinner(
    progress: Float = 0F
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            strokeWidth = 10.dp,
            color = MaterialTheme.colors.secondary
        )
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            strokeWidth = 10.dp,
            color = MaterialTheme.colors.primary
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "분석 중... (${kotlin.math.round(progress*100).toInt()}%)",
                style = TextStyle(
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                ),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(10.dp),
            )
            LinearProgressIndicator(
                modifier = Modifier.width(80.dp),
                color = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.secondary
            )
        }
    }
}

@Preview
@Composable
fun SpinnerPreview() {
    SignEzPrototypeTheme(darkTheme = false) {
        var inprogressFrame =  remember { mutableStateOf(0) }
        var totalFrame = remember { mutableStateOf(1) }
        totalFrame.value = 300
        var progressPercent = inprogressFrame.value/(totalFrame).value.toFloat()

        Column(modifier = Modifier.fillMaxSize()) {
            LoadingSpinner(progressPercent)
            Button(onClick = { inprogressFrame.value = inprogressFrame.value+1}){
                Text(text = inprogressFrame.value.toString())
            }
        }
    }
}


