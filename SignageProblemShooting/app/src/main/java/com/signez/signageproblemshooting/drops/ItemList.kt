package com.signez.signageproblemshooting.drops

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectBox() {
    val items = listOf("A", "B", "C")
    var selectedItem by remember { mutableStateOf(items[0]) }
    var openList by remember { mutableStateOf(false) }

    Column {
        Row() {
            Text("Select a module's model :",modifier = Modifier.padding(16.dp))
            Button(
                onClick = { openList = true},
                modifier = Modifier.padding(16.dp)

            ) {
                Text("목록")
            }
        }

        DropdownMenu(
            expanded = openList,
            onDismissRequest = {
                openList = false
            }
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    selectedItem.let {
                        Handler(Looper.getMainLooper()).postDelayed({
                            openList = false
                        }, 200)
                        selectedItem = item
                    }
                }) {
                    Text(text = item)
                }
            }
        }
        Text("Selected module's model: $selectedItem")
    }
}