package com.hostd.wedo.components

import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


@Composable
fun SimpleProgressDialog(
    modifier: Modifier = Modifier,
    message: String? = null,
    isCircular: Boolean = true,
    shape: Shape = RoundedCornerShape(10.dp),
    backgroundColor: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        ProgressDialogUI(
            modifier,
            message,
            isCircular,
            shape,
            backgroundColor,
            elevation,
        )
    }
}

@Composable
fun ProgressDialogUI(
    modifier: Modifier,
    message: String?,
    isCircular: Boolean,
    shape: Shape,
    backgroundColor: CardColors,
    elevation: CardElevation
) {
    Card(
        shape = shape,
        colors = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            if (isCircular) {
                CircularProgressIndicator(strokeWidth = 1.dp)
            } else {
                LinearProgressIndicator()
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (message != null) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp)
            }
        }
    }
}


@Composable
@Preview
fun SimpleProgressDialogPreview() {
    SimpleProgressDialog(message = "Processing...") {}
}