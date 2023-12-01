package com.hostd.wedo.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestScreen() {

    Row(
        modifier = Modifier
            .drawBehind {

                val strokeWidth = 10f
                val y = size.height - strokeWidth / 2

                drawLine(
                    Color.Blue,
                    Offset(0f, y),
                    Offset(size.width, y),
                    strokeWidth
                )
            }){
        //....
    }

//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Box() {
//            Box(
//                Modifier.padding(start = 20.dp).border(
//                    width = 2.dp,
//                    color = Color.Blue
//                )
//            )
//            Icon(
//                modifier = Modifier.size(100.dp).border(2.dp, Color.Red, shape = RectangleShape),
//                imageVector = Icons.Default.Person,
//                contentDescription = "Person Icon",
//                tint = Color.Green
//            )
//        }
//    }
}