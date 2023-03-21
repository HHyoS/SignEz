package com.kgh.signezprototype.fields

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }

    androidx.compose.foundation.text.BasicTextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onValueChange(it.text)
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.subtitle1.copy(fontSize = 20.sp),
        cursorBrush = SolidColor(Color.Black)
    )
}

@Composable
fun EditNumberField(
    head:String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    unit: String
) {
    Row(
        Modifier.fillMaxWidth(0.9f),
        horizontalArrangement = Arrangement.SpaceAround // set width to 70% of screen width
    ) {
        Text(
            text = head,
            modifier = Modifier
                .heightIn(min = 20.dp) // set min height to 48dp
                .padding(8.dp)
                .align(Alignment.CenterVertically) // center vertically with TextField
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .heightIn(min = 20.dp) // set min height to 48dp
                .fillMaxWidth(0.4f)
                .padding(3.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = NumberCommaVisualTransformation()
        )
        Text(
            text = unit,
            modifier = Modifier
                .heightIn(min = 20.dp) // set min height to 48dp
                .padding(8.dp)
                .align(Alignment.CenterVertically) // center vertically with TextField
        )
        //디자인 참고
        //https://developer.android.com/jetpack/compose/text?hl=ko#styling-textfield
        //https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#textfield
    }

}

@Composable
fun CustomTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder:String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(8.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.body1
            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}

class NumberCommaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val amount = text.text.getCommaNumber()

        return TransformedText(
            text = AnnotatedString(if (text.isEmpty()) "" else amount),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val commas = amount.count { it == ',' }
                    return offset + commas
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val commas = amount.count { it == ',' }

                    return when (offset) {
                        8, 7 -> offset - 2
                        6 -> if (commas == 1) 5 else 4
                        5 -> if (commas == 1) 4 else if (commas == 2) 3 else offset
                        4, 3 -> if (commas >= 1) offset - 1 else offset
                        2 -> if (commas == 2) 1 else offset
                        else -> offset
                    }
                }
            }
        )
    }
}

// Extension function to insert commas in a number string
fun String.getCommaNumber(): String {
    val regex = "(\\d)(?=(\\d{3})+\$)".toRegex()
    return replace(regex, "\$1,")
}
//https://dealicious-inc.github.io/2022/03/14/android-compose-apply.html 참고