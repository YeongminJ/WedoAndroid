package com.hostd.wedo.ui

import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.hostd.wedo.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDialog(showDialog: MutableState<Boolean>, addAction: (String) -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TextBox() {
    //TODO 칼러 값을 가져오던지 주소를 가져오던지 처리
//    PreferenceUtils.get()
    //TODO Color 임시
    val color = Color.Red

    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        label = {
            Row {
                Text("Name")
                Icon(Icons.Default.Person, "그룹아이콘", tint = color)
            }
        },
        onValueChange = {
            text = it
        },
        placeholder = {
            Text("귀여운 사슴")
        }
    )
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        BasicTextField("", onValueChange = {
//
//        }, decorationBox = { innerTextField ->
//            Row(
//                modifier = Modifier.fillMaxWidth().border(2.dp, color, shape = RoundedCornerShape(16.dp)).padding(16.dp)
//            ) {
//                innerTextField()
//            }
//        })
//    }
}
