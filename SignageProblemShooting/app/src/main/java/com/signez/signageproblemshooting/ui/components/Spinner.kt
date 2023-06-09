package com.signez.signageproblemshooting.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signez.signageproblemshooting.ui.theme.NotoSansKR
import com.signez.signageproblemshooting.ui.theme.SignEzTheme

@Composable
fun LoadingSpinner(
    title: String = "분석 중...",
    progress: Float = 0F,
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            strokeWidth = 10.dp,
            color = MaterialTheme.colors.secondary
        )
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            strokeWidth = 10.dp,
            color = MaterialTheme.colors.primary
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title + " (${kotlin.math.round(progress * 100).toInt()}%)",
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
    SignEzTheme(darkTheme = false) {
        val inProgressFrame = remember { mutableStateOf(0) }
        val totalFrame = remember { mutableStateOf(300) }
        val progressPercent = inProgressFrame.value / (totalFrame).value.toFloat()

        Column(modifier = Modifier.fillMaxSize()) {
            LoadingSpinner("분석 중", progressPercent)
            Button(onClick = { inProgressFrame.value = inProgressFrame.value + 1 }) {
                Text(text = inProgressFrame.value.toString())
            }
        }
    }
}


