package com.jdi.wedo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity: ComponentActivity() {

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                bottomBar = {MainScreen()},
                floatingActionButton = {
                    FloatingActionButton(onClick = { /* ... */ }) {
                        Text("Hello")
                    }
                }

            ) { innerPadding->
                Box(modifier = Modifier.padding(innerPadding)) {
                    /* ... */
                }
            }
//            Scaffold(
//                bottomBar = {
//                    NavigationBar {
//                        items.forEachIndexed { index, item ->
//                            NavigationBarItem(
//                                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
//                                label = { Text(item) },
//                                selected = index == 0,
//                                onClick = {}
//                            )
//                        }
//                    }
//                },
//                content = { innerPadding ->
//                    LazyColumn(
//                        // consume insets as scaffold doesn't do it by default
//                        modifier = Modifier.fillMaxWidth(),
//                        contentPadding = innerPadding
//                    ) {
//                        items(count = 100) {
//                            Box(
//                                Modifier
//                                    .fillMaxWidth()
//                                    .height(50.dp)
//                                    .background(Color.Blue)
//                            )
//                        }
//                    }
//
//                }
//            )

        }
    }
}

val items = listOf("Wedo", "BucketList", "Group")

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    var selectedIndex = 0
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = { Text(item) },
                selected = index == selectedIndex,
                onClick = { selectedIndex = index }
            )
        }
    }
//    Scaffold(
//        bottomBar = bottomNavigation
//    )
}