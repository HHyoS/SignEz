package com.kgh.basic1

import android.os.Bundle
import androidx.compose.ui.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kgh.basic1.ui.theme.Basic1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Basic1Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    BirthdayGreetingWithImage(getString(R.string.happy_birthday_text),"- from 강기한")
                }
            }
        }
    }
}

@Composable
fun BirthdayGreetingWithText(message:String, from:String) {
    Row {
        Column {
            Text(text = message, fontSize = 36.sp,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally).
                    padding(start=16.dp, end=16.dp))
            Text(text = from, fontSize = 24.sp)
        }
        Column {
            Text("Some text")
            Text("Some more text")
            Text("Last text")
        }
    }

}

@Composable
fun BirthdayGreetingWithImage(message:String, from:String) {
    val image = painterResource(id = R.drawable.ssafy)
    Box {
        Image(painter = image, contentDescription = null, modifier = Modifier.fillMaxHeight().fillMaxWidth(),
            contentScale = ContentScale.Crop)
        BirthdayGreetingWithText(message = message , from = from)
    }
}

@Preview(showBackground = true) // 진짜 배경을 보여줌.
@Composable
fun DefaultPreview() { // 미리보기 가능하게 해주는 녀석, 안에 정의한 녀석을 미리보기로 보여줌, 실제 app x;
    Basic1Theme {
        BirthdayGreetingWithImage("!", "!")
    }
}