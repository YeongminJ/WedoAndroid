package com.hostd.wedo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.hostd.wedo.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDialog(showDialog: MutableState<Boolean>, addAction: (String)-> Unit) {
    Modifier.padding()
    val textFieldString = remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = {
       showDialog.value = false
    }, title = {
        Text(text = "할 일 추가")
    }, confirmButton = {
        IconButton(onClick = {
            addAction(textFieldString.value)
            showDialog.value = false
        }) {
            Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "추가")
        }
    }, text = {
        TextField(value = textFieldString.value, onValueChange = {
            Log.e("JDI", "onValue : $it")
            textFieldString.value = it
        })
    }, properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true))
}