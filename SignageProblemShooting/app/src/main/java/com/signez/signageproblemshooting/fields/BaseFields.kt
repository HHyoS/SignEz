package com.signez.signageproblemshooting.fields

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun EditNumberField(
    head: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround // set width to 70% of screen width
    ) {
        Text(
            text = head,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .heightIn(min = 20.dp) // set min height to 48dp
                .padding(start = 30.dp)
                .align(Alignment.Bottom) // center vertically with TextField
                .weight(0.3f)
        )
        Spacer(modifier = Modifier.weight(0.15f))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .heightIn(min = 20.dp) // set min height to 48dp
                .weight(0.35f),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = NumberCommaVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                backgroundColor = MaterialTheme.colors.surface
            )
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .heightIn(min = 20.dp) // set min height to 48dp
                .padding(start = 15.dp)
                .align(Alignment.Bottom) // center vertically with TextField
                .weight(0.2f)
        )
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
    placeholder: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.onBackground
            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onSurface,
            backgroundColor = MaterialTheme.colors.surface,
        )
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

fun String.getCommaNumber(): String {
    val regex = "(\\d)(?=(\\d{3})+\$)".toRegex()
    return replace(regex, "\$1,")
}