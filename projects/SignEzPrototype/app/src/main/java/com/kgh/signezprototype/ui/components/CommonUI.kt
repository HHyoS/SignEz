package com.kgh.signezprototype.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kgh.signezprototype.fields.CustomTextInput
import com.kgh.signezprototype.fields.EditNumberField
import com.kgh.signezprototype.ui.theme.SignEzPrototypeTheme

/**
 *
 */
@Composable
fun FocusBlock(
    title: String,
    subtitle: String? = null,
    infols: List<String>? = null,
    buttonTitle: String?,
    isbuttonVisible: Boolean,
    buttonOnclickEvent: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = buttonOnclickEvent),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colors.surface
        )
    ) {
        Column(
            Modifier
//                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .padding(start = 18.dp, top = 8.dp)
                        .weight(0.4f)
                )
                if (buttonTitle != null) {
                    Spacer(
                        modifier = Modifier.weight(0.3f)
                    )
                    Column(
                        modifier = Modifier.weight(0.4f)
                    ) {
                        InFocusBlockButton(
                            title = buttonTitle,
                            isVisible = isbuttonVisible,
                            onClickEvent = buttonOnclickEvent
                        )
                    }
                }

            }

            if (subtitle!=null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            if(infols!=null){
            for (info in infols) {
                Text(
                    text = info,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 18.dp, top = 8.dp, bottom = 8.dp)
                )
            }
            }

//                Text(
//                    text = subtitle,
//                    style = MaterialTheme.typography.body1,
//                    color = MaterialTheme.colors.onBackground,
//                    modifier = Modifier.padding(start = 18.dp, bottom = 16.dp)
//                )

        }


    }
}

@Composable
fun WhiteButton(
    title: String,
    isUsable: Boolean,
    onClickEvent: () -> Unit
) {
    if (isUsable) {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant),
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
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    } else {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant),
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
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun InFocusBlockButton(
    title: String,
    isVisible: Boolean,
    onClickEvent: () -> Unit
) {
    if (isVisible) {
        androidx.compose.material3.Button(
            onClick = onClickEvent,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 10.dp, end = 15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
//                style = MaterialTheme.typography.button,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    } else if (!isVisible) {
        androidx.compose.material3.Button(
            onClick = {},
            shape = RoundedCornerShape(20.dp),
            enabled = false,
            border = BorderStroke(1.dp, MaterialTheme.colors.surface),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.surface,
                disabledContainerColor = MaterialTheme.colors.surface,
            ),
            modifier = Modifier
                .padding(top = 10.dp, end = 15.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Text(
                text = title,
//                style = MaterialTheme.typography.button,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.surface,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
}

@Composable
fun IntentButton(
    title: String,
    onClickEvent: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClickEvent,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.primaryVariant,
        ),
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
fun SignEzFloatingButton(
    onClickEvent: () -> Unit
) {
    FloatingActionButton(
        onClick = onClickEvent,
        shape = CircleShape,
        containerColor = androidx.compose.material.MaterialTheme.colors.primary,
    ) {
        androidx.compose.material.Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "추가",
            tint = androidx.compose.material.MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun TutorialStartButton(
    title: String,
    onClickEvent: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClickEvent,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        modifier = Modifier
            .padding(top = 10.dp, end = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}


@Composable
fun BottomSingleFlatButton(
    title: String,
    isUsable: Boolean,
    onClickEvent: () -> Unit
) {

    androidx.compose.material3.Button(
        onClick = onClickEvent,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.background,
        ),
        enabled = isUsable,
        modifier = Modifier
//            .padding(top = 10.dp, end = 15.dp)
            .fillMaxWidth()
//            .wrapContentHeight()
            .height(65.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.button,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
fun BottomDoubleFlatButton(
    leftTitle: String,
    rightTitle: String,
    isLeftUsable: Boolean,
    isRightUsable: Boolean,
    leftOnClickEvent: () -> Unit,
    rightOnClickEvent: () -> Unit,
) {
    Row() {
        androidx.compose.material3.Button(
            onClick = leftOnClickEvent,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.background,
            ),
            enabled = isLeftUsable,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f)
        ) {
            if (isLeftUsable) {
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text(
                    text = leftTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
        androidx.compose.material3.Button(
            onClick = rightOnClickEvent,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colors.background,
                disabledContainerColor = MaterialTheme.colors.background,
            ),
            enabled = isRightUsable,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f),
        ) {
            if (isRightUsable) {
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Text(
                    text = rightTitle,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }

}

//@Composable
//fun InputFocusBlock(
//    ,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = Modifier
//            .padding(top = 8.dp, bottom = 8.dp)
//            .fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colors.surface
//        )
//    ) {
//        Column(
//            Modifier
//        ) {
//            for (textField in textFields) {
//                Row {
//                    textField
//                }
//            }
//        }
//    }
//}

@Preview
@Composable
fun ComponentPreview() {
    SignEzPrototypeTheme(darkTheme = false) {
        Column() {
//            FocusBlock(
//                title = "사이니지 스펙",
//                subtitle = "정보 입력이 필요합니다",
//                buttonTitle = "입력",
//                isbuttonVisible = true,
//                buttonOnclickEvent = {},
//                modifier = Modifier
//            )
//            AnalyzeButton("영상 분석", false, onClickEvent = {})
//            AnalyzeButton("사진 분석", true, onClickEvent = {})
//            InFocusBlockButton(title = "입력", isVisible = true, onClickEvent = {})
//            IntentButton(title = "갤러리", onClickEvent = {})
//            SignEzFloatingButton(onClickEvent = {})
//            TutorialStartButton(title = "시작하기", onClickEvent = {})
//            BottomSingleFlatButton(title = "확인", true, onClickEvent = {})
//            BottomDoubleFlatButton(leftTitle = "취소",
//                rightTitle = "확인",
//                isLeftUsable = true,
//                isRightUsable = false,
//                leftOnClickEvent = {},
//                rightOnClickEvent = {})
//            InputFocusBlock(textFields =    )
        }
    }
}

