package com.hostd.wedo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.hostd.wedo.MainActivity
import com.hostd.wedo.data.Constants
import com.hostd.wedo.util.PreferenceUtils

@Composable
fun IntroScreen(navController: NavHostController) {
    Column {
        Text(text = "튜토리얼 입니다.")
        Button(onClick = {
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