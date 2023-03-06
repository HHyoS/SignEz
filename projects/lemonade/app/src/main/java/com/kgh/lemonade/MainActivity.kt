package com.kgh.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgh.lemonade.ui.theme.LemonadeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LemonadeTheme {
                LemonMain()
            }
        }
    }
}

@Composable
fun LemonMaking(modifier: Modifier = Modifier) {
    var result by remember {
        mutableStateOf(0)
    }
    var lemon_count by remember {
        mutableStateOf(3)
    }

    val textResource = when(result) {
        0 -> R.string.scene_1
        1 -> R.string.scene_2
        2 -> R.string.scene_3
        else -> R.string.scene_4
    }

    val imageResource = when(result) {
        0 -> R.drawable.lemon_tree
        1 -> R.drawable.lemon_squeeze
        2 -> R.drawable.lemon_drink
        else -> R.drawable.lemon_restart
    }

    var input by remember {
        mutableStateOf("")
    }

    Column(modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {

        TextField(value = input, onValueChange = {input = it} )
        Text(text = stringResource(textResource), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Image(painter = painterResource(imageResource),
            contentDescription = result.toString(),
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color(red = 105, green = 205, blue = 216, alpha = 100),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomEnd = 4.dp,
                        bottomStart = 4.dp // 4.dp 하나로 4개 전부 표현가능
                    )
                )
                .clickable(onClick = {
                    if (result == 0) {
                        lemon_count = (2..5).random()
                        result = (result + 1) % 4
                    } else if (result == 1) {
                        if (lemon_count > 0) {
                            lemon_count--
                        } else {
                            result = (result + 1) % 4
                        }
                    } else {
                        result = (result + 1) % 4
                    }
                })
        )

    }
}

@Preview(showBackground = true)
@Composable
fun LemonMain() {
    LemonadeTheme {
        LemonMaking(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center))
    }
}