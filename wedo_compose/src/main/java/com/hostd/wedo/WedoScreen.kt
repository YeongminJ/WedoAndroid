package com.hostd.wedo

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hostd.wedo.ui.IntroScreen
import com.hostd.wedo.ui.MainScreen
import com.hostd.wedo.ui.TodoDialog

@ExperimentalMaterial3Api
@Composable
fun WedoScreen(
    viewModel: WedoViewModel,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainActivity.Screen.INTRO_SCREEN.name
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding()
    ) {
        composable(MainActivity.Screen.INTRO_SCREEN.name) {
            IntroScreen(navController)
        }
        composable(MainActivity.Screen.MAIN_SCREEN.name) {
            MainScreen(viewModel)
        }
    }
}