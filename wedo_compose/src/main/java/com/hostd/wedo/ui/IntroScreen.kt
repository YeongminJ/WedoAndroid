package com.hostd.wedo.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hostd.wedo.MainActivity
import com.hostd.wedo.data.Constants
import com.hostd.wedo.gallery.GalleryPickerActivity
import com.hostd.wedo.util.PreferenceUtils
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar

//@Preview(showBackground = true)
@Composable
fun IntroScreen(navController: NavHostController = rememberNavController()) {
    val colorIndex = remember { mutableIntStateOf(0) }
    Column {
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
            text = "안녕하세요 Wedo 입니다. 먼저 메인 배경 색상을 골라볼까요?"
        )

        val colorList = listOf(
            Color(45, 197, 128),
            Color(56, 186, 192),
            Color(66, 169, 241),
            Color(137, 117, 109),
            Color(35, 35, 35),
            Color(226, 53, 55),
            Color(240, 85, 128),
            Color(249, 118, 111),
            Color(252, 185, 64),
            Color(170, 129, 211),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(12.dp),  //
            content = {
                items(colorList.size) {
                    RadioShape(colorList[it], it == colorIndex.value) {
                        //update
                        colorIndex.value = it
                    }
                }
            }
        )
        /*ColorPicker(
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
        )*/
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

@Composable
fun RadioShape(color: Color, isSelected: Boolean, clickListener: () -> Unit) {
    if (isSelected) {
        Box(modifier = Modifier.fillMaxWidth().padding(4.dp).aspectRatio(1f).clip(CircleShape).background(color).clickable {
            clickListener.invoke()
        }) {
            Icon(Icons.Default.Check, modifier = Modifier.fillMaxSize(), contentDescription = "Check", tint = Color.White)
        }
    } else {
        Box(modifier = Modifier.fillMaxWidth().padding(4.dp).aspectRatio(1f).clip(CircleShape).background(color).clickable {
            clickListener.invoke()
        })
    }
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

    val context = LocalContext.current

    Column {
        Button(
            onClick = {
                context.startActivity(Intent(context, GalleryPickerActivity::class.java))
                //On button press, launch the photo picker

//                launcher.launch(
//                    PickVisualMediaRequest(
//                    //Here we request only photos. Change this to .ImageAndVideo if you want videos too.
//                    //Or use .VideoOnly if you only want videos.
//                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
//                    )
//                )
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