package com.jdi.wedo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity: ComponentActivity() {

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mutableItems = remember { mutableStateListOf("AAAA","BBBB","CCCC") }
            Scaffold(
                bottomBar = {MainScreen()},
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        mutableItems.add("DDDD")
                    }) {

                    }
                }

            ) { innerPadding->

                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    /* ... */
                    items(mutableItems.size) {
                        TodoItem(mutableItems[it])
                    }
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

@Composable
fun TodoItem(itemStr: String) {
    Card(Modifier.padding(12.dp)
        .border(width = 4.dp, color = Color.Black)) {
        Box(contentAlignment = Alignment.Center) {
            Text(itemStr)
        }
    }
}

val navItems = listOf("Wedo", "BucketList", "Group")
@Preview(showBackground = true)
@Composable
fun MainScreen() {
    var selectedIndex = 0
    NavigationBar {
        navItems.forEachIndexed { index, item ->
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