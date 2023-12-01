package com.hostd.wedo.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hostd.wedo.MainActivity
import com.hostd.wedo.data.Constants
import com.hostd.wedo.util.PreferenceUtils
import com.skydoves.orchestra.colorpicker.AlphaSlideBar
import com.skydoves.orchestra.colorpicker.BrightnessSlideBar
import com.skydoves.orchestra.colorpicker.ColorPicker

//@Preview(showBackground = true)
@Composable
fun IntroScreen(navController: NavHostController = rememberNavController()) {
    Column {
        Text(text = "안녕하세요 Wedo 입니다. 먼저 메인 배경 색상을 골라볼까요?")

        ColorPicker(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            onColorListener = { envelope, _ ->
//                setSelectedColor(envelope)
            },
//            initialColor = Color.The,
            children = { colorPickerView ->
                Column(modifier = Modifier.padding(top = 32.dp)) {
                    Box(modifier = Modifier.padding(vertical = 6.dp)) {
                        AlphaSlideBar(
                            modifier = Modifier.fillMaxWidth().height(30.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            colorPickerView = colorPickerView
                        )
                    }
                    Box(modifier = Modifier.padding(vertical = 6.dp)) {
                        BrightnessSlideBar(
                            modifier = Modifier.fillMaxWidth().height(30.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            colorPickerView = colorPickerView
                        )
                    }
                }
            }
        )
        PhotoPickerDemoScreen()
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
            PreferenceUtils.set(Constants.TUTORIAL_COMPLETE, true)
            navController.navigate(MainActivity.Screen.MAIN_SCREEN.name) {
                //finish 와 같은 역할
                popUpTo(MainActivity.Screen.INTRO_SCREEN.name) { inclusive = true }
            }
        }) {
            Text(text = "튜토리얼 완료!")
        }
    }
}


@Preview
@Composable
fun testCompose() {
//    LazyVerticalGrid()
}

@Composable
fun PhotoPickerDemoScreen() {
    //The URI of the photo that the user has picked
    var photoUri: Uri? by remember { mutableStateOf(null) }

    //The launcher we will use for the PickVisualMedia contract.
    //When .launch()ed, this will display the photo picker.
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        //When the user has selected a photo, its URI is returned here
        photoUri = uris[0]
    }


    Column {
        Button(
            onClick = {
                //On button press, launch the photo picker
                launcher.launch(
                    PickVisualMediaRequest(
                    //Here we request only photos. Change this to .ImageAndVideo if you want videos too.
                    //Or use .VideoOnly if you only want videos.
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
                )
            }
        ) {
            Text("Select Photo")
        }

        if (photoUri != null) {
            //Use Coil to display the selected image
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = photoUri)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .border(6.0.dp, Color.Gray),
                contentScale = ContentScale.Crop
            )
        }
    }
}